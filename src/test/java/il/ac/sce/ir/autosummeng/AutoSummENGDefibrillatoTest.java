package il.ac.sce.ir.autosummeng;

import gr.demokritos.iit.jinsect.console.summaryEvaluator;
import org.junit.Test;

import java.util.concurrent.Semaphore;

public class AutoSummENGDefibrillatoTest {


    @Test
    public void startInTheMiddleAttempt001Test() {

        String _do = "all";
        Semaphore outputSemaphore = new Semaphore(1);
        int wordMin = 1;
        int wordMax = 10;
        int wordDist = 5;
        int charMin = 1;
        int charMax = 10;
        int charDist = 5;
        int threadsNum = 1;
        String outfile = ".\\output.log";
        String summaryDir = "C:\\my\\learning\\final-project\\working-set\\attempt003\\summaries\\sysid01";
        String modelsDir = "C:\\my\\learning\\final-project\\working-set\\attempt003\\models";
        int weightMethod = summaryEvaluator.USE_OCCURENCES_AS_WEIGHT;
        boolean silent = false;
        boolean progress = false;



        summaryEvaluator summaryEvaluator = new summaryEvaluator(
                outputSemaphore,
                _do,
                wordMin,
                wordMax,
                wordDist,
                charMin,
                charMax,
                charDist,
                threadsNum,
                outfile,
                summaryDir,
                modelsDir,
                silent,
                weightMethod,
                progress
        );

        summaryEvaluator.run();
    }
}
