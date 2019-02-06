package il.ac.sce.ir.results;

import il.ac.sce.ir.metric.core.utils.file_system.KnownMetricFormatsHierarchicalOrganizer;
import org.junit.Test;

import java.util.Map;

public class KnownMetricsOrganizerTest {

    @Test
    public void knownMetricOrganizerTest() {
        KnownMetricFormatsHierarchicalOrganizer organizer = new KnownMetricFormatsHierarchicalOrganizer();
        Map<String, Object> result = organizer.organize("result", "result/reduced");
        System.out.println(result);
    }
}
