package il.ac.sce.ir.metric.core.utils;

import il.ac.sce.ir.metric.core.config.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.config.ProcessedCategory;

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

    public String getCategoryDir(Configuration configuration, ProcessedCategory processedCategory) {
        return configuration.getWorkingSetDirectory() + File.separator + processedCategory.getDirLocation();
    }

    public String getTopicsDir(String categoryDir, ProcessedCategory processedCategory) {
        return categoryDir + File.separator + Constants.TOPICS;
    }
}
