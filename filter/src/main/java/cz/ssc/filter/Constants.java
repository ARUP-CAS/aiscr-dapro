package cz.ssc.filter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juraj
 */
public final class Constants {
    
    // Keywords
    public static final String PROJECT = "projekt";
    public static final String EVENT = "akce";
    public static final String SITE = "lokalita";
    public static final String DOC_UNIT = "dok_jednotka";
    public static final String DOCUMENT = "dokument";
    public static final String FILE = "soubor";
    public static final String PIAN = "pian";
    public static final String EXT_SOURCE = "ext_zdroj";
    public static final String FLIGHT = "let";
    public static final String PAS = "samostatny_nalez";
    public static final String ADB = "adb";
    public static final List<String> KEYWORDS = new ArrayList<String>();
    
    static {
        KEYWORDS.add(PROJECT);
        KEYWORDS.add(EVENT);
        KEYWORDS.add(SITE);
        KEYWORDS.add(DOC_UNIT);
        KEYWORDS.add(DOCUMENT);
        KEYWORDS.add(FILE);
        KEYWORDS.add(PIAN);
        KEYWORDS.add(EXT_SOURCE);
        KEYWORDS.add(FLIGHT);
        KEYWORDS.add(PAS);
        KEYWORDS.add(ADB);
    }
    
}
