package il.ac.sce.ir.metric.temp_playing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Starter001 {

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();
        final int threadsNum = 4;
        for (int i = 0; i < threadsNum; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    double t = 1;
                    for (long GG = 0; GG < 10_000L * 50L * 64L / (long) threadsNum; GG++) {
                        double f = 2;
                        double j = 3;
                        double r = f * t / j + f * f * f + Math.pow(f, t / 2) + j /  t * t;
                        double k = r / t * j;
                        t = k;
                        // System.out.println(k);
                    }
                }
            }));
        }
        threads.get(0).start();
        for (int i = 1; i < threadsNum; i++) {
            Thread thread = threads.get(i);
            thread.start();
        }

    }
}
