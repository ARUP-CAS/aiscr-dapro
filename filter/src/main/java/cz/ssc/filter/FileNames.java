package cz.ssc.filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Juraj
 */
public class FileNames {

    /**
     * Method will return list of pairs where first there is a keyword(tag) and second is its input xml filename.
     * @param fileBase
     * @param dated
     * @param internal
     * @return 
     */
    public static List<Pair<String, String>> getAllFileInfo(String fileBase, boolean dated, boolean internal){
        List<Pair<String, String>> fileInfos = new ArrayList<Pair<String, String>>();
        
        for(String s: Constants.KEYWORDS){
            fileInfos.add(new Pair<>(s, makeHead(fileBase, dated, internal, s)));
        }
        
        return fileInfos;
    }
    
    
    public static String makeHead(String fileBase, boolean dated, boolean internal, String entityName) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileBase);

        if (internal) {
            sb.append("export");
        } else {
            sb.append("rdf");
        }

        sb.append('_');

        if (dated) {
            sb.append((new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
            sb.append('_');
        }

        sb.append(entityName);
        sb.append("_amcr");
        sb.append(".xml");
        return sb.toString();
    }
    
    public static String getDatedOutputName(String filebase, String entityName){
        return makeHead(filebase, true, false, entityName);
    }
    
    public static String getMappingFileName(String filebase, String entityName){
        return filebase+"/AMCR_to_CIDOC_X3ML_"+entityName+".xml";
    }

}
