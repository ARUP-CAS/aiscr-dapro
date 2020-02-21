package cz.ssc.dapro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class BulkFilter extends XMLFilterImpl {
    private static final Logger LOGGER = Logger.getLogger(BulkFilter.class.getName());

    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private static final Set<String> dateNames = initDateNames();

    private final FilterSpec filterSpec;

    private final SimpleDateFormat dayFormat;

    private int depth;

    private int rows;

    private boolean finished;

    private final ArrayList<IDeferredEvent> queue;

    private StringBuffer rawIdentifier;

    private String identifier;

    private boolean inRdfDate;

    private StringBuffer rawDate;

    private Date curDate;

    static Set<String> initDateNames() {
        HashSet<String> names = new HashSet<>();
        names.add("datum_zapisu");
        names.add("datum_prihlaseni");
        names.add("datum_zapisu_zahajeni");
        names.add("datum_zapisu_ukonceni");
        names.add("datum_navrhu_archivace");
        names.add("datum_archivace");
        names.add("datum_navrzeni_zruseni");
        names.add("datum_zruseni");
        names.add("datum_autorizace");
        names.add("datum_archivace_zaa");
        names.add("datum_odlozeni_nz");
        names.add("datum_vraceni_zaa");
        names.add("datum_podani_nz");
        names.add("datum_zamitnuti");
        names.add("datum");
        names.add("datum_vlozeni");
        names.add("vytvoreno");
        names.add("datum_vymezeni");
        names.add("datum_potvrzeni");
        names.add("datum_nalezu");
        return names;
    }

    public BulkFilter(FilterSpec filterSpec) {
        this.filterSpec = filterSpec;
        dayFormat = DateUtil.initDayFormat();
        depth = 0;
        rows = 0;
        finished = false;
        queue = new ArrayList<>();
        rawIdentifier = null;
        identifier = null;
        inRdfDate = false;
        rawDate = null;
        curDate = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException {
        if (++depth == 1) {
            super.startElement(uri, localName, qName, atts);
        } else {
            if (finished) {
                return;
            }

            if (depth == 2) {
                String about = atts.getValue(RDF, "about");
                if (about != null) {
                    int pos = about.lastIndexOf("/");
                    identifier = about.substring(pos + 1);
                }
            } else if ((depth == 3) &&
                    ("ident_cely".equals(localName) || "filepath".equals(localName))) {
                rawIdentifier = new StringBuffer();
            } else if ((depth == 3) && dateNames.contains(localName)) {
                rawDate = new StringBuffer();
            } else if ("E50_Date".equals(localName)) {
                String about = atts.getValue(RDF, "about");
                if (about != null) {
                    int fragPos = about.lastIndexOf("#");
                    String fragName = about.substring(fragPos + 1);
                    if (dateNames.contains(fragName)) {
                        inRdfDate = true;
                    }
                }
            } else if (inRdfDate && "label".equals(localName)) {
                rawDate = new StringBuffer();
            }

            queue.add(new StartEvent(uri, localName, qName, atts));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException {
        if (rawIdentifier != null) {
            identifier = rawIdentifier.toString();
            rawIdentifier = null;
        }

        if (rawDate != null) {
            String raw = rawDate.toString();
            Date d = null;
            try {
                d = dayFormat.parse(raw);
            } catch (ParseException ex) {
                String msg = String.format("Invalid date %s.", raw);
                LOGGER.warning(msg);
            }

            if ((d != null) && ((curDate == null) || curDate.before(d))) {
                curDate = d;
            }

            rawDate = null;
            inRdfDate = false;
        }

        if (--depth == 1) {
            if (finished) {
                return;
            }

            if (identifier == null) {
                LOGGER.warning("Skipping record with no identifier.");
            } else if (curDate == null) {
                LOGGER.warning("Skipping record with no date.");
            } else if (filterSpec.accept(identifier, curDate)) {
                if (rows >= filterSpec.getOffset()) {
                    if (rows <= (filterSpec.getOffset() + filterSpec.getLimit())) {
                        ContentHandler ch = getContentHandler();
                        for (IDeferredEvent event : queue) {
                            event.execute(ch);
                        }

                        super.endElement(uri, localName, qName);
                    } else {
                        // it would be more efficient to throw a
                        // custom exception here, but then we wouldn't
                        // get parse result and have to build the DOM
                        // ourselves...
                        finished = true;
                    }
                }

                ++rows;
            }

            identifier = null;
            curDate = null;
            queue.clear();
        } else if (depth == 0) {
            super.endElement(uri, localName, qName);
        } else {
            if (!finished) {
                queue.add(new EndEvent(uri, localName, qName));
            }
        }
    }

    @Override
    public void characters(char chars[], int start, int length)
        throws SAXException {
        if (finished) {
            return;
        }

        if (rawIdentifier != null) {
            rawIdentifier.append(chars, start, length);
        }

        if (rawDate != null) {
            rawDate.append(chars, start, length);
        }

        if (hasText(chars, start, length)) {
            queue.add(new TextEvent(chars, start, length));
        }
    }

    private static boolean hasText(char chars[], int start, int length) {
        for (int i = 0; i < length; ++i) {
            if (!Character.isWhitespace(chars[start + i])) {
                return true;
            }
        }

        return false;
    }
}
