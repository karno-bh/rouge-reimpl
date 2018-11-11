package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import java.util.List;

public class FileSystemTopologyResolver {

    private String workingSetDirectory;

    private List<ProcessedCategory> processedCategories;

    public String getWorkingSetDirectory() {
        return workingSetDirectory;
    }

    public void setWorkingSetDirectory(String workingSetDirectory) {
        this.workingSetDirectory = workingSetDirectory;
    }

    public List<ProcessedSystem> getProcessedSystems(String category) {
        return null;
    }

    public List<String> getModels(ProcessedCategory category) {
        return null;
    }

    public List<String> getTopics(ProcessedCategory category) {
        return null;
    }

    public List<String> getPeers(ProcessedSystem processedSystem) {
        return null;
    }

}
