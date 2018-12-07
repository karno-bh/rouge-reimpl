package il.ac.sce.ir.metric.concrete_metric.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestWithMain implements Runnable {

    private final int me;

    public TestWithMain(int me) {
        this.me = me;
    }

    public void test() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<?>> futures = new ArrayList<>();
        futures.add(executorService.submit(new TestWithMain(me + 1)));
        futures.add(executorService.submit(new TestWithMain(me + 2)));
        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    @Override
    public void run() {
        try {
            Process p = Runtime.getRuntime().exec("java -cp \".\\target\\classes\" il.ac.sce.ir.metric.concrete_metric.common.util.TestWithMain " + me);
            InputStream pInputStream = p.getInputStream();
            int data;
            while ((data = pInputStream.read()) != -1) {
                char c = (char)data;
                System.out.print(c);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Hello World!" + args[0]);
        Thread.sleep(5000);
        System.exit(42);
    }
}
