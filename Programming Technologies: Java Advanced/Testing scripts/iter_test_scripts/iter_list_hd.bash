#!/bin/bash

# Компиляция всех классов, включая IterativeParallelism для сложных тестов
javac -cp ".:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.iterative.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/iterative/IterativeParallelism.java

# Запуск сложных тестов (ListIP)
java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.iterative.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.iterative.Tester new-list info.kgeorgiy.ja.gusev.iterative.IterativeParallelism

