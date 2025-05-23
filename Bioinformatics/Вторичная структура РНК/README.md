# Вторичная структура РНК

## Содержание

1. **rna_secondary_structure.py**: Реализация алгоритма Нуссинова для вычисления максимального числа пар оснований в последовательности РНК.
2. **rna_structure_traceback.py**: Вычисление оптимальной вторичной структуры РНК с восстановлением пути (traceback).

---

## Описание файлов

### 1. rna_secondary_structure.py
Этот файл реализует алгоритм Нуссинова, который используется для нахождения максимального числа пар оснований в последовательности РНК. Алгоритм включает:
- Проверку возможности образования пар оснований (A-U, C-G).
- Построение динамической матрицы для подсчета максимального числа пар.

**Вывод**: Максимальное число пар оснований.

#### Пример работы программы:
#### Ввод:
```
Введите последовательность РНК: GCAUCUAUG
```
#### Вывод:
```
Максимальное число пар оснований: 3
```

---

### 2. rna_structure_traceback.py
Этот файл расширяет функциональность алгоритма Нуссинова, добавляя восстановление оптимальной вторичной структуры РНК. Основные шаги:
- Построение динамической матрицы.
- Восстановление структуры в виде скобочной нотации (например, `((..))`).

**Вывод**: Оптимальная вторичная структура РНК.

#### Пример работы программы:
#### Ввод:
```
Введите последовательность РНК: GCAUCUAUG
```
#### Вывод:
```
Оптимальная вторичная структура РНК: (.(..).)
```

---
