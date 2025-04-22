#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/hello/*.class && sleep 0.5
echo "Old classes are removed" && sleep 0.5

# Компиляция всех классов, включая HelloUDPClient и HelloUDPServer
javac -cp "java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.hello.jar:java-advanced-2024/lib/*" \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/hello/HelloUDPClient.java \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/hello/HelloUDPServer.java \

# Запуск серверной части
java -cp "java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.hello.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.hello.Tester new-server-i18n info.kgeorgiy.ja.gusev.hello.HelloUDPServer

# Запуск клиентской части
java -cp "java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.hello.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.hello.Tester new-client-i18n info.kgeorgiy.ja.gusev.hello.HelloUDPClient
