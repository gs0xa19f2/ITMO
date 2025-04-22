package info.kgeorgiy.ja.gusev.tracingproxy.tests;

import info.kgeorgiy.ja.gusev.tracingproxy.TracingProxy;

// Простые тесты для базовой проверки работоспособности кода

public class TracingProxyTest {
    public static void main(String[] args) {
        Calculator calculator = new SimpleCalculator();

        Calculator proxy = (Calculator) TracingProxy.create(calculator, 2);

        int result1 = proxy.add(5, 3);
        System.out.println("Result of add: " + result1);
        System.out.println();

        int result2 = proxy.subtract(10, 4);
        System.out.println("Result of subtract: " + result2);
        System.out.println();

        Calculator nestedProxy = proxy.getCalculator();
        int result3 = nestedProxy.add(2, 2);
        System.out.println("Result of nested add: " + result3);
        System.out.println();

        try {
            proxy.divide(10, 0); // Должно вызвать ArithmeticException
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }
}

