package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

/**
 * Builder for ListMetadataFormats.
 */
public class MetadataBuilder extends Builder {
    @Override
    public void addParameters(HttpServletRequest request, Options options,
            Element params, AccessLevel lvl) throws ProtocolException {
        super.addParameters(request, options, params, lvl);

        String identifierHead = options.getIdentifierHead();
        appendParamNode(params, IDENTIFIER_HEAD, identifierHead);

        String identifier = request.getParameter(IDENTIFIER);
        if (identifier != null) {
            appendParamNode(params, IDENTIFIER, identifier);
        }

        int argCount = (identifier == null) ? 1 : 2;
        Map param = request.getParameterMap();
        if (param.size() > argCount) {
            throw new ProtocolException(Errors.extraArgument, null);
        }
    }
}
