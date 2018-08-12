package il.ac.sce.ir.metric.core.utils;

public class RangeMapper {
    private final double fromL;
    private final double toL;
    private final double factor;


    public RangeMapper(double fromL, double fromR, double toL, double toR) {
        this.fromL = fromL;
        this.toL = toL;
        this.factor = (toR - toL) / (fromR - fromL);
    }

    public double map(double value) {
        return toL + (value - fromL) * factor;
    }

    public static double map(double value, double fromL, double fromR, double toL, double toR) {
        double fromDiff = fromR - fromL;
        double toDiff = toR - toL;
        double fact = toDiff / fromDiff;
        double normVal = value - fromL;
        return toL + normVal * fact;
    }
}
