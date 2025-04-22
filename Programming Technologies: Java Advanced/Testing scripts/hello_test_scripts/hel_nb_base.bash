#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/hello/*.class && sleep 1
echo "Old classes are removed" && sleep 1

# Компиляция всех классов, включая HelloUDPClient и HelloUDPServer
javac -cp "java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.hello.jar:java-advanced-2024/lib/*" \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/hello/HelloUDPNonblockingClient.java \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/hello/HelloUDPNonblockingServer.java \

# Запуск серверной части
java -cp "java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.hello.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.hello.Tester server info.kgeorgiy.ja.gusev.hello.HelloUDPNonblockingServer

# Запуск клиентской части
java -cp "java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.hello.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.hello.Tester client info.kgeorgiy.ja.gusev.hello.HelloUDPNonblockingClient

