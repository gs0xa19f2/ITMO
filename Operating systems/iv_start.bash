#!/bin/bash

pid_file="process_pids.txt"

infinite_loop() {
    while true; do
        result=$(( 123 * 456 )) 
    done
}

echo "Запуск первого процесса"
infinite_loop &
echo $! >> "$pid_file"
echo "Запущен процесс с PID: $!"

echo "Запуск второго процесса"
infinite_loop &
echo $! >> "$pid_file"
echo "Запущен процесс с PID: $!"

echo "Запуск третьего процесса"
infinite_loop &
echo $! >> "$pid_file"
echo "Запущен процесс с PID: $!"

