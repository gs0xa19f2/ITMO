#!/bin/bash

CLASSPATH=".:./info"

find ./info -name "*.java" > sources.txt

javac -cp "$CLASSPATH" @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    java -cp "$CLASSPATH" info.kgeorgiy.ja.gusev.tracingproxy.tests.TracingProxyTest
else
    echo "Compilation failed."
fi

rm sources.txt

