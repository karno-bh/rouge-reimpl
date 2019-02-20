package il.ac.sce.ir.libs_playing;

import org.junit.Test;

import java.io.File;

public class StringFunctionTest {

    @Test
    public void stringFunctionsTest() {

        String path = "c:\\my\\temp";
        int lastIndex = path.lastIndexOf(File.separator);
        System.out.println(lastIndex);
        System.out.println(path.substring(lastIndex + 1));
    }

    @Test
    public void strFunctTest2() {
        String str = "group.test.id";
        System.out.println(str.substring(str.indexOf('.') + 1));
    }
}
