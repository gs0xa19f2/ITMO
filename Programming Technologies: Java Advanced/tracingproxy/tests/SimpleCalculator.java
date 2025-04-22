package info.kgeorgiy.ja.gusev.tracingproxy.tests;

public class SimpleCalculator implements Calculator {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int subtract(int a, int b) {
        return a - b;
    }

    @Override
    public int divide(int a, int b) {
        return a / b; // Бросит ArithmeticException, если b == 0
    }

    @Override
    public Calculator getCalculator() {
        return this;
    }
}

