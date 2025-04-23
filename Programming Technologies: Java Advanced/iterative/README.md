# IterativeParallelism

## Описание

`IterativeParallelism` — это реализация различных операций над данными с использованием многопоточности. Поддерживает операции фильтрации, поиска, преобразования и агрегации.

---

## Основные особенности

- Параллельное выполнение операций над коллекциями.
- Использование потоков для повышения производительности.
- Поддержка операций, таких как `maximum`, `minimum`, `filter`, `map`, `join`, `all`, `any`.

---

## API

### Основные методы
- **`maximum(int threads, List<? extends T> values, Comparator<? super T> comparator)`**: Возвращает максимальное значение в коллекции.
- **`minimum(int threads, List<? extends T> values, Comparator<? super T> comparator)`**: Возвращает минимальное значение в коллекции.
- **`filter(int threads, List<? extends T> values, Predicate<? super T> predicate)`**: Возвращает элементы, соответствующие условию.
