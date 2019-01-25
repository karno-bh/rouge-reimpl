package il.ac.sce.ir.libs_playing;

import org.junit.Test;

public class MathConversionsTest {

    @Test
    public void mathConversionsTest() {
        int diff = 9;
        int d = (int) (Math.floor(((double) diff) / 2));
        System.out.println("d = " + d);

        d = diff / 2;
        System.out.println(d);
    }
}
