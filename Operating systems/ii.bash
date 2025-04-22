#!/bin/bash
# sudo systemctl start atd - для запуска atd-демона
touch ~/report
echo "bash /home/grey/Documents/Common_Directory/lab3/i.bash" | at now + 2 minutes
tail -f ~/report


