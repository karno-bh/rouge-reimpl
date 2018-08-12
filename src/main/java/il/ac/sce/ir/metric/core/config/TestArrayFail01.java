package il.ac.sce.ir.metric.core.config;

public class TestArrayFail01 {

    public static void main(String[] args) {
        int deg = 2;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            long[] array = new long[deg];
            cheatCompiler(array);
            deg *= 2;
        }
    }

    public static void cheatCompiler(long[] array) {
        int length = array.length;
        System.out.println(length);
    }
}
