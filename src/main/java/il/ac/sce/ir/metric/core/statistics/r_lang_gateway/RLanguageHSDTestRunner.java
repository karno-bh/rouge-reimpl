package il.ac.sce.ir.metric.core.statistics.r_lang_gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.data.HSDTestLetterRepresentationRow;
import il.ac.sce.ir.metric.core.statistics.util.RLanguageHSDTestResponseAnalyzer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RLanguageHSDTestRunner {

    private final Map<String, List<Double>> flattenedData;

    public RLanguageHSDTestRunner(Map<String, List<Double>> flattenedData) {
        this.flattenedData = flattenedData;
        File tempDir = requireTempDirectory();
        try {
            requireScripts(tempDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot copy files from resources", e);
        }
    }

    private File requireTempDirectory() {
        File tempDir = new File(Constants.TEMP_DIRECTORY_DEFAULT);
        if (!tempDir.exists()) {
            boolean mkdir = tempDir.mkdir();
            if (!mkdir) {
                throw new RuntimeException("Cannot create temp directory");
            }
        } else if (!tempDir.isDirectory()) {
            throw new RuntimeException(Constants.TEMP_DIRECTORY_DEFAULT  + " should be a directory under main directory");
        }
        return tempDir;
    }

    private void requireScripts(File tempDir) throws IOException {
        File hsdRScript = Paths.get(tempDir.toString(), Constants.SCRIPT_R_HSD_TEST).toFile();
        File hsdBatScript = Paths.get(tempDir.toString(), Constants.SCRIPT_BAT_HSD_TEST).toFile();
        if (!hsdRScript.exists() || !hsdBatScript.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream hsdRScriptStream = cl.getResourceAsStream(Constants.SCRIPT_R_HSD_TEST);
            Files.copy(hsdRScriptStream, hsdRScript.toPath(), StandardCopyOption.REPLACE_EXISTING);

            InputStream hsdBatScriptStream = cl.getResourceAsStream(Constants.SCRIPT_BAT_HSD_TEST);
            Files.copy(hsdBatScriptStream, hsdBatScript.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void saveToTempCSV() {
        File resultFile = Paths.get(Constants.TEMP_DIRECTORY_DEFAULT, Constants.DEFAULT_CSV_FILE_RPC).toFile();
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(resultFile)
                )
        )) {
            pw.println("metric,value");
            flattenedData.forEach((metric, data) -> {
                data.forEach(val -> {
                    pw.println(metric + "," + val);
                });
            });
        } catch (IOException ioe) {
            throw new RuntimeException("Cannot write file", ioe);
        }
    }

    private void runRScript() {
        try {
            String path = Constants.TEMP_DIRECTORY_DEFAULT;
            File f = new File(path);
            Process p = Runtime.getRuntime().exec("cmd /c start /wait " + f.getAbsolutePath() + "\\run_hsd_test.bat");
            // System.out.println("Waiting for batch file ...");
            p.waitFor();
            // System.out.println("Batch file done.");
        } catch (Exception e) {
            throw new RuntimeException("Can't execute R code");
        }
    }

    private List<Map<String, Object>> readResult() {
        File file = Paths.get(Constants.TEMP_DIRECTORY_DEFAULT, Constants.DEFAULT_HSD_TEST_RESULT).toFile();
        try {
            return new ObjectMapper().readValue(file, ArrayList.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read result file", e);
        }
    }

    public List<HSDTestLetterRepresentationRow> process() {
        saveToTempCSV();
        runRScript();
        List<Map<String, Object>> result = readResult();
        RLanguageHSDTestResponseAnalyzer analyzer = new RLanguageHSDTestResponseAnalyzer(result);
        return analyzer.asHSDTestLetterRepresentation();
    }
}
