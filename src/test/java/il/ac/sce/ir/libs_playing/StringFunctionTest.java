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
}
