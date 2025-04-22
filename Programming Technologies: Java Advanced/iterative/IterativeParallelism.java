package info.kgeorgiy.ja.gusev.iterative;

import info.kgeorgiy.java.advanced.iterative.*;

import java.util.*;
import java.util.function.*;

public class IterativeParallelism implements NewListIP {

    private <T> List<List<? extends T>> split(int threads, List<? extends T> values) {
        int n = Math.min(threads, values.size());
        List<List<? extends T>> result = new ArrayList<>(n);
        int chunkSize = values.size() / n;
        int remainder = values.size() % n;
        int start = 0;
        for (int i = 0; i < n; i++) {
            int end = start + chunkSize + (i < remainder ? 1 : 0);
            result.add(values.subList(start, end));
            start = end;
        }
        return result;
    }

    private static class StepList<T> extends AbstractList<T> {
        private final List<? extends T> list;
        private final int step;
        private final int size;

        public StepList(List<? extends T> list, int step) {
            this.list = list;
            this.step = step;
            this.size = (list.size() + step - 1) / step;
        }

        @Override
        public T get(int index) {
            int actualIndex = index * step;
            if (actualIndex >= list.size()) {
                throw new IndexOutOfBoundsException();
            }
            return list.get(actualIndex);
        }

        @Override
        public int size() {
            return size;
        }
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator, 1);
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        if (stepValues.isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        List<List<? extends T>> chunks = split(threads, stepValues);
        int n = chunks.size();
        @SuppressWarnings("unchecked")
        T[] maxValues = (T[]) new Object[n];
        Thread[] threadArray = new Thread[n];
        for (int i = 0; i < n; i++) {
            final List<? extends T> chunk = chunks.get(i);
            final int index = i;
            threadArray[i] = new Thread(() -> {
                T max = Collections.max(chunk, comparator);
                maxValues[index] = max;
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        return Collections.max(Arrays.asList(maxValues), comparator);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator) throws InterruptedException {
        return minimum(threads, values, comparator, 1);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        if (stepValues.isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        List<List<? extends T>> chunks = split(threads, stepValues);
        int n = chunks.size();
        @SuppressWarnings("unchecked")
        T[] minValues = (T[]) new Object[n];
        Thread[] threadArray = new Thread[n];
        for (int i = 0; i < n; i++) {
            final List<? extends T> chunk = chunks.get(i);
            final int index = i;
            threadArray[i] = new Thread(() -> {
                T min = Collections.min(chunk, comparator);
                minValues[index] = min;
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        return Collections.min(Arrays.asList(minValues), comparator);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return all(threads, values, predicate, 1);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values,
                           Predicate<? super T> predicate, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        List<List<? extends T>> chunks = split(threads, stepValues);
        boolean[] result = {true};
        Thread[] threadArray = new Thread[chunks.size()];
        for (int i = 0; i < chunks.size(); i++) {
            final List<? extends T> chunk = chunks.get(i);
            threadArray[i] = new Thread(() -> {
                for (T value : chunk) {
                    synchronized (result) {
                        if (!result[0]) {
                            return;
                        }
                    }
                    if (!predicate.test(value)) {
                        synchronized (result) {
                            result[0] = false;
                        }
                        return;
                    }
                }
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        return result[0];
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return any(threads, values, predicate, 1);
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values,
                           Predicate<? super T> predicate, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        List<List<? extends T>> chunks = split(threads, stepValues);
        boolean[] result = {false};
        Thread[] threadArray = new Thread[chunks.size()];
        for (int i = 0; i < chunks.size(); i++) {
            final List<? extends T> chunk = chunks.get(i);
            threadArray[i] = new Thread(() -> {
                for (T value : chunk) {
                    synchronized (result) {
                        if (result[0]) {
                            return;
                        }
                    }
                    if (predicate.test(value)) {
                        synchronized (result) {
                            result[0] = true;
                        }
                        return;
                    }
                }
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        return result[0];
    }

    @Override
    public <T> int count(int threads, List<? extends T> values,
                         Predicate<? super T> predicate) throws InterruptedException {
        return count(threads, values, predicate, 1);
    }

    @Override
    public <T> int count(int threads, List<? extends T> values,
                         Predicate<? super T> predicate, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        List<List<? extends T>> chunks = split(threads, stepValues);
        int n = chunks.size();
        int[] counts = new int[n];
        Thread[] threadArray = new Thread[n];
        for (int i = 0; i < n; i++) {
            final List<? extends T> chunk = chunks.get(i);
            final int index = i;
            threadArray[i] = new Thread(() -> {
                int count = 0;
                for (T value : chunk) {
                    if (predicate.test(value)) {
                        count++;
                    }
                }
                counts[index] = count;
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        int total = 0;
        for (int count : counts) {
            total += count;
        }
        return total;
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return join(threads, values, 1);
    }

    @Override
    public String join(int threads, List<?> values, int step) throws InterruptedException {
        List<?> stepValues = new StepList<>(values, step);
        List<List<?>> chunks = split(threads, stepValues);
        int n = chunks.size();
        String[] results = new String[n];
        Thread[] threadArray = new Thread[n];
        for (int i = 0; i < n; i++) {
            final List<?> chunk = chunks.get(i);
            final int index = i;
            threadArray[i] = new Thread(() -> {
                StringBuilder sb = new StringBuilder();
                for (Object value : chunk) {
                    sb.append(value.toString());
                }
                results[index] = sb.toString();
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        StringBuilder finalResult = new StringBuilder();
        for (String result : results) {
            finalResult.append(result);
        }
        return finalResult.toString();
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values,
                              Predicate<? super T> predicate) throws InterruptedException {
        return filter(threads, values, predicate, 1);
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values,
                              Predicate<? super T> predicate, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        List<List<? extends T>> chunks = split(threads, stepValues);
        int n = chunks.size();
        List<List<T>> results = new ArrayList<>(Collections.nCopies(n, null));
        Thread[] threadArray = new Thread[n];
        for (int i = 0; i < n; i++) {
            final List<? extends T> chunk = chunks.get(i);
            final int index = i;
            threadArray[i] = new Thread(() -> {
                List<T> result = new ArrayList<>();
                for (T value : chunk) {
                    if (predicate.test(value)) {
                        result.add(value);
                    }
                }
                results.set(index, result);
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        List<T> finalResult = new ArrayList<>();
        for (List<T> result : results) {
            finalResult.addAll(result);
        }
        return finalResult;
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values,
                              Function<? super T, ? extends U> function) throws InterruptedException {
        return map(threads, values, function, 1);
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values,
                              Function<? super T, ? extends U> function, int step) throws InterruptedException {
        List<? extends T> stepValues = new StepList<>(values, step);
        List<List<? extends T>> chunks = split(threads, stepValues);
        int n = chunks.size();
        List<List<U>> results = new ArrayList<>(Collections.nCopies(n, null));
        Thread[] threadArray = new Thread[n];
        for (int i = 0; i < n; i++) {
            final List<? extends T> chunk = chunks.get(i);
            final int index = i;
            threadArray[i] = new Thread(() -> {
                List<U> result = new ArrayList<>(chunk.size());
                for (T value : chunk) {
                    result.add(function.apply(value));
                }
                results.set(index, result);
            });
            threadArray[i].start();
        }
        for (Thread thread : threadArray) {
            thread.join();
        }
        List<U> finalResult = new ArrayList<>();
        for (List<U> result : results) {
            finalResult.addAll(result);
        }
        return finalResult;
    }
}

