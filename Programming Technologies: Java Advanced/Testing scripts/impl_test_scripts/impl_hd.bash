#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/implementor/*.class 2> /dev/null

javac -cp .:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar java-advanced/java-solutions/info/kgeorgiy/ja/gusev/implementor/Implementor.java

java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" info.kgeorgiy.java.advanced.implementor.Tester class info.kgeorgiy.ja.gusev.implementor.Implementor
