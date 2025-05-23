# Операционные системы

## Описание

Каталог содержит Bash-скрипты и материалы, связанные с лабораторной работой №5 по предмету "Операционные системы". Основное внимание уделено выполнению заданий, связанных с управлением процессами, взаимодействием между процессами, использованием памяти и синхронизацией.
---

## Структура каталога

### 1. Управление процессами
- `i.bash`: Создает каталог, файл с текущей датой и временем, и проверяет доступность сайта с записью результата в отчет.
- `ii.bash`: Использует планировщик задач `at` для выполнения скрипта `i.bash` через 2 минуты.
- `iii.bash`: Добавляет задачу в `crontab` для автоматического выполнения скрипта `i.bash` раз в неделю.

### 2. Управление и запуск процессов
- `iv_start.bash`: Запускает три бесконечных цикла в фоновом режиме и записывает их PID в файл `process_pids.txt`.
- `iv_manage.bash`: Управляет процессами из `process_pids.txt`, ограничивая использование CPU для одного из них и завершая другой.

### 3. Взаимодействие через именованный канал
- `v_generator.bash`: Генерирует сообщения и передает их через именованный канал (`pipe`).
- `v_handler.bash`: Обрабатывает данные, полученные через `pipe`. Поддерживает операции сложения и умножения, а также завершение работы по команде `QUIT`.

### 4. Сигнальное взаимодействие между процессами
- `vi_generator.bash`: Отправляет сигналы (`USR1`, `USR2`, `TERM`) другому процессу на основе пользовательского ввода.
- `vi_handler.bash`: Обрабатывает сигналы, изменяя значение переменной и завершая работу по сигналу `TERM`.

### 5. Отчет
- `Лабораторная работа №5.pdf`: Подробное описание экспериментов с анализом использования памяти, управления процессами и синхронизации.
