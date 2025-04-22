#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/student/*.class 2> /dev/null

javac -cp .:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.student.jar java-advanced/java-solutions/info/kgeorgiy/ja/gusev/student/StudentDB.java

java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.student.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" info.kgeorgiy.java.advanced.student.Tester StudentQuery info.kgeorgiy.ja.gusev.student.StudentDB
