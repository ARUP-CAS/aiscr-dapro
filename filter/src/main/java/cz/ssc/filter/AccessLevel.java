package cz.ssc.filter;

/**
 *
 * @author Juraj
 */
public enum AccessLevel {

    ANONYM('A'),
    BADATEL('B'),
    ARCHEOLOG('C'),
    ARCHIVAR('D'),
    ADMIN('E');

    private final Character key;

    AccessLevel(Character key){
        this.key = key;
    }

    public final Character getKey() {
        return key;
    }

    public static AccessLevel convert(String s) {
        switch (s) {
            case "A":
                return AccessLevel.ANONYM;
            case "B":
                return AccessLevel.BADATEL;
            case "C":
                return AccessLevel.ARCHEOLOG;
            case "D":
                return AccessLevel.ARCHIVAR;
            case "E":
                return AccessLevel.ADMIN;
            default:
                throw new IllegalArgumentException();
        }
    }
}
