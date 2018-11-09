package il.ac.sce.ir.metric.concrete_metric.rouge.processor;

import il.ac.sce.ir.metric.concrete_metric.rouge.utils.RougeUtils;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;

import java.util.List;

public class DPMatrixBiTextProcessor implements BiTextProcessor<List<String>, int[][]> {

    @Override
    public BiText<int[][]> process(Text<List<String>> left, Text<List<String>> right) {
        int[][] dpMatrix = RougeUtils.calculateLongestCommonSubsequenceDPMatrix(left.getTextData(), right.getTextData());
        BiText<int[][]> result = new BiText<>(left.getTextId(), right.getTextId(), dpMatrix);
        return result;
    }
}
