package il.ac.sce.ir.metric.rouge.utils;

import java.util.List;
import java.util.Map;

public class RougeUtils {

    public static int nGramHits(Map<String, Integer> peerGrams, Map<String, Integer> modelGrams) {
        int hits = 0;
        for (String modelGramToken : modelGrams.keySet()) {
            if (!"_cn_".equals(modelGramToken) && peerGrams.get(modelGramToken) != null) {
                //System.out.println(modelGramToken);
                int peerHits = peerGrams.get(modelGramToken);
                int modelHits = modelGrams.get(modelGramToken);
                hits += Math.min(peerHits, modelHits);
            }
        }
        return hits;
    }

    public static int[][] calculateLongestCommonSubsequenceDPMatrix(List<String> leftTokens, List<String> rightTokens) {
        String[] left = leftTokens.toArray(new String[leftTokens.size()]);
        String[] right= rightTokens.toArray(new String[rightTokens.size()]);
        int[][] dpMatrix = new int[left.length + 1][right.length + 1];
        for (int j = 1; j <= right.length; j++) {
            for (int i = 1; i <= left.length; i++) {
                dpMatrix[i][j] = left[i - 1].equals(right[j - 1]) ?
                        1 + dpMatrix[i - 1][j - 1] :
                        Math.max(dpMatrix[i - 1][j], dpMatrix[i][j - 1]);

            }
        }
        return dpMatrix;
    }

    public static boolean[] extractMatch(int[][] dpMatrix) {
        int rows = dpMatrix[0].length;
        int columns = dpMatrix.length;
        boolean[] aMatch = new boolean[rows - 1];
        int j = rows - 1;
        int i = columns - 1;
        while ( j != 0 && i != 0) {
            //int prevVal = dpMatrix[i - 1][j - 1];
            int prevVal = dpMatrix[i - 1][j - 1];
            if (dpMatrix[i][j] == prevVal + 1 && prevVal == dpMatrix[i - 1][j] && prevVal == dpMatrix[i][j - 1]) {
                j--;
                i--;
                aMatch[j] = true;
            } else if (dpMatrix[i - 1][j] >= dpMatrix[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return aMatch;
    }

}
