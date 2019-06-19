package cz.ssc.dapro;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

@WebServlet(name = "DataProvider", urlPatterns = {"/oai"})
public class DataProvider extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DataProvider.class.getName());

    private final XsltHandler xsltHandler;
    
    private Options options;

    public DataProvider() {
        xsltHandler = new XsltHandler();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGGER.log(Level.FINE, "processing GET");
        process(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGGER.log(Level.FINE, "processing POST");
        process(request, response);
    }
    
    private void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        synchronized (this) {
            if (options == null) {
                options = new Options(getServletContext());
            }
        }

        try {
            xsltHandler.process(request, response, options);
        } catch (TransformerException ex) {
            LOGGER.log(Level.SEVERE, "XSLT error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletOutputStream out = response.getOutputStream();
            out.print("Internal transformation error");
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "Malformed URL error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletOutputStream out = response.getOutputStream();
            out.print("Delegation configuration error");
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, "URL syntax error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletOutputStream out = response.getOutputStream();
            out.print("Delegation configuration error");
        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, "XML error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletOutputStream out = response.getOutputStream();
            out.print("Internal XML configuration error");
        } catch (SAXException ex) {
            LOGGER.log(Level.SEVERE, "SOLR XML error", ex);
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            ServletOutputStream out = response.getOutputStream();
            out.print("Cannot parse SOLR response");
        } catch (ConnectException ex) {
            LOGGER.log(Level.SEVERE, "SOLR connection error", ex);
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            ServletOutputStream out = response.getOutputStream();
            out.print("SOLR not available");
        } catch (BadAuthException ex) {
            LOGGER.log(Level.SEVERE, "Amcr-pass bad auth", ex);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletOutputStream out = response.getOutputStream();
            out.print("Bad auth");
        }
    }
}
