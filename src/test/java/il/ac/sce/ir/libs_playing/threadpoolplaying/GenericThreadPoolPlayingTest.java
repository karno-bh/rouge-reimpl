package il.ac.sce.ir.libs_playing.threadpoolplaying;

import org.junit.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GenericThreadPoolPlayingTest {

    @Test
    public void playWithThreadPoolExampleTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final int tasks = 30;

        List<Future<ResultHolder>> playingCallables = new ArrayList<>();

        for (int i = 0; i < tasks; i++) {
            PlayingCallable task = new PlayingCallable(i);
            long startTime = System.currentTimeMillis();
            Future<ResultHolder> taskFuture = executorService.submit(task);
            long totalSumbitTime = System.currentTimeMillis() - startTime;
            playingCallables.add(taskFuture);
            System.out.println("Task: " + i + " submission time: " + totalSumbitTime);
        }

        List<ResultHolder> resultHolderList = new ArrayList<>();
        for (Future<ResultHolder> future : playingCallables) {
            try {
                long startTime = System.currentTimeMillis();
                ResultHolder resultHolder = future.get();
                resultHolderList.add(resultHolder);
                long totalSumbitTime = System.currentTimeMillis() - startTime;
                System.out.println("Task: " + resultHolder.getTaskId() + " future.get() time: " + totalSumbitTime);
            } catch (Exception e) {
                throw new RuntimeException("Task failed to be executed");
            }
        }

        MessageFormat messageFormat = new MessageFormat("Task {0} slept for {1} milliseconds");

        for (ResultHolder resultHolder : resultHolderList) {
            System.out.println(messageFormat.format(new Object[] {resultHolder.getTaskId(), resultHolder.getSleptMs()}));
        }

        /*try {
            for (int i = 0; i < 30; i++) {
                PlayingCallable playingCallable = new PlayingCallable(i);
                Future<ResultHolder> resultHolderFuture = executorService.submit(playingCallable);
                try {
                    resultHolderList.add(resultHolderFuture.get());
                } catch (Exception e) {
                    throw new RuntimeException("Task " + i + " failed to be executed");
                }
            }
        } finally {
            executorService.shutdown();
        }

        MessageFormat messageFormat = new MessageFormat("Task {0} slept for {1} milliseconds");

        for (ResultHolder resultHolder : resultHolderList) {
            System.out.println(messageFormat.format(new Object[] {resultHolder.getTaskId(), resultHolder.getSleptMs()}));
        }*/

    }

    private static class ResultHolder {

        private final int taskId;

        private final long sleptMs;

        public ResultHolder(int taskId, long sleptMs) {
            this.taskId = taskId;
            this.sleptMs = sleptMs;
        }

        public int getTaskId() {
            return taskId;
        }

        public long getSleptMs() {
            return sleptMs;
        }
    }

    private static class PlayingCallable implements Callable<ResultHolder> {

        private final int taskId;

        public PlayingCallable(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public ResultHolder call() throws Exception {
            double sleepSecond = Math.random() * 3 + 1;
            long sleepMs = (long) sleepSecond * 100;
            Thread.sleep(sleepMs);
            return new ResultHolder(
                    this.taskId,
                    sleepMs
            );
        }
    }
}
