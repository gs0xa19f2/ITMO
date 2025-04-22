#!/bin/bash

# Устанавливаем переменные
JAR_PATH="scripts/jarimplementor.jar"
CLASS_NAME="info.kgeorgiy.java.advanced.implementor.Impler"
OUTPUT_JAR="scripts/out.jar"

# Проверяем наличие JAR-файла
if [ ! -f "$JAR_PATH" ]; then
    echo "Ошибка: JAR-файл $JAR_PATH не найден. Сначала сгенерируйте JAR-файл."
    exit 1
fi

# Запуск теста
echo "Запуск JAR-файла для класса $CLASS_NAME..."
java -jar "$JAR_PATH" -jar "$CLASS_NAME" "$OUTPUT_JAR"

# Проверяем результат выполнения
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении JAR-файла."
    exit 1
fi

echo "Тест JAR-файла успешно завершён. Сгенерирован файл: $OUTPUT_JAR"

