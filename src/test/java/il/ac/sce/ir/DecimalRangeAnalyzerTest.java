package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.utils.math.DecimalRangeAnalyzer;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class DecimalRangeAnalyzerTest {

    @Test
    public void decimalRangeAnalyzerTest() {
        DecimalRangeAnalyzer decimalRangeAnalyzer = new DecimalRangeAnalyzer(2,22);
        System.out.println(decimalRangeAnalyzer.decimalExponent());
        System.out.println(decimalRangeAnalyzer.decimalStep());
        System.out.println(decimalRangeAnalyzer.decimalRange());

        DecimalRangeAnalyzer decimalRangeAnalyzer1 = new DecimalRangeAnalyzer(0, 1);
        System.out.println(decimalRangeAnalyzer1.decimalExponent());
        System.out.println(decimalRangeAnalyzer1.decimalStep());
        System.out.println(decimalRangeAnalyzer1.decimalRange());

        DecimalRangeAnalyzer decimalRangeAnalyzer2 = new DecimalRangeAnalyzer(0, .99999);
        System.out.println(decimalRangeAnalyzer2.decimalExponent());
        System.out.println(decimalRangeAnalyzer2.decimalStep());
        System.out.println(decimalRangeAnalyzer2.decimalRange());

        DecimalRangeAnalyzer decimalRangeAnalyzer3 = new DecimalRangeAnalyzer(30.345, 92.345);
        System.out.println(decimalRangeAnalyzer3.decimalRange());



        DecimalRangeAnalyzer decimalRangeAnalyzer5 = new DecimalRangeAnalyzer(-10, 10);
        System.out.println(decimalRangeAnalyzer5.decimalExponent());
        System.out.println(decimalRangeAnalyzer5.decimalStep());
        System.out.println(decimalRangeAnalyzer5.decimalRange());

        DecimalRangeAnalyzer decimalRangeAnalyzer6 = new DecimalRangeAnalyzer(100, 120);
        System.out.println(decimalRangeAnalyzer6.decimalExponent());
        System.out.println(decimalRangeAnalyzer6.decimalStep());
        System.out.println(decimalRangeAnalyzer6.decimalRange());

        DecimalRangeAnalyzer decimalRangeAnalyzer7 = new DecimalRangeAnalyzer(-10000, 0);
        System.out.println(decimalRangeAnalyzer7.decimalExponent());
        System.out.println(decimalRangeAnalyzer7.decimalStep());
        System.out.println(decimalRangeAnalyzer7.decimalRange());


    }

    @Test
    public void testMath() {
        double t = -123354358098.343478998772434;
        System.out.println(Math.floor(t));
        System.out.println(Math.ceil(t));
        String s = String.format("%f", t);
        System.out.println(new BigDecimal(t).toString());
        System.out.println(new BigDecimal(123.0d).toString());
        System.out.println(s);

        t = 12.3434;
        System.out.println(Math.floor(t));
        System.out.println(Math.ceil(t));
    }

    @Test
    public void testMath2() {
        DecimalRangeAnalyzer decimalRangeAnalyzer = new DecimalRangeAnalyzer(88.565d, 120d);
        double d = -82338.5655611d;
        System.out.println(d);
        System.out.println("DBP: " + decimalRangeAnalyzer.closestByExponent(d, -1));
        System.out.println(new BigDecimal("88.566"));
        System.out.println(new BigDecimal("88.566"));

        System.out.println(decimalRangeAnalyzer.decimalRange());

    }

    @Test
    public void testMath3() {
        DecimalRangeAnalyzer decimalRangeAnalyzer = new DecimalRangeAnalyzer(-11231.213123d, 320.34324);
        List<BigDecimal> range = decimalRangeAnalyzer.decimalRange();
        System.out.println(range);
        System.out.println(range.size());
    }
}
