package il.ac.sce.ir;

import il.ac.sce.ir.metric.*;
import il.ac.sce.ir.metric.rouge.processor.NGramPipeline;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RougeNTest {

    @Test
    public void testRougeN01() {
        String text = "the officers it the concessions the the discussion";
        String[] split = text.split("\\s+");
        List<String> tokens = Arrays.asList(split);
        Document document = new Document("id0001");
        document.setTokens(tokens);

        NGramPipeline nGramPipeline = new NGramPipeline();
        nGramPipeline.setnGramExtractor(new NgramExtractorDefaultImpl());
        nGramPipeline.process(document);

        System.out.println(document.getnGram(1));
    }
}
