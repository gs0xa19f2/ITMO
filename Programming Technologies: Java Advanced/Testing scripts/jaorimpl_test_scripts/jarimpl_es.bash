#!/bin/bash

# Компиляция всех классов: Implementor, JarImplementor, и другие необходимые классы
javac -cp ".:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/implementor/Implementor.java \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/jarimplementor/JarImplementor.java

# Запуск тестов для задания 5 (Jar Implementor)
java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.implementor.Tester jar-interface info.kgeorgiy.ja.gusev.jarimplementor.JarImplementor

