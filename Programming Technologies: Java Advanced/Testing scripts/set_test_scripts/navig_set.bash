#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/arrayset/*.class

javac -cp . java-advanced/java-solutions/info/kgeorgiy/ja/gusev/arrayset/ArraySet.java

java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.arrayset.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" info.kgeorgiy.java.advanced.arrayset.Tester NavigableSet info.kgeorgiy.ja.gusev.arrayset.ArraySet
