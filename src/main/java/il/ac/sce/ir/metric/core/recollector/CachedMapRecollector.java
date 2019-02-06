package il.ac.sce.ir.metric.core.recollector;

import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.file_system.AllCSVFilesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CachedMapRecollector implements Recollector<Map<String, Object>> {

    private static final Logger log = LoggerFactory.getLogger(CachedMapRecollector.class);

    private final Map<String, Object> results = new HashMap<>();

    private Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public synchronized Map<String, Object> recollect() {
        if (!results.isEmpty()) {
            return results;
        }
        String resultDirectoryLocation = configuration.getResultDirectory();
        AllCSVFilesLoader allCSVFilesLoader = new AllCSVFilesLoader();
        Map<String, List<Map<String, String>>> fileContents = allCSVFilesLoader.getAllCSVInDirectory(resultDirectoryLocation);
        if (fileContents == null) return null;

        // System.out.println(fileContents);

        return allCSVFilesLoader.extractHierarchyStructure(
                fileContents,
                Arrays.asList(Constants.METRICS_FOR_SYSTEM_TO_BE_REDUCED),
                results
        );
    }


}
