package il.ac.sce.ir.libs_playing;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapComputePlayingTest {

    @Test
    public void mapComputeTest() {

        Map<String, Double> map = new HashMap<>();
        for (double t = 0; t < 5; t++) {
            final double j = t;
            map.compute("Val1", (k, v) -> v == null ? j : v + j);
        }
        System.out.println("Map: " + map);
    }
}
