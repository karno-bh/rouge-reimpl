package il.ac.sce.ir;

import il.ac.sce.ir.metric.NGramExtractor;
import il.ac.sce.ir.metric.NgramExtractorDefaultImpl;
import org.junit.Test;

import java.util.*;

public class NGramExtractor01Test {

    @Test
    public void testNGramValid01() {
        String text = "the officers it the concessions the the discussion";
        String[] split = text.split("\\s+");
        List<String> tokens = Arrays.asList(split);
        NGramExtractor nGramExtractor = new NgramExtractorDefaultImpl();
        Map<String, Integer> extract01 = nGramExtractor.extract(tokens, 1);
        System.out.println(extract01);
        Map<String, Integer> expectedResult = new HashMap<>();
        //expectedResult.put("concessions")
        //Assert.assertEquals();
    }

    @Test
    public void testNGramValid02() {
        String text = "the officers it the concessions the the discussion";
        String[] split = text.split("\\s+");
        List<String> tokens = Arrays.asList(split);
        NGramExtractor nGramExtractor = new NgramExtractorDefaultImpl();
        Map<String, Integer> extract01 = nGramExtractor.extract(tokens, 2);
        System.out.println(extract01);
        Map<String, Integer> expectedResult = new HashMap<>();
        //expectedResult.put("concessions")
        //Assert.assertEquals();
    }

    @Test
    public void testNGramNull() {
        NGramExtractor nGramExtractor = new NgramExtractorDefaultImpl();
        Map<String, Integer> extract01 = nGramExtractor.extract(null, 2);
        System.out.println(extract01);
        Map<String, Integer> expectedResult = new HashMap<>();
        //expectedResult.put("concessions")
        //Assert.assertEquals();
    }

    @Test
    public void testNGramEmpty() {
        NGramExtractor nGramExtractor = new NgramExtractorDefaultImpl();
        Map<String, Integer> extract01 = nGramExtractor.extract(new ArrayList<>(), 2);
        System.out.println(extract01);
        Map<String, Integer> expectedResult = new HashMap<>();
        //expectedResult.put("concessions")
        //Assert.assertEquals();
    }
}
