package info.kgeorgiy.ja.gusev.jarimplementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Impler} and {@link JarImpler} interfaces that allows generating default implementations
 * of specified interfaces or abstract classes and packaging them into a JAR file.
 *
 * <p>This class generates a Java source file that implements all abstract methods
 * of the specified class or interface. The generated class is named as the original
 * class with {@code Impl} suffix added. It also provides functionality to compile
 * the generated source file and package it into a JAR file.</p>
 *
 * <p>Note: The generated implementation may use or override deprecated methods from 
 * the implemented interfaces or classes. You may encounter warnings like 
 * "uses or overrides a deprecated API" during compilation. These warnings can be 
 * examined in more detail by recompiling with the flag {@code -Xlint:deprecation}.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Path outputPath = Paths.get("output/directory");
 * Class<?> clazz = MyInterface.class;
 * new JarImplementor().implement(clazz, outputPath);
 *
 * Path jarFile = Paths.get("output/myInterfaceImpl.jar");
 * new JarImplementor().implementJar(clazz, jarFile);
 * }</pre>
 *
 * <p>This class does not guarantee avoidance of deprecated APIs if they are present 
 * in the implemented interface or class. The usage of deprecated methods is inherent 
 * to the underlying APIs being implemented.</p>
 *
 * @author
 *     Sergei Gusev M3237
 */
public class JarImplementor implements Impler, JarImpler {

    /**
     * Default constructor.
     */
    public JarImplementor() {
        // Empty constructor
    }


    /**
     * System-dependent line separator.
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();
    /**
     * Standard indentation (4 spaces) used in generated code.
     */
    private static final String INDENT = "    ";

