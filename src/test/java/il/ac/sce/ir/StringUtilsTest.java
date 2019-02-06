package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.utils.StringUtils;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testStringUtilsLastFileTest() {
        StringUtils su = new StringUtils();
        System.out.println(su.getLastFileInString("c:\\temp\\somefile.txt"));
    }
}
