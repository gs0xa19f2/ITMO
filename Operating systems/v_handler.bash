#!/bin/bash

pipe="/tmp/my_pipe"

if [[ ! -p $pipe ]]; then
    echo "Именованный канал не найден"
    exit 1
fi

mode="add"
result=1

perform_operation() {
    case $mode in
        "add")
            result=$((result + $1))
            ;;
        "mul")
            result=$((result * $1))
            ;;
    esac
    echo "Текущее значение: $result"
}

while true; do
    if read -r line < $pipe; then
        case $line in
            "+")
                mode="add"
                ;;
            "*")
                mode="mul"
                ;;
            "QUIT")
                echo "Плановая остановка"
                break
                ;;
            *)
                if [[ $line =~ ^-?[0-9]+$ ]]; then
                    perform_operation $line
                else
                    echo "Ошибка: неверный ввод"
                    break
                fi
                ;;
        esac
    fi
done

rm $pipe

