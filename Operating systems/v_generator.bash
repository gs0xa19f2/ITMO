#!/bin/bash

pipe="/tmp/my_pipe"

[[ ! -p $pipe ]] && mkfifo $pipe

while IFS= read -r line; do
    echo "$line" > $pipe
done

