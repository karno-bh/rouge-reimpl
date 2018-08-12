package il.ac.sce.ir.metric;

import java.util.List;
import java.util.Map;

public interface NGramExtractor {

    String COUNT = "_cn_";

    Map<String, Integer> extract(List<String> tokens, int nSize);

}
