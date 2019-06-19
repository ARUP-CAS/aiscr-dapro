package cz.ssc.dapro;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.bind.DatatypeConverter;

public class DateUtil {
    static SimpleDateFormat initUtcFormat() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(tz);
        return sdf;
    }

    public static SimpleDateFormat initDayFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat utcFormat = initUtcFormat();
        return utcFormat.format(new Date());
    }

    public static String getExpirationDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat utcFormat = initUtcFormat();
        return utcFormat.format(cal.getTime());
    }

    public static String getExportDate(String datingFile) {
        long t = 0;
        if (datingFile != null) {
            File f = new File(datingFile);
            if (f.exists()) {
                t = f.lastModified();
            }
        }

        if (t == 0) {
            t = System.currentTimeMillis();
        }

        SimpleDateFormat dayFormat = initDayFormat();
        return dayFormat.format(new Date(t));
    }

    public static String reformatDate(String rawDate) throws ProtocolException {
        try {
            Calendar cal = DatatypeConverter.parseDate(rawDate);
            SimpleDateFormat dayFormat = initDayFormat();
            return dayFormat.format(cal.getTime());
        } catch (IllegalArgumentException ex) {
            throw new ProtocolException(Errors.badArgument, null);
        }
    }
}
