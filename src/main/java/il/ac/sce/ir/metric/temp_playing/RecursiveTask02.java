package il.ac.sce.ir.metric.temp_playing;

import java.util.UUID;
import java.util.concurrent.RecursiveTask;

public class RecursiveTask02 extends RecursiveTask<Long>{

    private final UUID myUUID = UUID.randomUUID();

    @Override
    protected Long compute() {
        // long timeToSleep = (long)(Math.random() * 100);
        if (Starter.print) {
            System.out.println(myUUID + " Level 2 working on thread: " + Thread.currentThread().getName());
        }
        try {
            long startTime = System.currentTimeMillis();
            double t = 1;
            for (long GG = 0; GG < 10_000L; GG++) {
                double f = 2;
                double j = 3;
                double r = f * t / j + f * f * f + Math.pow(f, t / 2) + j /  t * t;
                double k = r / t * j;
                t = k;
                // System.out.println(k);
            }
            long totalSumbitTime = System.currentTimeMillis() - startTime;
            if (Starter.print) {
                System.out.println(myUUID + " Level 2 total time: " + totalSumbitTime);
            }
            return totalSumbitTime;
        } catch (Exception e) {
            throw new RuntimeException(myUUID + " Level 2 failed to be executed");
        }
    }



}
