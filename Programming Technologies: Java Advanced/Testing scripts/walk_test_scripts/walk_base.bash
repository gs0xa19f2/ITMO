#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/walk/*.class

javac -cp . java-advanced/java-solutions/info/kgeorgiy/ja/gusev/walk/Walk.java java-advanced/java-solutions/info/kgeorgiy/ja/gusev/walk/RecursiveWalk.java

java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.walk.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" info.kgeorgiy.java.advanced.walk.Tester Walk info.kgeorgiy.ja.gusev.walk.Walk

