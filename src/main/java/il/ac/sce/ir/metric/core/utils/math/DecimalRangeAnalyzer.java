package il.ac.sce.ir.metric.core.utils.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DecimalRangeAnalyzer {

    private final double left;

    private final double right;

    private int decimalExponent;


    public DecimalRangeAnalyzer(double left, double right) {
        if (left >= right) {
            throw new IllegalArgumentException("Left value should be less than right");
        }

        this.left = left;
        this.right = right;
    }

    public int decimalExponent() {
        double diff = right - left;
        String diffScientific = String.format("%e", diff);
        int plusLastIndex = diffScientific.lastIndexOf("+");
        int minusLastIndex = diffScientific.lastIndexOf("-");
        int lastSignIndex;
        if (plusLastIndex > 0) {
            lastSignIndex = plusLastIndex;
        } else if (minusLastIndex > 0) {
            lastSignIndex = minusLastIndex;
        } else {
            throw new IllegalStateException("Invalid diff");
        }
        String decExpStr = diffScientific.substring(lastSignIndex);
        this.decimalExponent = Integer.parseInt(decExpStr);
        return this.decimalExponent;
    }


    public long decimalStep() {
        long powRes = 1;
        int absDecimalExp = decimalExponent();
        if (absDecimalExp < 0) {
            absDecimalExp = -absDecimalExp;
        }

        for (int i = 0; i < absDecimalExp; i++) {
           powRes *= 10d;
        }

        return powRes;
    }

    public BigDecimal closestByExponent(double val, int exponent) {
        BigDecimal bdVal = new BigDecimal(val);
        String valStr = bdVal.toString();
        // System.out.println(valStr);
        int i = valStr.lastIndexOf(".");
        String wholeDecimal;
        if (i >= 0) {
            wholeDecimal = valStr.substring(0, i);
        } else {
            wholeDecimal = valStr;
        }
        // System.out.println(wholeDecimal);
        if (exponent == 0) {
            return new BigDecimal(wholeDecimal);
        } else if (exponent > 0) {
            int roundingExponent = wholeDecimal.length() - exponent;
            if (roundingExponent < 0) {
                return BigDecimal.ZERO;
            }
            String number = wholeDecimal.substring(0, roundingExponent);
            for(int j = 0; j < exponent; j++) {
                number += "0";
            }
            return new BigDecimal(number);
        }

        if (i < 0) {
            return new BigDecimal(wholeDecimal);
        }

        String decimalPart = valStr.substring(i + 1);
        // System.out.println(decimalPart);
        int absExp = -exponent;
        decimalPart = decimalPart.substring(0, absExp);
        return new BigDecimal(wholeDecimal + "." + decimalPart);
    }

    public List<BigDecimal> decimalRange() {
        double decimalStep = decimalStep();
        BigDecimal step;
        int currentExponent = decimalExponent;
        if (currentExponent < 0) {
            step = BigDecimal.ONE.divide(new BigDecimal(decimalStep));
        } else {
            step = new BigDecimal(decimalStep);
        }
        ArrayList<BigDecimal> range = new ArrayList<>();

        BigDecimal minClosest = closestByExponent(left, currentExponent);
        BigDecimal curVal = minClosest;
        BigDecimal min = new BigDecimal(left);
        BigDecimal max = new BigDecimal(right);
        while (curVal.compareTo(max) <= 0) {
            if (curVal.compareTo(min) >= 0 && curVal.compareTo(max) <= 0) {
                range.add(curVal);
            }
            curVal = curVal.add(step);
        }

        if (range.size() > 3) {
            return range;
        }

        range.clear();
        currentExponent--;
        if (currentExponent < 0) {
            decimalStep *= 10;
        } else {
            decimalStep /= 10;
        }

        if (currentExponent < 0) {
            step = BigDecimal.ONE.divide(new BigDecimal(decimalStep));
        } else {
            step = new BigDecimal(decimalStep);
        }

        minClosest = closestByExponent(left, currentExponent);
        curVal = minClosest;
        min = new BigDecimal(left);
        max = new BigDecimal(right);

        while (curVal.compareTo(max) <= 0) {
            if (curVal.compareTo(min) >= 0 && curVal.compareTo(max) <= 0) {
                range.add(curVal);
            }
            curVal = curVal.add(step);
        }

        if (range.size() < 13) {
            return range;
        }


        Iterator<BigDecimal> iterator = range.iterator();
        while (iterator.hasNext()) {
            BigDecimal val = iterator.next();
            String strVal = val.toString();
            int dotIndex = strVal.lastIndexOf(".");
            long valToDecide;
            if (dotIndex < 0) {
                valToDecide = Long.parseLong(strVal);
                if (currentExponent > 0) {
                    long expPow = 1;
                    for (int i = 0; i < currentExponent; i++) {
                        expPow *= 10;
                    }
                    valToDecide /= expPow;
                }
            } else {
                valToDecide = Long.parseLong(strVal.substring(dotIndex + 1));
            }
            if (valToDecide % 2L != 0) {
                iterator.remove();
            }
        }

        return range;
    }
}
