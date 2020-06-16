package cz.ssc.dapro;

import cz.ssc.filter.Constants;

public class ResumptionToken {
    public static final String OAI_DC = "oai_dc";

    public static final String OAI_RDF = "oai_rdf";

    public static final String OAI_AMCR = "oai_amcr";

    public static final String[] PROCESSION = { Constants.PROJECT,
        Constants.EVENT, Constants.SITE, Constants.FLIGHT, Constants.DOC_UNIT,
        Constants.DOCUMENT, Constants.EXT_SOURCE, Constants.PIAN,
        Constants.FILE, Constants.ADB, Constants.PAS};

    private final String day;

    private final String metadataPrefix;

    private final int globalOffset;

    private final String recordClass;

    private final int offset;

    private final int limit;

    private final String from;

    private final String until;

    private final boolean setScope;

    public ResumptionToken(String day, String metadataPrefix, int globalOffset,
            String recordClass, int offset, int limit, String from,
            String until, boolean setScope) throws ProtocolException {
        if ((globalOffset < 0) || (globalOffset < offset) || (limit <= 0)) {
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        this.day = day;
        this.metadataPrefix = metadataPrefix;
        this.globalOffset = globalOffset;
        this.recordClass = recordClass;
        this.offset = offset;
        this.limit = limit;
        this.from = from;
        this.until = until;
        this.setScope = setScope;
    }

    public String getDay() {
        return day;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public int getGlobalOffset() {
        return globalOffset;
    }

    public String getRecordClass() {
        return recordClass;
    }
    
    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public String getFrom() {
        return from;
    }

    public String getUntil() {
        return until;
    }

    public boolean getSetScope() {
        return setScope;
    }

    public static ResumptionToken parseToken(String token)
            throws ProtocolException {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }

        String tokens[] = token.split("\\.");
        if (tokens.length != 9) {
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        String day;
        try {
            day = DateUtil.reformatDate(tokens[0]);
        } catch (ProtocolException ex) {
            // reformatDate throws Errors.badArgument, which isn't
            // really correct here
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        if (!isValidPrefix(tokens[1])) {
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        if (!isValidClass(tokens[3])) {
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        String from = parseOptDate(tokens[6]);
        String until = parseOptDate(tokens[7]);

        if (!"t".equals(tokens[8]) && !"f".equals(tokens[8])) {
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        try {
            return new ResumptionToken(day, tokens[1], Integer.parseInt(tokens[2]),
                    tokens[3], Integer.parseInt(tokens[4]),
                    Integer.parseInt(tokens[5]), from, until,
                    "t".equals(tokens[8]));
        } catch (NumberFormatException ex) {
            throw new ProtocolException(Errors.badResumptionToken, null);            
        }
    }

    static String parseOptDate(String raw) throws ProtocolException {
        if ((raw == null) || raw.isEmpty()) {
            return null;
        }

        return DateUtil.reformatDate(raw);
    }

    public static boolean isValidClass(String recordClass) {
        for (String rc : PROCESSION) {
            if (rc.equals(recordClass)) {
                return true;
            }
        }

        return false;
    }

    public static String getFirstClass() {
        return PROCESSION[0];
    }

    public static String getNextClass(String recordClass) {
        for (int i = 0; i < PROCESSION.length - 1; ++i) {
            if (PROCESSION[i].equals(recordClass)) {
                return PROCESSION[i + 1];
            }
        }
        
        return null;
    }
    
    public static boolean isValidPrefix(String prefix) {
        return OAI_DC.equals(prefix) || OAI_RDF.equals(prefix) || OAI_AMCR.equals(prefix);
    }
}
