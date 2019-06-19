package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

public class ListBuilder extends Builder {
    @Override
    public void addParameters(HttpServletRequest request, Options options,
            Element params, AccessLevel lvl) throws ProtocolException {
        super.addParameters(request, options, params, lvl);

        String identifierHead = options.getIdentifierHead();
        appendParamNode(params, IDENTIFIER_HEAD, identifierHead);

        appendParamNode(params, DATESTAMP, DATESTAMP_VALUE);
        appendParamNode(params, EXPIRATION_DATETIME, DateUtil.getExpirationDateTime());

        String resumptionToken = request.getParameter(RESUMPTION_TOKEN);
        if (resumptionToken != null) {
            Map param = request.getParameterMap();
            if (param.size() > 2) { // verb + resumption token
                throw new ProtocolException(Errors.extraArgument, null);
            }

            processResumptionToken(params, options, resumptionToken);
        } else {
            String metadataPrefix = request.getParameter(METADATA_PREFIX);
            ensureValid(metadataPrefix);

            int limit = getRowLimit(options);
            appendParamNode(params, ROWS, Integer.toString(limit));
            appendParamNode(params, METADATA_PREFIX, metadataPrefix);
            appendParamNode(params, TOKEN_DAY,
                    DateUtil.getExportDate(options.getDatingFile()));

            String set = request.getParameter(SET);
            if (set != null) {
                appendParamNode(params, SET, set);
                appendParamNode(params, SET_SCOPE, "t");
            }

            String from = request.getParameter(FROM);
            if (from != null) {
                from = DateUtil.reformatDate(from);
                appendParamNode(params, FROM, from);
            }

            String until = request.getParameter(UNTIL);
            if (until != null) {
                until = DateUtil.reformatDate(until);
                appendParamNode(params, UNTIL, until);
            }
        }
    }

    void processResumptionToken(Element params, Options options, String token)
            throws ProtocolException {
        Builder.appendParamNode(params, RESUMPTION_TOKEN, token);

        ResumptionToken rt = ResumptionToken.parseToken(token);
        String prefix = rt.getMetadataPrefix();
        ensureValid(prefix);
        appendParamNode(params, METADATA_PREFIX, prefix);
        appendParamNode(params, TOKEN_DAY, rt.getDay());
        appendIntParam(params, START, rt.getGlobalOffset());
        appendIntParam(params, OFFSET, rt.getOffset());

        int limit = Math.min(getRowLimit(options), rt.getLimit());
        appendIntParam(params, ROWS, limit);

        String from = rt.getFrom();
        if (from != null) {
            appendParamNode(params, FROM, from);
        }

        String until = rt.getUntil();
        if (until != null) {
            appendParamNode(params, UNTIL, until);
        }

        appendParamNode(params, SET_SCOPE, rt.getSetScope() ? "t" : "f");
    }

    static int parseIntTokenItem(String raw) throws ProtocolException {
        try {
            int n = Integer.parseInt(raw);
            if (n < 0) {
                throw new ProtocolException(Errors.badResumptionToken, null);
            }

            return n;
        } catch (NumberFormatException ex) {
            throw new ProtocolException(Errors.badResumptionToken, null);
        }
    }
}
