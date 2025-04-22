#!/bin/bash

rm java-advanced/java-solutions/info/kgeorgiy/ja/gusev/crawler/*.class

# Компиляция всех классов, включая WebCrawler
javac -cp ".:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.crawler.jar:java-advanced-2024/lib/*" \
    java-advanced/java-solutions/info/kgeorgiy/ja/gusev/crawler/WebCrawler.java

# Запуск тестов (Web Crawler)
java -cp ".:java-advanced/java-solutions:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.crawler.jar:java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.base.jar:java-advanced-2024/lib/*" \
    info.kgeorgiy.java.advanced.crawler.Tester hard info.kgeorgiy.ja.gusev.crawler.WebCrawler

