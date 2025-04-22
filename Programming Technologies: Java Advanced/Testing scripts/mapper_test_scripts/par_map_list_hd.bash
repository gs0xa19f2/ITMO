#!/bin/bash

# Компиляция всех классов, включая IterativeParallelism и ParallelMapperImpl
javac -cp ".:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.mapper.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.iterative.jar:java-advanced-2024/lib/*" \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/mapper/ParallelMapperImpl.java \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/mapper/IterativeParallelism.java

# Запуск тестов (ListIP)
java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.mapper.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.iterative.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.mapper.Tester new-list info.kgeorgiy.ja.gusev.mapper.IterativeParallelism

