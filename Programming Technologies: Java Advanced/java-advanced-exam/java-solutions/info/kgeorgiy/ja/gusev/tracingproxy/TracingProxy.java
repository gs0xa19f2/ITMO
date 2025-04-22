package info.kgeorgiy.ja.gusev.tracingproxy;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Collectors;

// 99. TracingProxy

// Задача фактически заключается в написании класса, реализующего интерфейс InvocationHandler из пакета java.lang.reflect
public class TracingProxy implements InvocationHandler {

    private final Object target; // Integer(5); 
    private final int depth;
    
    private TracingProxy(Object target, int depth) {
        this.target = target;
        this.depth = depth;
    }

// Статический метод для создания прокси-объекта
    public static Object create(Object target, int depth) {
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative");
        }
        Class<?> targetClass = target.getClass();
        Class<?>[] interfaces = getAllInterfaces(targetClass);

        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Target object does not implement any interfaces");
        }

        return Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                interfaces,
                new TracingProxy(target, depth)
        );
    }

// Метод, возвращающий массив всех интерфейсов, реализованных классом и его суперклассами
    private static Class<?>[] getAllInterfaces(Class<?> clazz) {
        if (clazz == null) {
            return new Class<?>[0];
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        Class<?>[] superInterfaces = getAllInterfaces(clazz.getSuperclass());
        Class<?>[] allInterfaces = Arrays.copyOf(interfaces, interfaces.length + superInterfaces.length);
        System.arraycopy(superInterfaces, 0, allInterfaces, interfaces.length, superInterfaces.length);
        return Arrays.stream(allInterfaces).distinct().toArray(Class<?>[]::new);
    }

// Метод перехвата вызовов методов прокси-объекта.
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logMethodCall(method, args);

        try {
            Object result = method.invoke(target, args);

            logMethodReturn(method, result);

            if (depth > 0 && result != null && !isPrimitiveOrWrapper(result.getClass())) {
                result = create(result, depth - 1);
            }

            return result;
        } catch (InvocationTargetException e) {
            logMethodException(method, e.getCause());
            throw e.getCause();
        }
    }


// Вспомогательный метод; логирующие методы

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Byte.class
                || clazz == Character.class || clazz == Short.class || clazz == Integer.class
                || clazz == Long.class || clazz == Float.class || clazz == Double.class
                || clazz == Void.class || clazz == String.class;
    }
    
    private void logMethodCall(Method method, Object[] args) {
        String argsString = (args == null || args.length == 0) ? "" : Arrays.stream(args)
                .map(String::valueOf) 
                .collect(Collectors.joining(", "));
        System.out.println("Calling method: " + method.getName() + "(" + argsString + ")");
    }

    private void logMethodReturn(Method method, Object result) {
        if (!method.getReturnType().equals(void.class)) {
            System.out.println("Method " + method.getName() + " returned: " + result);
        } else {
            System.out.println("Method " + method.getName() + " completed");
        }
    }

    private void logMethodException(Method method, Throwable throwable) {
        System.out.println("Method " + method.getName() + " threw exception: " + throwable);
    }
}

