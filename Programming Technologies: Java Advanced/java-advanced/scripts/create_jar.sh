#!/bin/bash

# Устанавливаем директории
SRC_DIR="java-solutions"
BUILD_DIR="build"
JAR_NAME="scripts/jarimplementor.jar"
MANIFEST="scripts/MANIFEST.MF"
ADVANCED_PATH="../java-advanced-2024/artifacts"
LIB_PATH="../java-advanced-2024/lib"

# Очистка предыдущих сборок
rm -rf $BUILD_DIR

# Компиляция исходного кода
javac -d $BUILD_DIR -cp "$ADVANCED_PATH/info.kgeorgiy.java.advanced.implementor.jar:$LIB_PATH/*" $(find $SRC_DIR -name "Implementor.java" -or -name "JarImplementor.java")

if [ $? -ne 0 ]; then
    echo "Компиляция завершилась с ошибкой."
    exit 1
fi

# Создание JAR-файла
jar cfm $JAR_NAME $MANIFEST -C $BUILD_DIR .

if [ $? -ne 0 ]; then
    echo "Ошибка создания JAR-файла."
    exit 1
fi

# Удаление временной директории build
rm -rf $BUILD_DIR

echo "JAR-файл успешно создан: $JAR_NAME"

