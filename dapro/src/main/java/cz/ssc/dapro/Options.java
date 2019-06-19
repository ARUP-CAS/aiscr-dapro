package cz.ssc.dapro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class Options {
    private static final Logger LOGGER = Logger.getLogger(Options.class.getName());

    private final String passUrl;

    private final String fileBase;

    private final boolean datedSource;

    private final String datingFile;

    private final int pageRows;

    private final String identifierHead;

    private final String publicHead;

    public Options(ServletContext context) throws IOException {
        String confDir = context.getInitParameter("confdir");
        if ((confDir == null) || confDir.isEmpty()) {
            confDir = "/etc/tomcat7";
        }

        LOGGER.fine(String.format("conf dir = %s", confDir));
        Path confPath = Paths.get(confDir, "amcr-dapro.json");
        File confFile = new File(confPath.toString());
        String confStr = FileUtils.readFileToString(confFile, "UTF-8");
        JSONObject json = new JSONObject(confStr);

        passUrl = json.getString("passurl");
        fileBase = json.getString("filebase");
        datedSource = json.optBoolean("datedsource", true);
        datingFile = json.optString("datingfile");
        pageRows = json.optInt("pagerows", 100);
        identifierHead = json.getString("identifierhead");

        String ph = json.optString("publichead");
        publicHead = (ph != null) ? ph : "https://api.aiscr.cz/";
    }

    public String getPassUrl() {
        return passUrl;
    }

    public String getFileBase() {
        return fileBase;
    }

    public boolean getDatedSource() {
        return datedSource;
    }

    public String getDatingFile() {
        return datingFile;
    }

    public int getPageRows() {
        return pageRows;
    }

    public String getIdentifierHead() {
        return identifierHead;
    }

    public String getPublicHead() {
        return publicHead;
    }

    public String getPublicUrl() {
        return publicHead + "dapro/oai";
    }

    public String getSchemaHead() {
        return publicHead + "dapro/media/";
    }
}
