package info.kgeorgiy.ja.gusev.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Impler} interface that generates default implementations
 * of given interfaces or abstract classes.
 *
 * <p>This class generates a Java source file that implements all abstract methods
 * of the specified class or interface. The generated class is named as the original
 * class with {@code Impl} suffix added.</p>
 *
 * <p>Note: The generated implementation may contain unsafe operations (such as 
 * unchecked casts or raw types) and may override deprecated methods from the 
 * implemented interfaces or classes. You may encounter warnings like 
 * "uses unchecked or unsafe operations" or "uses or overrides a deprecated API" 
 * during compilation. These warnings can be checked in more detail by recompiling 
 * with the flag {@code -Xlint:unchecked} for unsafe operations and {@code -Xlint:deprecation} 
 * for deprecated API usage.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Path outputPath = Paths.get("output/directory");
 * Class<?> clazz = MyInterface.class;
 * new Implementor().implement(clazz, outputPath);
 * }</pre>
 *
 * <p>This class does not guarantee avoidance of deprecated APIs if they are present 
 * in the implemented interface or class. Additionally, it may use operations that 
 * are flagged as unchecked or unsafe due to type inference or other reasons.</p>
 *
 * @author
 *     Sergei Gusev M3237
 */
public class Implementor implements Impler {

    /**
     * Default constructor.
     */
    public Implementor() {
        // Empty constructor
    }

