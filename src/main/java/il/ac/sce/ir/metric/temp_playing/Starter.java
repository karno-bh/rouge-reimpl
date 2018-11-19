package il.ac.sce.ir.metric.temp_playing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Starter {

    public static boolean print = false;

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(8);

    public void start() {
        List<RecursiveTask01> level1Tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            level1Tasks.add(new RecursiveTask01());
        }

        long startTime = System.currentTimeMillis();
        long result = 0;
        for (RecursiveTask01 level1Task : level1Tasks) {
            ForkJoinTask<Long> forkJoinTask = forkJoinPool.submit(level1Task);
            result += forkJoinTask.join();
        }
        long diff = System.currentTimeMillis() - startTime;
        double ratio = (double) result / (double) diff;

        System.out.println("Result is: " + result + " total time: " + diff + " ratio: " + ratio);
    }

    public static void main(String[] args) {
        Starter starter = new Starter();
        starter.start();
    }
}
