package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

public class RecordBuilder extends Builder {
    @Override
    public int getRowLimit(Options options) {
        return 1;
    }

    @Override
    public void addParameters(HttpServletRequest request, Options options,
            Element params, AccessLevel lvl) throws ProtocolException {
        super.addParameters(request, options, params, lvl);

        String identifierHead = options.getIdentifierHead();
        appendParamNode(params, IDENTIFIER_HEAD, identifierHead);

        String identifier = request.getParameter(IDENTIFIER);
        if (identifier == null) {
            throw new ProtocolException(Errors.badArgument, IDENTIFIER);
        }

        appendParamNode(params, IDENTIFIER, identifier);

        int limit = getRowLimit(options);
        appendParamNode(params, ROWS, Integer.toString(limit));

        String metadataPrefix = request.getParameter(METADATA_PREFIX);
        ensureValid(metadataPrefix);
        appendParamNode(params, METADATA_PREFIX, metadataPrefix);

        Map param = request.getParameterMap();
        if (param.size() > 3) { // verb, identifier, metadataPrefix
            throw new ProtocolException(Errors.extraArgument, null);
        }

        appendParamNode(params, DATESTAMP, DATESTAMP_VALUE);
    }
}
