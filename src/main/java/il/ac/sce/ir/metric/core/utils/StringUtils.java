package il.ac.sce.ir.metric.core.utils;

public class StringUtils {

    public static boolean contains(String str, String... values) {
        for (String value : values) {
            if (str.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
