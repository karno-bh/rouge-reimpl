package il.ac.sce.ir.libs_playing;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClassReflectionTest {

    @Test
    public void testIsAssignableFrom() {
        Class c1 = ArrayList.class;
        Class c2 = List.class;

        System.out.println(c1.isAssignableFrom(c2));
        System.out.println(c2.isAssignableFrom(c1)); // works!: c2 should be a super!
    }
}
