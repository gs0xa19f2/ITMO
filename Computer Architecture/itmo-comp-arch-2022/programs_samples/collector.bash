#!/bin/bash

# Имя файла, в который будет записываться весь контент
output_file="all_files_content.txt"

# Очищаем файл, если он уже существует
> "$output_file"

# Функция для рекурсивного обхода директорий
process_directory() {
    for file in "$1"/*; do
        if [ -d "$file" ]; then
            # Если это директория, рекурсивно обрабатываем её
            process_directory "$file"
        elif [ -f "$file" ]; then
            # Пропускаем файлы с расширениями .class, .jar, .jpg, .png и .pp
            if [[ "$file" != *.class && "$file" != *.jar && "$file" != *.jpg && "$file" != *.png && "$file" != *.pp ]]; then
                # Если это файл, добавляем его имя и содержимое в итоговый файл
                echo "===== $file =====" >> "$output_file"
                cat "$file" >> "$output_file"
                echo -e "\n" >> "$output_file"
            fi
        fi
    done
}

# Начинаем с текущей директории или директории, переданной первым аргументом
start_dir="${1:-.}"

# Запускаем обработку
process_directory "$start_dir"

echo "Содержимое всех файлов записано в $output_file."

