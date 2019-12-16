package cz.ssc.dapro;

import cz.ssc.filter.Constants;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public class RequestData {
    private final String identCely;

    private final ResumptionToken resumptionToken;

    private final String prefix;

    private final String setClass;

    private final String recordClass;

    private final int offset;

    private final int limit;

    private final String from;
    
    private final String until;

    private final String exportDate;

    public RequestData(HttpServletRequest request, Options options) throws ProtocolException {
        String identifier = request.getParameter(Builder.IDENTIFIER);
        if (identifier != null) {
            int slash = identifier.lastIndexOf("/");
            identCely = identifier.substring(slash + 1);
        } else {
            identCely = null;
        }

        String token = request.getParameter(Builder.RESUMPTION_TOKEN);
        if (token != null) {
            resumptionToken = ResumptionToken.parseToken(token);
        } else {
            resumptionToken = null;
        }

        if (resumptionToken != null) {
            prefix = resumptionToken.getMetadataPrefix();
            setClass = resumptionToken.getSetScope() ? resumptionToken.getRecordClass() : null;
            recordClass = resumptionToken.getRecordClass();
            offset = resumptionToken.getOffset();
            limit = resumptionToken.getLimit();
            from = resumptionToken.getFrom();
            until = resumptionToken.getUntil();
            exportDate = resumptionToken.getDay();
        } else {
            prefix = request.getParameter(Builder.METADATA_PREFIX);

            setClass = request.getParameter(Builder.SET);
            if ((setClass != null) && !ResumptionToken.isValidClass(setClass)) {
                throw new ProtocolException(Errors.illegalArgumentValue, Builder.SET);
            }

            recordClass = ResumptionToken.getFirstClass();

            offset = 0;
            limit = options.getPageRows();

            String f = request.getParameter(Builder.FROM);
            if (f != null) {
                if (f.length() != 10) { // day granularity only
                    throw new ProtocolException(Errors.illegalArgumentValue,
                            Builder.FROM);
                }

                from = DateUtil.reformatDate(f);
            } else {
                from = null;
            }

            String u = request.getParameter(Builder.UNTIL);
            if (u != null) {
                if (u.length() != 10) {
                    throw new ProtocolException(Errors.illegalArgumentValue,
                            Builder.UNTIL);
                }

                until = DateUtil.reformatDate(u);
            } else {
                until = null;
            }

            exportDate = DateUtil.getExportDate(options.getDatingFile());;
        }
    }
    
    public boolean hasResumptionToken() {
        return resumptionToken != null;
    }

    public boolean hasInternalFormat() {
        return !ResumptionToken.OAI_RDF.equals(prefix);
    }

    public boolean hasDublinCoreFormat() {
        return ResumptionToken.OAI_DC.equals(prefix);
    }

    public String getIdentCely() {
        return identCely;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getIndividualClass() {
        if (identCely == null) {
            return null;
        }

        if (identCely.matches("(?:X-)?[CM]-[0-9]{9}")) {
            return Constants.PROJECT;
        } else if (identCely.matches("(?:X-)?[CM]-[0-9]{7,9}[A-Z]")) {
            return Constants.EVENT;
        } else if (identCely.matches("(?:X-)?[CM]-[LNK][0-9]{7}")) {
            return Constants.SITE;
        } else if (identCely.matches("[CM]-LET-[0-9]{5}")) {
            return Constants.FLIGHT;
        } else if (identCely.matches("(?:X-)?[CM]-[LNK]?[0-9]{7,9}[A-Z]?-D[0-9]{2}")) {
            return Constants.DOC_UNIT;
        } else if (identCely.matches("(?:X-)?BIB-[0-9]{7}")) {
            return Constants.EXT_SOURCE;
        } else if (identCely.matches("[PN]-[0-9]{4}-[0-9]{6}")) {
            return Constants.PIAN;
        } else if (identCely.matches("[0-9]+_.+")) { // filepath
            return Constants.FILE;
        } else if (identCely.matches("[CM]-[0-9]{9}-N[0-9]{5}")) {
            return Constants.PAS;
        } else if (identCely.matches("(?:X-)?[CM]-[A-Z]{4}[0-9]{2}-[0-9]{4}")) {
            return Constants.ADB;
        } else {
            return Constants.DOCUMENT;
        }
    }

    public String getSetClass() {
        return setClass;
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

    public String getExportDate() {
        return exportDate;
    }
}
