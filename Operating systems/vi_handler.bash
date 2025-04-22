#!/bin/bash

echo $$ > /tmp/handler_pid

value=1
prev_value=1

handle_usr1() {
    value=$((value + 2))
}

handle_usr2() {
    value=$((value * 2))
}

handle_sigterm() {
    echo "Обработчик завершает работу по сигналу от другого процесса"
    exit 0
}

trap 'handle_usr1' USR1
trap 'handle_usr2' USR2
trap 'handle_sigterm' SIGTERM

while true; do
    if [ "$value" -ne "$prev_value" ]; then
        echo "Текущее значение: $value"
        prev_value=$value
    fi
    sleep 1
done
