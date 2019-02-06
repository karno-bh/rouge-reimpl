package il.ac.sce.ir.results;

import il.ac.sce.ir.metric.core.utils.file_system.AllCSVFilesLoader;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ResultsLoadingTest {

    @Test
    public void resultsLoadingTest() {
        AllCSVFilesLoader allCSVFilesLoader = new AllCSVFilesLoader();
        Map<String, List<Map<String, String>>> results = allCSVFilesLoader.getAllCSVInDirectory("result");
        System.out.println(results);
    }

    @Test
    public void resultsLoadinWithReducerTest() {
        AllCSVFilesLoader allCSVFilesLoader = new AllCSVFilesLoader(true);
        Map<String, List<Map<String, String>>> results = allCSVFilesLoader.getAllCSVInDirectories("result", "result/reduced");
        System.out.println(results);
    }
}