    /**
     * Generates default implementation of the specified class or interface.
     *
     * <p>Generates a Java source file containing the implementation of the specified
     * class or interface. The generated class will be placed in the specified
     * directory. If the class is not an interface and does not have at least one
     * accessible constructor, an {@link ImplerException} will be thrown.</p>
     *
     * @param clazz the class or interface to implement
     * @param path  the path where the generated implementation should be saved
     * @throws ImplerException if the implementation cannot be generated
     */
    @Override
    public void implement(Class<?> clazz, Path path) throws ImplerException {
        // Check if the class or interface can be implemented
        checkClassValidity(clazz);

        // Build the path for the generated class file
        Path filePath = constructFilePath(clazz, path, ".java");

        // Create parent directories for the file
        createParentDirectories(filePath);

        // Collect all abstract methods that need to be implemented
        Set<Method> abstractMethods = collectAbstractMethods(clazz);

        // Collect necessary imports for the class
        Set<String> imports = collectImports(clazz, abstractMethods);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Generate class header and imports
            writer.write(generateClassHeader(clazz, imports));

            // If it's a class (not an interface), generate constructors
            if (!clazz.isInterface()) {
                for (Constructor<?> constructor : generateConstructors(clazz)) {
                    writer.write(generateConstructor(constructor));
                }
            }

            // Generate all methods of the class
            for (Method method : abstractMethods) {
                writer.write(generateMethod(method));
            }

            // If it's an annotation, generate the annotationType() method
            if (clazz.isAnnotation()) {
                writer.write(generateAnnotationTypeMethod(clazz));
            }

            // Close the class
            writer.write("}" + System.lineSeparator());
        } catch (IOException e) {
            throw new ImplerException("Error writing to file", e);
        }
    }

    /**
     * Checks if the specified class or interface can be implemented.
     *
     * @param clazz the class or interface to check
     * @throws ImplerException if the class or interface cannot be implemented
     */
    private void checkClassValidity(Class<?> clazz) throws ImplerException {
        int modifiers = clazz.getModifiers();
        if (clazz.isArray() || clazz.isPrimitive() || clazz == Enum.class || Modifier.isFinal(modifiers)) {
            throw new ImplerException("Cannot implement the given class or interface");
        }
        if (Modifier.isPrivate(modifiers)) {
            throw new ImplerException("Cannot implement private class or interface");
        }
        if (clazz == Record.class) {
            throw new ImplerException("Cannot implement Record classes");
        }
    }

    /**
     * Constructs the path for the output file of the generated implementation.
     *
     * @param clazz         the class or interface being implemented
     * @param path          the base output path
     * @param fileExtension the file extension (e.g., ".java")
     * @return the path to the output file
     */
    private Path constructFilePath(Class<?> clazz, Path path, String fileExtension) {
        return path.resolve(clazz.getPackageName().replace('.', File.separatorChar))
                .resolve(clazz.getSimpleName() + "Impl" + fileExtension);
    }

    /**
     * Creates parent directories for the specified path.
     *
     * @param path the path for which to create parent directories
     * @throws ImplerException if the directories cannot be created
     */
    private void createParentDirectories(Path path) throws ImplerException {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new ImplerException("Unable to create directories for output file", e);
        }
    }

    /**
     * Generates accessible constructors for the specified class.
     *
     * @param clazz the class for which to generate constructors
     * @return an array of accessible constructors
     * @throws ImplerException if no accessible constructors are found
     */
    private Constructor<?>[] generateConstructors(Class<?> clazz) throws ImplerException {
        Constructor<?>[] constructors = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .toArray(Constructor<?>[]::new);

        if (!clazz.isInterface() && constructors.length == 0) {
            throw new ImplerException("No accessible constructors found");
        }

        return constructors;
    }

    /**
     * Collects all abstract methods from the specified class that need to be implemented.
     *
     * @param clazz the class from which to collect methods
     * @return a set of methods to implement
     */
    private Set<Method> collectAbstractMethods(Class<?> clazz) {
        Set<MethodSignature> methodsToImplement = new HashSet<>();
        Set<MethodSignature> implementedMethods = new HashSet<>();
        Map<MethodSignature, Method> methodMap = new HashMap<>();

        collectMethods(clazz, methodsToImplement, implementedMethods, methodMap);

        methodsToImplement.removeAll(implementedMethods);

        return methodsToImplement.stream()
                .map(methodMap::get)
                .collect(Collectors.toSet());
    }

    /**
     * Recursively collects methods from the specified class.
     *
     * @param clazz              the class from which to collect methods
     * @param methodsToImplement set of methods that need to be implemented
     * @param implementedMethods set of methods that are already implemented
     * @param methodMap          a map from method signatures to methods
     */
    private void collectMethods(Class<?> clazz, Set<MethodSignature> methodsToImplement,
                                Set<MethodSignature> implementedMethods, Map<MethodSignature, Method> methodMap) {
        if (clazz == null) {
            return;
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }
            MethodSignature signature = new MethodSignature(method);
            methodMap.putIfAbsent(signature, method);
            if (Modifier.isAbstract(method.getModifiers())) {
                methodsToImplement.add(signature);
            } else {
                implementedMethods.add(signature);
            }
        }

        collectMethods(clazz.getSuperclass(), methodsToImplement, implementedMethods, methodMap);
        for (Class<?> iface : clazz.getInterfaces()) {
            collectMethodsFromInterface(iface, methodsToImplement, implementedMethods, methodMap);
        }
    }

    /**
     * Collects methods from the specified interface.
     *
     * @param iface              the interface from which to collect methods
     * @param methodsToImplement set of methods that need to be implemented
     * @param implementedMethods set of methods that are already implemented
     * @param methodMap          a map from method signatures to methods
     */
    private void collectMethodsFromInterface(Class<?> iface, Set<MethodSignature> methodsToImplement,
                                             Set<MethodSignature> implementedMethods, Map<MethodSignature, Method> methodMap) {
        for (Method method : iface.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }
            MethodSignature signature = new MethodSignature(method);
            methodMap.putIfAbsent(signature, method);
            if (Modifier.isAbstract(method.getModifiers())) {
                methodsToImplement.add(signature);
            } else {
                implementedMethods.add(signature);
            }
        }

        for (Class<?> superIface : iface.getInterfaces()) {
            collectMethodsFromInterface(superIface, methodsToImplement, implementedMethods, methodMap);
        }
    }

    /**
     * Generates the header of the implementation class, including package declaration and imports.
     *
     * @param clazz   the class being implemented
     * @param imports set of imports required by the class
     * @return a string containing the class header
     */
    private String generateClassHeader(Class<?> clazz, Set<String> imports) {
        StringBuilder header = new StringBuilder();
        String packageName = clazz.getPackageName();
        if (!packageName.isEmpty()) {
            header.append("package ").append(packageName).append(";").append(System.lineSeparator()).append(System.lineSeparator());
        }

        for (String imp : imports) {
            if (!imp.equals(clazz.getCanonicalName())) {
                header.append("import ").append(imp).append(";").append(System.lineSeparator());
            }
        }
        if (!imports.isEmpty()) {
            header.append(System.lineSeparator());
        }

        String extOrImpl = clazz.isInterface() ? "implements" : "extends";
        header.append("public class ").append(clazz.getSimpleName()).append("Impl ")
                .append(extOrImpl).append(" ").append(clazz.getCanonicalName()).append(" {")
                .append(System.lineSeparator());
        return header.toString();
    }

    /**
     * Collects all necessary imports for the class.
     *
     * @param clazz   the class being implemented
     * @param methods the methods to be implemented
     * @return a set of import statements
     */
    private Set<String> collectImports(Class<?> clazz, Set<Method> methods) {
        Set<String> imports = new HashSet<>();

        for (Method method : methods) {
            collectType(imports, method.getReturnType());
            for (Parameter parameter : method.getParameters()) {
                collectType(imports, parameter.getType());
            }
            for (Class<?> exception : method.getExceptionTypes()) {
                collectType(imports, exception);
            }
        }

        return imports;
    }

    /**
     * Collects types for imports.
     *
     * @param imports set of imports to add to
     * @param type    the type to collect
     */
    private void collectType(Set<String> imports, Class<?> type) {
        if (type.isArray()) {
            collectType(imports, type.getComponentType());  // Import base type
        } else if (!type.isPrimitive() && !type.getPackageName().equals("java.lang") && !type.getPackageName().startsWith("java.")) {
            imports.add(type.getCanonicalName());
        }
    }

    /**
     * Generates code for a constructor.
     *
     * @param constructor the constructor to generate
     * @return a string containing the constructor code
     */
    private String generateConstructor(Constructor<?> constructor) {
        List<String> paramNames = new ArrayList<>();
        StringBuilder signature = new StringBuilder();
        signature.append("    ").append(Modifier.toString(constructor.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT & ~Modifier.NATIVE & ~Modifier.PROTECTED))
                .append(" ").append(constructor.getDeclaringClass().getSimpleName()).append("Impl")
                .append(generateConstructorParameters(constructor, paramNames))
                .append(generateConstructorExceptions(constructor))
                .append(" {").append(System.lineSeparator())
                .append("        super(").append(String.join(", ", paramNames)).append(");").append(System.lineSeparator())
                .append("    }").append(System.lineSeparator());
        return signature.toString();
    }

    /**
     * Generates code for a method.
     *
     * @param method the method to generate
     * @return a string containing the method code
     */
    private String generateMethod(Method method) {
        List<String> paramNames = new ArrayList<>();
        StringBuilder signature = new StringBuilder();
        signature.append("    @Override").append(System.lineSeparator())
                .append("    ").append(Modifier.toString(method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT & ~Modifier.NATIVE & Modifier.methodModifiers()))
                .append(" ").append(method.getReturnType().getCanonicalName())
                .append(" ").append(method.getName())
                .append(generateParameters(method, paramNames))
                .append(generateExceptions(method))
                .append(" {").append(System.lineSeparator())
                .append("        ").append(determineReturnValue(method.getReturnType())).append(";").append(System.lineSeparator())
                .append("    }").append(System.lineSeparator());
        return signature.toString();
    }

    /**
     * Generates the {@code annotationType()} method for annotation interfaces.
     *
     * @param clazz the annotation interface
     * @return a string containing the method code
     */
    private String generateAnnotationTypeMethod(Class<?> clazz) {
        if (!hasAnnotationTypeMethod(clazz)) {
            return "    @Override" + System.lineSeparator()
                    + "    public Class<? extends java.lang.annotation.Annotation> annotationType() {" + System.lineSeparator()
                    + "        return this.getClass();" + System.lineSeparator()
                    + "    }" + System.lineSeparator();
        }
        return "";
    }

    /**
     * Checks if the annotation interface has an {@code annotationType()} method.
     *
     * @param clazz the annotation interface
     * @return {@code true} if the method exists, {@code false} otherwise
     */
    private boolean hasAnnotationTypeMethod(Class<?> clazz) {
        try {
            clazz.getMethod("annotationType");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Generates parameter list for a method.
     *
     * @param method     the method
     * @param paramNames list to store parameter names
     * @return a string containing the parameter list
     */
    private String generateParameters(Method method, List<String> paramNames) {
        Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            String paramName = "arg" + i;
            builder.append(parameters[i].getType().getCanonicalName()).append(" ").append(paramName);
            paramNames.add(paramName);
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Generates parameter list for a constructor.
     *
     * @param constructor the constructor
     * @param paramNames  list to store parameter names
     * @return a string containing the parameter list
     */
    private String generateConstructorParameters(Constructor<?> constructor, List<String> paramNames) {
        Parameter[] parameters = constructor.getParameters();
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            String paramName = "arg" + i;
            builder.append(parameters[i].getType().getCanonicalName()).append(" ").append(paramName);
            paramNames.add(paramName);
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Generates exceptions list for a method.
     *
     * @param method the method
     * @return a string containing the exceptions list
     */
    private String generateExceptions(Method method) {
        Class<?>[] exceptions = method.getExceptionTypes();
        if (exceptions.length == 0) {
            return "";
        }
        return " throws " + Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Generates exceptions list for a constructor.
     *
     * @param constructor the constructor
     * @return a string containing the exceptions list
     */
    private String generateConstructorExceptions(Constructor<?> constructor) {
        Class<?>[] exceptions = constructor.getExceptionTypes();
        if (exceptions.length == 0) {
            return "";
        }
        return " throws " + Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Determines the default return value for the specified return type.
     *
     * @param returnType the return type
     * @return a string containing the return statement
     */
    private String determineReturnValue(Class<?> returnType) {
        if (returnType.equals(void.class)) {
            return "";
        } else if (returnType.equals(boolean.class)) {
            return "return false";
        } else if (returnType.isPrimitive()) {
            return "return 0";
        }
        return "return null";
    }

    /**
     * Helper class representing the signature of a method for comparison purposes.
     */
    private static class MethodSignature {
        /** The name of the method */
        private final String name;
        /** The parameter types of the method */
        private final Class<?>[] parameterTypes;

        /**
         * Constructs a new {@code MethodSignature} based on the specified method.
         *
         * @param method the method from which to create the signature
         */
        public MethodSignature(Method method) {
            this.name = method.getName();
            this.parameterTypes = method.getParameterTypes();
        }

        /**
         * Checks if this {@code MethodSignature} is equal to another object.
         *
         * @param o the object to compare
         * @return {@code true} if equal, {@code false} otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodSignature)) return false;
            MethodSignature that = (MethodSignature) o;
            return name.equals(that.name) && Arrays.equals(parameterTypes, that.parameterTypes);
        }

        /**
         * Computes the hash code for this {@code MethodSignature}.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + Arrays.hashCode(parameterTypes);
            return result;
        }
    }
}
