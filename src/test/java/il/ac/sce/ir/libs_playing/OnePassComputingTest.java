package il.ac.sce.ir.libs_playing;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OnePassComputingTest {

    @Test
    public void mapOnePassComputeTest() {
        String[] values = "Alpha Beta Gamma Alpha Beta Beta Alpha Gamma Gamma Alpha Beta".split(" ");

        Map<String, int[]> counts = new HashMap<>();
        for (String value : values) {
            ++counts.computeIfAbsent(value, key -> new int[]{0})[0];
        }
        Map<String, Integer> countsToShow = new HashMap<>();
        counts.forEach((k,v) -> countsToShow.put(k, v[0]));
        System.out.println("Counts: \n" + countsToShow);
    }
}
