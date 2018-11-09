package il.ac.sce.ir.autosummeng;

import gr.demokritos.iit.jinsect.console.summaryEvaluator;
import gr.demokritos.iit.jinsect.documentModel.comparators.StandardDocumentComparator;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import gr.demokritos.iit.jinsect.structs.SimilarityArray;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AutoSummENGReImplAttemptTest {

    @Test
    public void reimplAttemptTest() {

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


        // algo specific...
        boolean bDoWordNGrams = true;
        boolean bDoCharNGrams = true;

        String peerFileName = "C:\\my\\learning\\final-project\\working-set\\attempt003\\summaries\\sysid01\\M000\\M000";

        List<String> models = Arrays.asList(
                "C:\\my\\learning\\final-project\\working-set\\attempt003\\models\\M000\\M000.A.250",
                "C:\\my\\learning\\final-project\\working-set\\attempt003\\models\\M000\\M000.B.250",
                "C:\\my\\learning\\final-project\\working-set\\attempt003\\models\\M000\\M000.C.250"
        );

        // Init return struct
        SimilarityArray saRes = new SimilarityArray();

        // Read first file
        SimpleTextDocument ndDoc1 = new SimpleTextDocument(wordMin, wordMax, wordDist);
        NGramSymWinDocument ndNDoc1 = new NGramSymWinDocument(charMin, charMax, charDist, charMin, charMax);

        StandardDocumentComparator sdcComparator = new StandardDocumentComparator();
        StandardDocumentComparator sdcNComparator = new StandardDocumentComparator();

        if (bDoWordNGrams) {
            ndDoc1.loadDataStringFromFile(peerFileName);
        }

        if (bDoCharNGrams) {
            ndNDoc1.loadDataStringFromFile(peerFileName);
        }

        for (String model : models) {

            // Load model data
            SimpleTextDocument ndDoc2 = null;
            NGramSymWinDocument ndNDoc2 = null;

            if (bDoWordNGrams) {
                ndDoc2 = new SimpleTextDocument(wordMin, wordMax, wordDist);
                ndDoc2.loadDataStringFromFile(model);
            }

            if (bDoCharNGrams) {
                ndNDoc2 = new NGramSymWinDocument(charMin, charMax, charDist, charMin, charMax);
                ndNDoc2.loadDataStringFromFile(model);
            }

            try {
                GraphSimilarity sSimil = null;
                if (bDoWordNGrams) {
                    // Get simple text similarities
                    sSimil = sdcComparator.getSimilarityBetween(ndDoc1, ndDoc2);
                    saRes.SimpleTextOverallSimil = sSimil;
                    saRes.SimpleTextGraphSimil = sdcComparator.getGraphSimilarity();
                    saRes.SimpleTextHistoSimil = sdcComparator.getHistogramSimilarity();
                }

                GraphSimilarity sSimil2 = null;
                if (bDoCharNGrams) {
                    sSimil2 = sdcNComparator.getSimilarityBetween(ndNDoc1, ndNDoc2);
                    // Get n-gram document similarities
                    saRes.NGramOverallSimil = sSimil2;
                    saRes.NGramGraphSimil = sdcNComparator.getGraphSimilarity();
                    saRes.NGramHistoSimil = sdcNComparator.getHistogramSimilarity();
                }

                StringBuilder sb = new StringBuilder(256);
                char delimiter = '\t';
                sb.append(peerFileName)
                    .append(delimiter)
                    .append(model)
                    .append(delimiter);

                if (bDoWordNGrams) {
                    sb.append(saRes.SimpleTextGraphSimil.ContainmentSimilarity + "\t" +
                            saRes.SimpleTextGraphSimil.ValueSimilarity + "\t" +
                            saRes.SimpleTextGraphSimil.SizeSimilarity + "\t" +
                            saRes.SimpleTextHistoSimil.ContainmentSimilarity + "\t" +
                            saRes.SimpleTextHistoSimil.ValueSimilarity + "\t" +
                            saRes.SimpleTextHistoSimil.SizeSimilarity + "\t" +
                            saRes.SimpleTextOverallSimil.getOverallSimilarity() + "\t");
                }

                if (bDoCharNGrams) {
                    sb.append(saRes.NGramGraphSimil.ContainmentSimilarity + "\t" +
                            saRes.NGramGraphSimil.ValueSimilarity + "\t" +
                            saRes.NGramGraphSimil.SizeSimilarity + "\t" +
                            saRes.NGramHistoSimil.ContainmentSimilarity + "\t" +
                            saRes.NGramHistoSimil.ValueSimilarity + "\t" +
                            saRes.NGramHistoSimil.SizeSimilarity + "\t" +
                            saRes.NGramOverallSimil.getOverallSimilarity());
                }

                System.out.println(sb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
