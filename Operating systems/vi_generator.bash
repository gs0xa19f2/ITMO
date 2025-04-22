#!/bin/bash

handler_pid=$(cat /tmp/handler_pid)
rm /tmp/handler_pid
send_signal() {
    kill -$1 $handler_pid 2>/dev/null
}

while true; do
    read -r line
    case $line in
        "+")
            send_signal "USR1"
            ;;
        "*")
            send_signal "USR2"
            ;;
        "TERM")
            send_signal "TERM"
            exit 0
            ;;
        *)
            :
            ;;
    esac
done

