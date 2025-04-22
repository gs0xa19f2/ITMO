package info.kgeorgiy.ja.gusev.mapper;

import info.kgeorgiy.java.advanced.iterative.*;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class IterativeParallelism implements NewListIP {

    private final ParallelMapper mapper;

    public IterativeParallelism() {
        this.mapper = null;
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

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

    @FunctionalInterface
    public interface Task<T> {
        T call() throws Exception;
    }

    private <T> List<T> executeTasks(List<Task<T>> tasks) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        List<T> results = new ArrayList<>(Collections.nCopies(tasks.size(), null));
        for (int i = 0; i < tasks.size(); i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                try {
                    results.set(index, tasks.get(index).call());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        return results;
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
        List<T> maxValues;
        if (mapper != null) {
            maxValues = mapper.map(
                    chunk -> Collections.max(chunk, comparator),
                    chunks
            );
        } else {
            List<Task<T>> tasks = chunks.stream()
                    .map(chunk -> (Task<T>) () -> Collections.max(chunk, comparator))
                    .collect(Collectors.toList());
            maxValues = executeTasks(tasks);
        }
        return Collections.max(maxValues, comparator);
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
        List<T> minValues;
        if (mapper != null) {
            minValues = mapper.map(
                    chunk -> Collections.min(chunk, comparator),
                    chunks
            );
        } else {
            List<Task<T>> tasks = chunks.stream()
                    .map(chunk -> (Task<T>) () -> Collections.min(chunk, comparator))
                    .collect(Collectors.toList());
            minValues = executeTasks(tasks);
        }
        return Collections.min(minValues, comparator);
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
        if (mapper != null) {
            List<Boolean> results = mapper.map(
                    chunk -> chunk.stream().allMatch(predicate),
                    chunks
            );
            return results.stream().allMatch(Boolean::booleanValue);
        } else {
            List<Task<Boolean>> tasks = chunks.stream()
                    .map(chunk -> (Task<Boolean>) () -> chunk.stream().allMatch(predicate))
                    .collect(Collectors.toList());
            List<Boolean> results = executeTasks(tasks);
            return results.stream().allMatch(Boolean::booleanValue);
        }
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
        if (mapper != null) {
            List<Boolean> results = mapper.map(
                    chunk -> chunk.stream().anyMatch(predicate),
                    chunks
            );
            return results.stream().anyMatch(Boolean::booleanValue);
        } else {
            List<Task<Boolean>> tasks = chunks.stream()
                    .map(chunk -> (Task<Boolean>) () -> chunk.stream().anyMatch(predicate))
                    .collect(Collectors.toList());
            List<Boolean> results = executeTasks(tasks);
            return results.stream().anyMatch(Boolean::booleanValue);
        }
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
        if (mapper != null) {
            List<Integer> counts = mapper.map(
                    chunk -> (int) chunk.stream().filter(predicate).count(),
                    chunks
            );
            return counts.stream().mapToInt(Integer::intValue).sum();
        } else {
            List<Task<Integer>> tasks = chunks.stream()
                    .map(chunk -> (Task<Integer>) () -> (int) chunk.stream().filter(predicate).count())
                    .collect(Collectors.toList());
            List<Integer> results = executeTasks(tasks);
            return results.stream().mapToInt(Integer::intValue).sum();
        }
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return join(threads, values, 1);
    }

    @Override
    public String join(int threads, List<?> values, int step) throws InterruptedException {
        List<?> stepValues = new StepList<>(values, step);
        List<List<?>> chunks = split(threads, stepValues);
        if (mapper != null) {
            List<String> results = mapper.map(
                    chunk -> chunk.stream().map(Object::toString).collect(Collectors.joining()),
                    chunks
            );
            return String.join("", results);
        } else {
            List<Task<String>> tasks = chunks.stream()
                    .map(chunk -> (Task<String>) () -> chunk.stream().map(Object::toString).collect(Collectors.joining()))
                    .collect(Collectors.toList());
            List<String> results = executeTasks(tasks);
            return String.join("", results);
        }
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
        if (mapper != null) {
            List<List<T>> results = mapper.map(
                    chunk -> chunk.stream().filter(predicate).collect(Collectors.toList()),
                    chunks
            );
            return results.stream().flatMap(List::stream).collect(Collectors.toList());
        } else {
            List<Task<List<T>>> tasks = chunks.stream()
                    .map(chunk -> (Task<List<T>>) () -> chunk.stream().filter(predicate).collect(Collectors.toList()))
                    .collect(Collectors.toList());
            List<List<T>> results = executeTasks(tasks);
            return results.stream().flatMap(List::stream).collect(Collectors.toList());
        }
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
        if (mapper != null) {
            List<List<U>> results = mapper.map(
                    chunk -> chunk.stream().map(function).collect(Collectors.toList()),
                    chunks
            );
            return results.stream().flatMap(List::stream).collect(Collectors.toList());
        } else {
            List<Task<List<U>>> tasks = chunks.stream()
                    .map(chunk -> (Task<List<U>>) () -> chunk.stream().map(function).collect(Collectors.toList()))
                    .collect(Collectors.toList());
            List<List<U>> results = executeTasks(tasks);
            return results.stream().flatMap(List::stream).collect(Collectors.toList());
        }
    }
}

