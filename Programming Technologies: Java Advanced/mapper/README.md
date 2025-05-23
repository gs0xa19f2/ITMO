# ParallelMapper

## Описание

`ParallelMapper` — это интерфейс и его реализация `ParallelMapperImpl`, предназначенные для распараллеливания вычислений над коллекциями.

---

## Основные особенности

- Поддержка многопоточной обработки данных.
- Управление задачами с использованием очередей и потоков.
- Удобный интерфейс для интеграции с другими инструментами многопоточности.

---

## API

### Основные методы
- **`map(Function<? super T, ? extends R> function, List<? extends T> items)`**: Применяет функцию ко всем элементам списка параллельно.
