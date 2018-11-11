package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import il.ac.sce.ir.metric.core.utils.StringUtils;

import java.io.File;

public class FileSystemTopology {

    private static final StringUtils STRING_UTILS = new StringUtils();

    private final String workingSetDirectory;

    public FileSystemTopology(String workingSetDirectory) {
        if (STRING_UTILS.isEmpty(workingSetDirectory)) {
            throw new IllegalArgumentException("Working Set Directory cannot be empty");
        }
        this.workingSetDirectory = workingSetDirectory;
    }

    private static class Mirror {

        private String categoryDir;

        private void resolve(String workingSetCategory) {

        }
    }
}
