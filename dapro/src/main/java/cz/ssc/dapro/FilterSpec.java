package cz.ssc.dapro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

public class FilterSpec {
    private String identCely; // can also be filepath

    private int offset;

    private int limit;

    private Date from;

    private Date until;

    public FilterSpec(URIBuilder builder) throws ProtocolException {
        List<NameValuePair> params = builder.getQueryParams();
        Iterator<NameValuePair> iter = params.iterator();
        SimpleDateFormat dayFormat = null;
        while (iter.hasNext()) {
            NameValuePair pair = iter.next();
            if (Builder.IDENTIFIER.equals(pair.getName())) {
                identCely = pair.getValue();
            } else if (Builder.OFFSET.equals(pair.getName())) {
                offset = Integer.parseInt(pair.getValue());
            } else if (Builder.ROWS.equals(pair.getName())) {
                limit = Integer.parseInt(pair.getValue());
            } else if (Builder.FROM.equals(pair.getName())) {
                if (dayFormat == null) {
                    dayFormat = DateUtil.initDayFormat();
                }

                from = parseOptDate(dayFormat, pair.getValue());
            } else if (Builder.UNTIL.equals(pair.getName())) {
                if (dayFormat == null) {
                    dayFormat = DateUtil.initDayFormat();
                }

                until = parseOptDate(dayFormat, pair.getValue());
            }
        }
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public boolean accept(String ident, Date date) {
        if (ident == null) {
            throw new IllegalArgumentException();
        }

        if (date == null) {
            throw new IllegalArgumentException();
        }

        if (identCely != null) {
            return identCely.equals(ident);
        }

        if ((from != null) && from.after(date)) {
            return false;
        }

        if ((until != null) && until.before(date)) {
            return false;
        }
        
        return true;
    }

    static Date parseOptDate(SimpleDateFormat dayFormat, String rawDate) throws ProtocolException {
        if (rawDate == null) {
            return null;
        } else {
            try {
                return dayFormat.parse(rawDate);
            } catch (ParseException ex) {
                throw new ProtocolException(Errors.badArgument, null);
            }
        }
    }
}
