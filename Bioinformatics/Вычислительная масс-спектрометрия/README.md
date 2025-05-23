# Вычислительная масс-спектрометрия

## Содержание

1. **theoretical_spectrum.py**: Вычисление теоретического спектра пептида.
2. **spectrum_to_peptide.py**: Восстановление последовательности пептида по спектру.
3. **best_protein_match.py**: Поиск лучшего пептида, соответствующего спектральному вектору.

---

## Описание файлов

### 1. theoretical_spectrum.py
Этот файл вычисляет теоретический спектр пептида. Спектр включает массы всех возможных фрагментов, включая префиксы и суффиксы.

**Пример работы программы:**
#### Ввод:
```
Введите последовательность пептида: ACDE
```
#### Вывод:
```
Теоретический спектр: 0 71 103 174 289 403
```

---

### 2. spectrum_to_peptide.py
Этот файл позволяет восстановить последовательность пептида по заданному спектру. Используются массы аминокислот для сопоставления спектра с возможными последовательностями.

**Пример работы программы:**
#### Ввод:
```
Введите спектр пептида: 0 71 103 174 289 403
```
#### Вывод:
```
Последовательность пептида: ACDE
```

---

### 3. best_protein_match.py
Этот файл находит лучший пептид в заданной последовательности протеома, соответствующий спектральному вектору. Для оценки соответствия используется метрика "оценка спектра".

**Пример работы программы:**
#### Ввод:
```
Введите спектральный вектор: 0 71 103 174 289 403
Введите последовательность протеома: ACDEF
```
#### Вывод:
```
Лучший пептид: ACDE
```

---
