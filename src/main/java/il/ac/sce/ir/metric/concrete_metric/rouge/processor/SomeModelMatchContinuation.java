package il.ac.sce.ir.metric.concrete_metric.rouge.processor;

import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.processor.BiTextContinuation;
import il.ac.sce.ir.metric.concrete_metric.rouge.utils.RougeUtils;

public class SomeModelMatchContinuation implements BiTextContinuation<int[][], boolean[]> {

    @Override
    public BiText<boolean[]> process(BiText<int[][]> biText) {
        int[][] dpMatrix = biText.getData();

        boolean[] someModelMatch = RougeUtils.extractMatch(dpMatrix);
        return new BiText<>(biText.getLeftId(), biText.getRightId(), someModelMatch);
    }
}