    /**
     * Entry point of the program.
     *
     * <p>Usage:</p>
     * <ul>
     *     <li>{@code JarImplementor -jar <class name> <jar path>} - Generates implementation and packages it into a JAR file.</li>
     * </ul>
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args == null || args.length != 3 || args[0] == null || args[1] == null || args[2] == null) {
            System.err.println("Usage: JarImplementor -jar <class name> <jar path>");
            return;
        }

        JarImplementor implementor = new JarImplementor();

        try {
            if ("-jar".equals(args[0])) {
                Class<?> token = Class.forName(args[1]);
                Path jarFile = Paths.get(args[2]);
                implementor.implementJar(token, jarFile);
            } else {
                System.err.println("Invalid arguments");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("Implementation error: " + e.getMessage());
        }
    }

    /**
     * Converts the given string to Unicode-escaped representation.
     *
     * @param str the string to escape
     * @return the Unicode-escaped string
     */
    private static String escapeUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c >= 128) {
                sb.append(String.format("\\u%04X", (int) c));
            } else if (c == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the default value for the specified type.
     *
     * @param type the type for which to get the default value
     * @return the default value as a string
     */
    private static String getDefaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return "null";
        } else if (type.equals(boolean.class)) {
            return "false";
        } else if (type.equals(void.class)) {
            return "";
        } else if (type.equals(char.class)) {
            return "'\\0'";
        } else {
            return "0";
        }
    }

    /**
     * Generates default implementation of the specified class or interface.
     *
     * <p>Generates a Java source file containing the implementation of the specified
     * class or interface. The generated class will be placed in the specified
     * directory. If the class is not an interface and does not have at least one
     * accessible constructor, an {@link ImplerException} will be thrown.</p>
     *
     * @param token the class or interface to implement
     * @param root  the root directory where the generated implementation should be saved
     * @throws ImplerException if the implementation cannot be generated
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        // Validate input parameters
        if (token == null || root == null) {
            throw new ImplerException("Token or root is null");
        }
        validateToken(token);

        // Build the path for the generated class file
        Path filePath = getFilePath(token, root, ".java");

        // Create parent directories for the file
        createParentDirectories(filePath);

        // Generate the code and write to file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(generateCode(token));
        } catch (IOException e) {
            throw new ImplerException("Cannot write to file", e);
        }
    }

    /**
     * Generates default implementation of the specified class or interface and packages it into a JAR file.
     *
     * @param token   the class or interface to implement
     * @param jarFile the path to the output JAR file
     * @throws ImplerException if the implementation cannot be generated
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        // Validate input parameters
        if (token == null || jarFile == null) {
            throw new ImplerException("Token or jarFile is null");
        }
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("temp");
        } catch (IOException e) {
            throw new ImplerException("Cannot create temporary directory", e);
        }

        try {
            implement(token, tempDir);
            compile(token, tempDir);
            createJar(token, tempDir, jarFile);
        } finally {
            // Clean up temporary files
            try {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Validates the class or interface to be implemented.
     *
     * @param token the class or interface to validate
     * @throws ImplerException if the token is invalid for implementation
     */
    private void validateToken(Class<?> token) throws ImplerException {
        int modifiers = token.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            throw new ImplerException("Cannot extend final class");
        }
        if (token.isPrimitive() || token == Enum.class || token.isArray()) {
            throw new ImplerException("Invalid token");
        }
        if (Modifier.isPrivate(modifiers)) {
            throw new ImplerException("Cannot implement private class");
        }
        if (token == Record.class) {
            throw new ImplerException("Cannot implement Record classes");
        }
    }

    /**
     * Constructs the path for the output file of the generated implementation.
     *
     * @param token         the class or interface being implemented
     * @param root          the base output path
     * @param fileExtension the file extension (e.g., ".java")
     * @return the path to the output file
     */
    private Path getFilePath(Class<?> token, Path root, String fileExtension) {
        return root.resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(token.getSimpleName() + "Impl" + fileExtension);
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
            throw new ImplerException("Cannot create directories", e);
        }
    }

    /**
     * Generates the code for the implementation of the specified class or interface.
     *
     * @param token the class or interface to implement
     * @return the generated code as a string
     * @throws ImplerException if code generation fails
     */
    private String generateCode(Class<?> token) throws ImplerException {
        StringBuilder sb = new StringBuilder();

        String packageName = token.getPackageName();
        if (!packageName.isEmpty()) {
            sb.append("package ").append(escapeUnicode(packageName)).append(";").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
        }

        String className = escapeUnicode(token.getSimpleName()) + "Impl";
        String classDeclaration = "public class " + className + " ";

        if (token.isInterface()) {
            classDeclaration += "implements ";
        } else {
            classDeclaration += "extends ";
        }
        classDeclaration += escapeUnicode(token.getCanonicalName()) + " {" + LINE_SEPARATOR;

        sb.append(classDeclaration);

        if (!token.isInterface()) {
            sb.append(generateConstructors(token));
        }

        sb.append(generateMethods(token));

        sb.append("}").append(LINE_SEPARATOR);

        return sb.toString();
    }

    /**
     * Generates constructors for the implementation class.
     *
     * @param token the class being implemented
     * @return the generated constructors as a string
     * @throws ImplerException if no accessible constructors are found
     */
    private String generateConstructors(Class<?> token) throws ImplerException {
        Constructor<?>[] constructors = token.getDeclaredConstructors();
        List<Constructor<?>> publicConstructors = Arrays.stream(constructors)
                .filter(c -> !Modifier.isPrivate(c.getModifiers()))
                .collect(Collectors.toList());

        if (!token.isInterface() && publicConstructors.isEmpty()) {
            throw new ImplerException("No accessible constructors found");
        }

        StringBuilder sb = new StringBuilder();

        for (Constructor<?> constructor : publicConstructors) {
            sb.append(INDENT);
            int modifiers = constructor.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT & ~Modifier.NATIVE & ~Modifier.SYNCHRONIZED & ~Modifier.PROTECTED;
            sb.append(Modifier.toString(modifiers));
            if (modifiers != 0) {
                sb.append(" ");
            }
            sb.append(escapeUnicode(token.getSimpleName())).append("Impl(");

            Parameter[] parameters = constructor.getParameters();
            String params = Arrays.stream(parameters)
                    .map(p -> escapeUnicode(getType(p.getParameterizedType())) + " " + escapeUnicode(p.getName()))
                    .collect(Collectors.joining(", "));
            sb.append(params).append(")");

            Class<?>[] exceptions = constructor.getExceptionTypes();
            if (exceptions.length > 0) {
                sb.append(" throws ").append(Arrays.stream(exceptions)
                        .map(c -> escapeUnicode(c.getCanonicalName()))
                        .collect(Collectors.joining(", ")));
            }

            sb.append(" {").append(LINE_SEPARATOR);
            sb.append(INDENT).append(INDENT).append("super(");
            String args = Arrays.stream(parameters)
                    .map(p -> escapeUnicode(p.getName()))
                    .collect(Collectors.joining(", "));
            sb.append(args).append(");").append(LINE_SEPARATOR);

            sb.append(INDENT).append("}").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * Generates methods for the implementation class.
     *
     * @param token the class or interface being implemented
     * @return the generated methods as a string
     */
    private String generateMethods(Class<?> token) {
        Map<MethodSignature, Method> methods = new LinkedHashMap<>();
        Set<MethodSignature> implementedMethods = new HashSet<>();
        collectMethods(token, methods, implementedMethods);

        StringBuilder sb = new StringBuilder();
        for (Method method : methods.values()) {
            if (Modifier.isAbstract(method.getModifiers()) && !Modifier.isFinal(method.getModifiers())) {
                sb.append(generateMethod(method, token));
            }
        }
        return sb.toString();
    }

    /**
     * Collects methods that need to be implemented from the specified class or interface.
     *
     * @param token              the class or interface to collect methods from
     * @param abstractMethods    map to collect abstract methods into
     * @param implementedMethods set to collect implemented methods into
     */
    private void collectMethods(Class<?> token, Map<MethodSignature, Method> abstractMethods, Set<MethodSignature> implementedMethods) {
        if (token == null) {
            return;
        }

        for (Method method : token.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            MethodSignature signature = new MethodSignature(method);
            if (Modifier.isAbstract(modifiers)) {
                if (!Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers)) {
                    if (!implementedMethods.contains(signature)) {
                        abstractMethods.putIfAbsent(signature, method);
                    }
                }
            } else {
                implementedMethods.add(signature);
                abstractMethods.remove(signature);
            }
        }

        for (Class<?> iface : token.getInterfaces()) {
            collectMethods(iface, abstractMethods, implementedMethods);
        }

        collectMethods(token.getSuperclass(), abstractMethods, implementedMethods);
    }

    /**
     * Generates code for a method.
     *
     * @param method the method to generate
     * @param token  the class or interface being implemented
     * @return a string containing the method code
     */
    private String generateMethod(Method method, Class<?> token) {
        StringBuilder sb = new StringBuilder();
        sb.append(INDENT).append("@Override").append(LINE_SEPARATOR);

        int modifiers = method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT & ~Modifier.NATIVE & ~Modifier.SYNCHRONIZED & ~Modifier.STRICT & Modifier.methodModifiers();
        sb.append(INDENT).append(Modifier.toString(modifiers));
        if (modifiers != 0) {
            sb.append(" ");
        }

        TypeVariable<Method>[] typeParameters = method.getTypeParameters();
        if (typeParameters.length > 0) {
            sb.append("<");
            sb.append(Arrays.stream(typeParameters)
                    .map(this::getTypeVariable)
                    .map(JarImplementor::escapeUnicode)
                    .collect(Collectors.joining(", ")));
            sb.append("> ");
        }

        sb.append(escapeUnicode(getType(method.getGenericReturnType()))).append(" ");
        sb.append(escapeUnicode(method.getName())).append("(");

        Parameter[] parameters = method.getParameters();
        String params = Arrays.stream(parameters)
                .map(p -> escapeUnicode(getType(p.getParameterizedType())) + " " + escapeUnicode(p.getName()))
                .collect(Collectors.joining(", "));
        sb.append(params).append(")");

        Set<String> allowedExceptions = getAllowedExceptions(method, token);
        if (!allowedExceptions.isEmpty()) {
            sb.append(" throws ").append(allowedExceptions.stream()
                    .map(JarImplementor::escapeUnicode)
                    .collect(Collectors.joining(", ")));
        }

        sb.append(" {").append(LINE_SEPARATOR);
        sb.append(INDENT).append(INDENT);
        if (method.getReturnType() != void.class) {
            sb.append("return ").append(getDefaultValue(method.getReturnType())).append(";");
        }
        sb.append(LINE_SEPARATOR).append(INDENT).append("}").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Determines the allowed exceptions for a method based on overridden methods.
     *
     * @param method the method to analyze
     * @param token  the class or interface being implemented
     * @return a set of allowed exception types
     */
    private Set<String> getAllowedExceptions(Method method, Class<?> token) {
        Set<String> allowedExceptions = new HashSet<>();
        Set<Class<?>> exceptionTypes = new HashSet<>();

        Method overriddenMethod = findOverriddenMethod(token.getSuperclass(), method);
        if (overriddenMethod != null) {
            exceptionTypes.addAll(Arrays.asList(overriddenMethod.getExceptionTypes()));
        }

        for (Class<?> iface : token.getInterfaces()) {
            Method interfaceMethod = findOverriddenMethod(iface, method);
            if (interfaceMethod != null) {
                exceptionTypes.addAll(Arrays.asList(interfaceMethod.getExceptionTypes()));
            }
        }

        for (Class<?> exception : method.getExceptionTypes()) {
            if (exceptionTypes.contains(exception)) {
                allowedExceptions.add(getType(exception));
            }
        }

        return allowedExceptions;
    }

    /**
     * Finds the overridden method in the superclass or interfaces.
     *
     * @param token  the class to search in
     * @param method the method to find
     * @return the overridden method if found, or {@code null}
     */
    private Method findOverriddenMethod(Class<?> token, Method method) {
        if (token == null) {
            return null;
        }
        try {
            return token.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            Method m = findOverriddenMethod(token.getSuperclass(), method);
            if (m != null) {
                return m;
            }
            for (Class<?> iface : token.getInterfaces()) {
                m = findOverriddenMethod(iface, method);
                if (m != null) {
                    return m;
                }
            }
            return null;
        }
    }

    /**
     * Returns the string representation of a type.
     *
     * @param type the type to represent
     * @return the string representation of the type
     */
    private String getType(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).getCanonicalName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            StringBuilder sb = new StringBuilder();
            sb.append(getType(pt.getRawType()));
            Type[] args = pt.getActualTypeArguments();
            if (args.length > 0) {
                sb.append("<").append(Arrays.stream(args).map(this::getType).collect(Collectors.joining(", "))).append(">");
            }
            return sb.toString();
        } else if (type instanceof TypeVariable) {
            return ((TypeVariable<?>) type).getName();
        } else if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            StringBuilder sb = new StringBuilder("?");
            Type[] upperBounds = wt.getUpperBounds();
            if (upperBounds.length > 0 && upperBounds[0] != Object.class) {
                sb.append(" extends ").append(getType(upperBounds[0]));
            }
            Type[] lowerBounds = wt.getLowerBounds();
            if (lowerBounds.length > 0) {
                sb.append(" super ").append(getType(lowerBounds[0]));
            }
            return sb.toString();
        } else if (type instanceof GenericArrayType) {
            return getType(((GenericArrayType) type).getGenericComponentType()) + "[]";
        } else {
            return type.toString();
        }
    }

    /**
     * Returns the string representation of a type variable.
     *
     * @param typeVariable the type variable to represent
     * @return the string representation of the type variable
     */
    private String getTypeVariable(TypeVariable<?> typeVariable) {
        StringBuilder sb = new StringBuilder();
        sb.append(typeVariable.getName());
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length > 0 && !(bounds.length == 1 && bounds[0].equals(Object.class))) {
            sb.append(" extends ");
            sb.append(Arrays.stream(bounds)
                    .map(this::getType)
                    .collect(Collectors.joining(" & ")));
        }
        return sb.toString();
    }

    /**
     * Compiles the generated source code.
     *
     * @param token   the class or interface being implemented
     * @param tempDir the temporary directory containing the source code
     * @throws ImplerException if compilation fails
     */
    private void compile(Class<?> token, Path tempDir) throws ImplerException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("No Java compiler found");
        }

        String classPath = tempDir.toString() + File.pathSeparator + System.getProperty("java.class.path");

        Path filePath = tempDir.resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(token.getSimpleName() + "Impl.java");

        List<String> args = new ArrayList<>();
        args.add(filePath.toString());
        args.add("-cp");
        args.add(classPath);
        args.add("-encoding");
        args.add("UTF-8");

        int exitCode = compiler.run(null, null, null, args.toArray(new String[0]));

        if (exitCode != 0) {
            throw new ImplerException("Compilation failed");
        }
    }

    /**
     * Creates a JAR file containing the compiled implementation.
     *
     * @param token   the class or interface being implemented
     * @param tempDir the temporary directory containing the compiled class
     * @param jarFile the path to the output JAR file
     * @throws ImplerException if an error occurs during JAR creation
     */
    private void createJar(Class<?> token, Path tempDir, Path jarFile) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        Path classFile = tempDir.resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(token.getSimpleName() + "Impl.class");

        String entryName = token.getPackageName().replace('.', '/') + "/" + token.getSimpleName() + "Impl.class";

        try (JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            jarOut.putNextEntry(new JarEntry(entryName));
            Files.copy(classFile, jarOut);
            jarOut.closeEntry();
        } catch (IOException e) {
            throw new ImplerException("Cannot create JAR file", e);
        }
    }

    /**
     * Helper class representing the signature of a method for comparison purposes.
     */
    private static class MethodSignature {
        /**
         * The name of the method.
         */
        private final String name;
        /**
         * The list of parameter types of the method.
         */
        private final List<Class<?>> parameterTypes;
        /**
         * The list of type parameter names of the method.
         */
        private final List<String> typeParameters;

        /**
         * Constructs a new {@code MethodSignature} based on the specified method.
         *
         * @param method the method from which to create the signature
         */
        public MethodSignature(Method method) {
            this.name = method.getName();
            this.parameterTypes = Arrays.asList(method.getParameterTypes());
            this.typeParameters = Arrays.stream(method.getTypeParameters())
                    .map(TypeVariable::getName)
                    .collect(Collectors.toList());
        }

        /**
         * Checks if this {@code MethodSignature} is equal to another object.
         *
         * @param obj the object to compare
         * @return {@code true} if equal, {@code false} otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MethodSignature)) return false;
            MethodSignature other = (MethodSignature) obj;
            return name.equals(other.name)
                    && parameterTypes.equals(other.parameterTypes)
                    && typeParameters.equals(other.typeParameters);
        }

        /**
         * Computes the hash code for this {@code MethodSignature}.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return Objects.hash(name, parameterTypes, typeParameters);
        }
    }
}

