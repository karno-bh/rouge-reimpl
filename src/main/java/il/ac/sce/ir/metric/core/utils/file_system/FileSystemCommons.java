package il.ac.sce.ir.metric.core.utils.file_system;

import java.io.File;
import java.text.MessageFormat;

public class FileSystemCommons {

    public void requireAndCreateResultDirectory(String resultDirectoryName) {
        File resultDirectory = new File(resultDirectoryName);
        if (!resultDirectory.exists()) {
            boolean mkdirsOk = resultDirectory.mkdirs();
            if (!mkdirsOk) {
                throw new RuntimeException("Cannot create result directory: " + resultDirectoryName);
            }
        } else if (!resultDirectory.isDirectory()) {
            throw new RuntimeException(MessageFormat.format("Result Directory \"{0}\" is not a directory on file system", resultDirectory));
        }
    }
}
