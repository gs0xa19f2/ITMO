# SyntaxClasses

## Описание
Каталог содержит задачи, связанные с использованием синтаксиса и классов в Kotlin. Каждая задача реализована в виде отдельных Kotlin-файлов, которые демонстрируют применение классов, делегатов, sealed-классов и других возможностей языка.

---

## Задачи

### 1. Банковский счёт (`bank_account`)
**Описание задачи**: Реализовать класс для управления банковским счётом. Класс должен поддерживать функции пополнения баланса, снятия средств и проверки текущего баланса. Также необходимо вести логирование транзакций.

**Файлы**:
- `BankAccount.kt`: Реализует класс банковского счёта с методами `deposit` и `withdraw`.
- `LogTransaction.kt`: Функция для логирования изменений баланса.

**Описание реализации**:
- Класс `BankAccount` содержит поле `balance`, которое обновляется через сеттер с автоматическим логированием.
- Методы:
  - `deposit(dep: Int)`: Пополняет баланс, проверяя корректность суммы.
  - `withdraw(with: Int)`: Снимает средства с проверкой на лимит.

---

### 2. Конфигурация из файла (`config_from_file`)
**Описание задачи**: Создать утилиту для загрузки конфигураций из файла и преобразования их в объект. Реализовать возможность доступа к значениям конфигурации через делегаты.

**Файлы**:
- `Config.kt`: Класс для обработки и хранения конфигураций.
- `GetResource.kt`: Утилита для загрузки содержимого конфигурационного файла.

**Описание реализации**:
- Класс `Config` парсит конфигурационный файл в `Map<String, String>`.
- Используется оператор `provideDelegate` для доступа к значениям конфигурации через свойства.

---

### 3. Работа с матрицами (`matrix`)
**Описание задачи**: Реализовать класс для работы с матрицами, включая операции чтения и записи элементов.

**Файлы**:
- `IntMatrix.kt`: Класс для представления целочисленной матрицы.

**Описание реализации**:
- Класс `IntMatrix` содержит двумерный массив, представленный в виде одномерного массива.
- Методы:
  - `get(row: Int, col: Int)`: Возвращает значение элемента матрицы.
  - `set(row: Int, col: Int, value: Int)`: Устанавливает значение элемента.

---

### 4. Обработка результатов (`result`)
**Описание задачи**: Создать класс для обработки результатов операций с поддержкой сообщений об ошибках.

**Файлы**:
- `IntResult.kt`: Реализует `sealed class` для представления успешных и неуспешных результатов.

**Описание реализации**:
- `IntResult` имеет два состояния: `Ok` (успех) и `Error` (ошибка).
- Методы:
  - `getOrDefault(default: Int)`: Возвращает значение или значение по умолчанию в случае ошибки.
  - `getStrict()`: Генерирует исключение при ошибке.
