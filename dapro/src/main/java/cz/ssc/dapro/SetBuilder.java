package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

public class SetBuilder extends Builder {
    @Override
    public boolean useSourceUrl() {
        return false;
    }

    @Override
    public void addParameters(HttpServletRequest request, Options options,
            Element params, AccessLevel lvl) throws ProtocolException {
        super.addParameters(request, options, params, lvl);
        
        String resumptionToken = request.getParameter(RESUMPTION_TOKEN);
        if (resumptionToken != null) {
            // sets are always returned all at once - there isn't that many
            throw new ProtocolException(Errors.badResumptionToken, null);
        }

        Map param = request.getParameterMap();
        if (param.size() > 1) { // verb
            throw new ProtocolException(Errors.extraArgument, null);
        }
    }
}
