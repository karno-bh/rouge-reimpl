package il.ac.sce.ir.metric.temp_playing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class RecursiveTask01 extends RecursiveTask<Long> {

    private final UUID myUUID = UUID.randomUUID();

    @Override
    protected Long compute() {
        if (Starter.print) {
            System.out.println(myUUID + " Level 1 working on thread: " + Thread.currentThread().getName());
        }
        try {
            long result = 0;
            long startTime = System.currentTimeMillis();
            List<RecursiveTask02> level2Tasks = generateLevel2Tasks();
            List<ForkJoinTask<Long>> promises = new ArrayList<>();
            for (RecursiveTask02 level2Task : level2Tasks) {
                ForkJoinTask<Long> promise = level2Task.fork();
                promises.add(promise);
            }
            for (ForkJoinTask<Long> promise : promises) {
                result += promise.join();
            }
            long totalSumbitTime = System.currentTimeMillis() - startTime;
            if (Starter.print) {
                System.out.println(myUUID + " Level 1 total time: " + totalSumbitTime);
            }
            return result;
        } catch (Exception e) {
            if (Starter.print) {
                throw new RuntimeException(myUUID + " Level 1 failed to be executed");
            }
        }
        return 0L;
    }

    public List<RecursiveTask02> generateLevel2Tasks() {
        List<RecursiveTask02> leveTwoTasks = new ArrayList<>();
        //int taskCount = (int)(Math.random() * 4) + 1;
        int taskCount = 64;
        for (int i = 0; i < taskCount; i++) {
            RecursiveTask02 recursiveTask02 = new RecursiveTask02();
            leveTwoTasks.add(recursiveTask02);
        }
        return leveTwoTasks;
    }
}
