#!/bin/bash

pid_file="process_pids.txt"

readarray -t pid_array < "$pid_file"

cpulimit -p ${pid_array[0]} -l 10 &

kill -9 ${pid_array[2]}

top -p ${pid_array[0]}

