package il.ac.sce.ir.statistics;

import il.ac.sce.ir.metric.core.statistics.NotchedBoxData;
import il.ac.sce.ir.metric.core.statistics.Quantile;
import org.junit.Test;

public class QuantileTest {

    double[] data =
            {58,  63,  69,  72,  74,  79,
             88,  88,  90,  91,  93,  94,
             97,  97,  99,  99,  99,  100,
             103, 104, 105, 107, 118, 127};

    @Test
    public void quantileTest() {
        Quantile q = new Quantile(data);
        q.quartiles();
        q.calcIQR();
        System.out.println(q.getFirstQuartile());
        System.out.println(q.getMedian());
        System.out.println(q.getThirdQuartile());
        System.out.println(q.getIqr());
    }

    @Test
    public void boxDataTest() {
        NotchedBoxData notchedBoxData = new NotchedBoxData(data);
        notchedBoxData.adjucentValues();
        notchedBoxData.notches();

        System.out.println(notchedBoxData.getLowIndex());
        System.out.println(data[notchedBoxData.getLowIndex()]);
        System.out.println(notchedBoxData.getUpIndex());
        System.out.println(data[notchedBoxData.getUpIndex()]);
        System.out.println(notchedBoxData.getLowNotch());
        System.out.println(notchedBoxData.getUpNotch());
    }
}
