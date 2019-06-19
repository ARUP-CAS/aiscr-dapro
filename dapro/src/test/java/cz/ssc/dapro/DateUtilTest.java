package cz.ssc.dapro;

import junit.framework.TestCase;
import org.junit.Test;

public class DateUtilTest extends TestCase {
    @Test
    public void testReformatDate() throws Exception {
        String expected = "2018-06-15";
        String actual = DateUtil.reformatDate(expected);
        assertEquals(expected, actual);
    }
}
