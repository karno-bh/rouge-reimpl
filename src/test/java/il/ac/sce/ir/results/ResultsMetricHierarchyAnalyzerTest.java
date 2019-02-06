package il.ac.sce.ir.results;

import il.ac.sce.ir.metric.core.utils.file_system.KnownMetricFormatsHierarchicalOrganizer;
import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class ResultsMetricHierarchyAnalyzerTest {

    @Test
    public void availableSystemTest() {
        KnownMetricFormatsHierarchicalOrganizer organizer = new KnownMetricFormatsHierarchicalOrganizer();
        Map<String, Object> metricHierarchy = organizer.organize("result", "result/reduced");
        ResultsMetricHierarchyAnalyzer analyzer = new ResultsMetricHierarchyAnalyzer(metricHierarchy);
        Map<String, Set<String>> availableSystems = analyzer.getAvailableSystems();
        System.out.println("Available Systems: " + availableSystems);
    }

    @Test
    public void availableSystemMetricTest() {
        KnownMetricFormatsHierarchicalOrganizer organizer = new KnownMetricFormatsHierarchicalOrganizer();
        Map<String, Object> metricHierarchy = organizer.organize("result", "result/reduced");
        ResultsMetricHierarchyAnalyzer analyzer = new ResultsMetricHierarchyAnalyzer(metricHierarchy);
        Map<String, Set<String>> availableSystems = analyzer.getAvailableSystems();
        availableSystems.forEach((category, systems) -> {
            systems.forEach(system -> {
                Set<String> availableSystemMetrics = analyzer.getAvailableSystemMetrics(category, system);
                System.out.println("available system metrics: " + availableSystemMetrics);
            });
        });
    }

}