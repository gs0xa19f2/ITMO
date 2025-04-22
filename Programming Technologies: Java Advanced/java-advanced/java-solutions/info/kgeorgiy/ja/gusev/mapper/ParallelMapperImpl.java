package info.kgeorgiy.ja.gusev.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> workers;
    private final Queue<Runnable> taskQueue;
    private volatile boolean isShutdown = false;

    public ParallelMapperImpl(int threads) {
        taskQueue = new ArrayDeque<>();
        workers = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            Thread worker = new Thread(() -> {
                try {
                    while (!isShutdown) {
                        Runnable task;
                        synchronized (taskQueue) {
                            while (taskQueue.isEmpty() && !isShutdown) {
                                taskQueue.wait();
                            }
                            if (isShutdown && taskQueue.isEmpty()) {
                                break;  
                            }
                            task = taskQueue.poll();
                        }
                        if (task != null) {
                            task.run();
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            });
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> function, List<? extends T> items) throws InterruptedException {
        List<R> results = new ArrayList<>(Collections.nCopies(items.size(), null));
        final Object lock = new Object();
        int tasksCount = items.size();
        final CountDownLatch latch = new CountDownLatch(tasksCount);  

        for (int i = 0; i < tasksCount; i++) {
            final int index = i;
            T item = items.get(index);
            synchronized (taskQueue) {
                taskQueue.add(() -> {
                    R result = function.apply(item);
                    synchronized (lock) {
                        results.set(index, result);
                    }
                    latch.countDown();
                });
                taskQueue.notify();  
            }
        }

        latch.await();  

        return results;
    }

    private static class CountDownLatch {
        private int count;

        public CountDownLatch(int count) {
            this.count = count;
        }

        public synchronized void countDown() {
            if (--count <= 0) {
                notifyAll();
            }
        }

        public synchronized void await() throws InterruptedException {
            while (count > 0) {
                wait();
            }
        }
    }

    @Override
    public void close() {
        synchronized (taskQueue) {
            isShutdown = true;
            taskQueue.notifyAll();  
        }
        for (Thread worker : workers) {
            try {
                worker.join();  
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

