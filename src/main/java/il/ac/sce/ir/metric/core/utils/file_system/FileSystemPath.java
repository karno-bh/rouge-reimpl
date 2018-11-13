package il.ac.sce.ir.metric.core.utils.file_system;

import java.io.File;

public class FileSystemPath {

    public String combinePath(String... params) {
        StringBuilder sb = new StringBuilder(256);
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                sb.append(params[i]);
            } else {
                sb.append(File.separatorChar).append(params[i]);
            }
        }
        return sb.toString();
    }

}
