# MIPS CPU Implementation

## Описание

Этот каталог содержит реализацию простого однотактного процессора на основе архитектуры MIPS. Используя язык Verilog, процессор поддерживает базовые инструкции, чтение/запись в память, а также взаимодействие с регистровым файлом.

---

## Содержание

### Основные модули

1. **`mips_cpu.v`**:
   - Реализация управляющего устройства процессора.
   - Поддержка базовых инструкций MIPS: `lw`, `sw`, `add`, `sub`, `and`, `or`, `slt`, `beq`, `bne`, `addi`, `andi`, `j`, `jal`, и бонусной инструкции `jr`.
   - Логика выбора следующего значения PC (Program Counter).

2. **Дополнительные модули**:
   - **`util.v`**: Реализация вспомогательных модулей:
     - Расширение знаковой константы (`sign_extend`).
     - Побитовый сдвиг влево на 2 бита (`shl_2`).
     - 32-битный сумматор (`adder`).
     - Мультиплексоры (`mux2_32`, `mux2_5`).
   - **`d_flop.v`**: Реализация триггера для хранения значения PC.
   - **`register_file.v`**: Реализация регистрового файла с 32 регистрами по 32 бита.
   - **`memory.v`**: Реализация памяти данных и памяти инструкций.

3. **Тесты**:
   - **`cpu_test.v`**: Тестирование работы процессора. Выполняется тестовая программа и записывается состояние регистров и памяти.

4. **Программы для тестирования**:
   - Директория `dat` содержит набор тестовых программ в формате MIPS ассемблера:
     - **`arith.dat`**: Проверка арифметических операций.
     - **`memory.dat`**: Проверка операций чтения/записи в память.
     - **`branch.dat`**: Проверка работы условных переходов.
     - **`hello_world.dat`**: Загрузка строки "Hello world!" в память.
     - **`comparch_is_cool.dat`**: Генерация строки "CompArch is coool! :D".
     - **`jump.dat`**: Проверка работы инструкций `j` и `jal`.
     - **`bne1.dat` и `bne2.dat`**: Тестирование работы инструкции `bne`.
     - **`jr.dat`**: Проверка работы инструкции `jr`.

---

## Запуск и тестирование

### Установка инструментов
Для работы с проектом требуется установка следующих инструментов:
- **Icarus Verilog** — симулятор Verilog. [Скачать здесь](https://bleyer.org/icarus/).
- **GTK Wave** — для визуализации сигналов. [Скачать здесь](https://gtkwave.sourceforge.net/).

## Пример работы

### Пример выполнения программы `hello_world.dat`
После выполнения теста `cpu_test.v`, вы получите вывод состояния регистров и памяти данных. Пример:
```
Register:  8, value:  72
Register:  9, value: 101
...
Addr:      0, value:  72
Addr:      4, value: 101
Addr:      8, value: 108
Addr:     12, value: 108
Addr:     16, value: 111
Addr:     20, value:  32
Addr:     24, value: 119
Addr:     28, value: 111
Addr:     32, value: 114
Addr:     36, value: 108
Addr:     40, value: 100
Addr:     44, value:  33
```

Строка "Hello world!" была записана в память данных.
