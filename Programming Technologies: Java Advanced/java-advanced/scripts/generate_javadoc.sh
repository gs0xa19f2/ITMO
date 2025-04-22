#!/bin/bash

# Устанавливаем директории
SRC_DIR="java-solutions"
JAVADOC_DIR="javadoc"
ADVANCED_PATH="scripts/jarimplementor.jar"

# Очистка предыдущих версий Javadoc
echo "Очистка предыдущих версий Javadoc..."
rm -rf $JAVADOC_DIR
mkdir -p $JAVADOC_DIR
echo "Очистка завершена."

# Генерация Javadoc
echo "Генерация Javadoc..."
javadoc -d $JAVADOC_DIR -sourcepath $SRC_DIR -cp "$ADVANCED_PATH" \
    -link https://docs.oracle.com/en/java/javase/11/docs/api/ \
    -private -author -version \
    $(find $SRC_DIR -name "Implementor.java" -or -name "JarImplementor.java")

if [ $? -ne 0 ]; then
    echo "Ошибка генерации Javadoc."
    exit 1
fi

echo "Javadoc успешно сгенерирован в $JAVADOC_DIR"

