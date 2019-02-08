package il.ac.sce.ir.libs_playing;

import org.junit.Test;

import java.io.File;

public class RenameTopicsTest {

    // @Test
    public void renameTopicsTest() {
        String topics = "c:\\my\\learning\\final-project\\working-set\\attempt001\\category01\\topics\\";
        File file = new File(topics);
        File[] files = file.listFiles();
        for (File innerFile : files) {
            String innerFileName = innerFile.getName();
            System.out.println(innerFileName);
            String prefix = innerFileName.substring(0, 4);
            //System.out.println(prefix);
            String postfix = innerFileName.substring(4);
            //System.out.println(postfix);
            String newFileName = prefix + "." + postfix;
            innerFile.renameTo(new File(topics + newFileName));
            System.out.println(newFileName);
            System.out.println(newFileName.substring(0, newFileName.indexOf('.')));
        }
    }
}
