package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

/**
 * Builder for Identify.
 *
 * The verb handling doesn't really need any source info.
 */
public class GreetingBuilder extends Builder {
    @Override
    public boolean useSourceUrl() {
        return false;
    }

    @Override
    public void addParameters(HttpServletRequest request, Options options,
            Element params, AccessLevel lvl) throws ProtocolException {
        super.addParameters(request, options, params, lvl);

        Map param = request.getParameterMap();
        if (param.size() > 1) { // verb
            throw new ProtocolException(Errors.extraArgument, null);
        }
    }
}
