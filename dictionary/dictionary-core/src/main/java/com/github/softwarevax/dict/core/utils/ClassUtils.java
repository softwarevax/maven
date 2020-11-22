package com.github.softwarevax.dict.core.utils;

import java.beans.Introspector;
import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author ctw
 * 2020/11/22 12:39
 */
public class ClassUtils {

    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';
    private static final char INNER_CLASS_SEPARATOR = '$';
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    public static final String CLASS_FILE_SUFFIX = ".class";
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap(8);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap(64);
    private static final Set<Class<?>> javaLanguageInterfaces;

    public ClassUtils() {
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        Class[] var1 = commonClasses;
        int var2 = commonClasses.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Class<?> clazz = var1[var3];
            commonClassCache.put(clazz.getName(), clazz);
        }

    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var3) {
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable var2) {
                }
            }
        }

        return cl;
    }

    public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
            currentThread.setContextClassLoader(classLoaderToUse);
            return threadContextClassLoader;
        } else {
            return null;
        }
    }

    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = (Class)commonClassCache.get(name);
        }

        if (clazz != null) {
            return clazz;
        } else {
            Class elementClass;
            String elementName;
            if (name.endsWith("[]")) {
                elementName = name.substring(0, name.length() - "[]".length());
                elementClass = forName(elementName, classLoader);
                return Array.newInstance(elementClass, 0).getClass();
            } else if (name.startsWith("[L") && name.endsWith(";")) {
                elementName = name.substring("[L".length(), name.length() - 1);
                elementClass = forName(elementName, classLoader);
                return Array.newInstance(elementClass, 0).getClass();
            } else if (name.startsWith("[")) {
                elementName = name.substring("[".length());
                elementClass = forName(elementName, classLoader);
                return Array.newInstance(elementClass, 0).getClass();
            } else {
                ClassLoader clToUse = classLoader;
                if (classLoader == null) {
                    clToUse = getDefaultClassLoader();
                }

                try {
                    return Class.forName(name, false, clToUse);
                } catch (ClassNotFoundException var9) {
                    int lastDotIndex = name.lastIndexOf(46);
                    if (lastDotIndex != -1) {
                        String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);

                        try {
                            return Class.forName(innerClassName, false, clToUse);
                        } catch (ClassNotFoundException var8) {
                        }
                    }

                    throw var9;
                }
            }
        }
    }

    public static Class<?> resolveClassName(String className, ClassLoader classLoader) throws IllegalArgumentException {
        try {
            return forName(className, classLoader);
        } catch (IllegalAccessError var3) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
        } catch (LinkageError var4) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", var4);
        } catch (ClassNotFoundException var5) {
            throw new IllegalArgumentException("Could not find class [" + className + "]", var5);
        }
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (IllegalAccessError var3) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
        } catch (Throwable var4) {
            return false;
        }
    }

    public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        } else {
            try {
                if (clazz.getClassLoader() == classLoader) {
                    return true;
                }
            } catch (SecurityException var3) {
            }

            return isLoadable(clazz, classLoader);
        }
    }

    public static boolean isCacheSafe(Class<?> clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");

        try {
            ClassLoader target = clazz.getClassLoader();
            if (target == classLoader || target == null) {
                return true;
            }

            if (classLoader == null) {
                return false;
            }

            ClassLoader current = classLoader;

            while(current != null) {
                current = current.getParent();
                if (current == target) {
                    return true;
                }
            }

            while(target != null) {
                target = target.getParent();
                if (target == classLoader) {
                    return false;
                }
            }
        } catch (SecurityException var4) {
        }

        return classLoader != null && isLoadable(clazz, classLoader);
    }

    private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
        try {
            return clazz == classLoader.loadClass(clazz.getName());
        } catch (ClassNotFoundException var3) {
            return false;
        }
    }


    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = (Class)primitiveTypeNameMap.get(name);
        }

        return result;
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    public static boolean isPrimitiveArray(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && clazz.getComponentType().isPrimitive();
    }

    public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType());
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() && clazz != Void.TYPE ? (Class)primitiveTypeToWrapperMap.get(clazz) : clazz;
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        } else {
            Class resolvedPrimitive;
            if (lhsType.isPrimitive()) {
                resolvedPrimitive = (Class)primitiveWrapperTypeMap.get(rhsType);
                if (lhsType == resolvedPrimitive) {
                    return true;
                }
            } else {
                resolvedPrimitive = (Class)primitiveTypeToWrapperMap.get(rhsType);
                if (resolvedPrimitive != null && lhsType.isAssignableFrom(resolvedPrimitive)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isAssignableValue(Class<?> type, Object value) {
        Assert.notNull(type, "Type must not be null");
        return value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive();
    }

    public static String convertResourcePathToClassName(String resourcePath) {
        Assert.notNull(resourcePath, "Resource path must not be null");
        return resourcePath.replace('/', '.');
    }

    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace('.', '/');
    }

    public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
        Assert.notNull(resourceName, "Resource name must not be null");
        return !resourceName.startsWith("/") ? classPackageAsResourcePath(clazz) + '/' + resourceName : classPackageAsResourcePath(clazz) + resourceName;
    }

    public static String classPackageAsResourcePath(Class<?> clazz) {
        if (clazz == null) {
            return "";
        } else {
            String className = clazz.getName();
            int packageEndIndex = className.lastIndexOf(46);
            if (packageEndIndex == -1) {
                return "";
            } else {
                String packageName = className.substring(0, packageEndIndex);
                return packageName.replace('.', '/');
            }
        }
    }

    public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
        return (Class[])collection.toArray(new Class[0]);
    }

    public static Class<?>[] getAllInterfaces(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClass(instance.getClass());
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
        return getAllInterfacesForClass(clazz, (ClassLoader)null);
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
        return toClassArray(getAllInterfacesForClassAsSet(clazz, classLoader));
    }

    public static Set<Class<?>> getAllInterfacesAsSet(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClassAsSet(instance.getClass());
    }

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
        return getAllInterfacesForClassAsSet(clazz, (ClassLoader)null);
    }

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            return Collections.singleton(clazz);
        } else {
            Set<Class<?>> interfaces = new LinkedHashSet();

            for(Class current = clazz; current != null; current = current.getSuperclass()) {
                Class<?>[] ifcs = current.getInterfaces();
                Class[] var5 = ifcs;
                int var6 = ifcs.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    Class<?> ifc = var5[var7];
                    if (isVisible(ifc, classLoader)) {
                        interfaces.add(ifc);
                    }
                }
            }

            return interfaces;
        }
    }

    public static Class<?> createCompositeInterface(Class<?>[] interfaces, ClassLoader classLoader) {
        Assert.notEmpty(interfaces, "Interface array must not be empty");
        return Proxy.getProxyClass(classLoader, interfaces);
    }


    public static Class<?> determineCommonAncestor(Class<?> clazz1, Class<?> clazz2) {
        if (clazz1 == null) {
            return clazz2;
        } else if (clazz2 == null) {
            return clazz1;
        } else if (clazz1.isAssignableFrom(clazz2)) {
            return clazz1;
        } else if (clazz2.isAssignableFrom(clazz1)) {
            return clazz2;
        } else {
            Class ancestor = clazz1;

            do {
                ancestor = ancestor.getSuperclass();
                if (ancestor == null || Object.class == ancestor) {
                    return null;
                }
            } while(!ancestor.isAssignableFrom(clazz2));

            return ancestor;
        }
    }

    public static boolean isJavaLanguageInterface(Class<?> ifc) {
        return javaLanguageInterfaces.contains(ifc);
    }

    public static boolean isInnerClass(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    public static boolean isCglibProxy(Object object) {
        return isCglibProxyClass(object.getClass());
    }

    public static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && isCglibProxyClassName(clazz.getName());
    }

    public static boolean isCglibProxyClassName(String className) {
        return className != null && className.contains("$$");
    }

    public static Class<?> getUserClass(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getUserClass(instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains("$$")) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }

        return clazz;
    }


    public static String getDescriptiveType(Object value) {
        if (value == null) {
            return null;
        } else {
            Class<?> clazz = value.getClass();
            if (Proxy.isProxyClass(clazz)) {
                StringBuilder result = new StringBuilder(clazz.getName());
                result.append(" implementing ");
                Class<?>[] ifcs = clazz.getInterfaces();

                for(int i = 0; i < ifcs.length; ++i) {
                    result.append(ifcs[i].getName());
                    if (i < ifcs.length - 1) {
                        result.append(',');
                    }
                }

                return result.toString();
            } else {
                return clazz.getTypeName();
            }
        }
    }

    public static boolean matchesTypeName(Class<?> clazz, String typeName) {
        return typeName != null && (typeName.equals(clazz.getTypeName()) || typeName.equals(clazz.getSimpleName()));
    }

    public static String getShortName(String className) {
        Assert.hasLength(className, "Class name must not be empty");
        int lastDotIndex = className.lastIndexOf(46);
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }

        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        return shortName;
    }

    public static String getShortName(Class<?> clazz) {
        return getShortName(getQualifiedName(clazz));
    }

    public static String getShortNameAsProperty(Class<?> clazz) {
        String shortName = getShortName(clazz);
        int dotIndex = shortName.lastIndexOf(46);
        shortName = dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName;
        return Introspector.decapitalize(shortName);
    }

    public static String getClassFileName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(46);
        return className.substring(lastDotIndex + 1) + ".class";
    }

    public static String getPackageName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return getPackageName(clazz.getName());
    }

    public static String getPackageName(String fqClassName) {
        Assert.notNull(fqClassName, "Class name must not be null");
        int lastDotIndex = fqClassName.lastIndexOf(46);
        return lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "";
    }

    public static String getQualifiedName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.getTypeName();
    }

    public static String getQualifiedMethodName(Method method) {
        return getQualifiedMethodName(method, (Class)null);
    }

    public static String getQualifiedMethodName(Method method, Class<?> clazz) {
        Assert.notNull(method, "Method must not be null");
        return (clazz != null ? clazz : method.getDeclaringClass()).getName() + '.' + method.getName();
    }

    public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
        return getConstructorIfAvailable(clazz, paramTypes) != null;
    }


    public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");

        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException var3) {
            return null;
        }
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return getMethodIfAvailable(clazz, methodName, paramTypes) != null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException var9) {
                throw new IllegalStateException("Expected method not found: " + var9);
            }
        } else {
            Set<Method> candidates = new HashSet(1);
            Method[] methods = clazz.getMethods();
            Method[] var5 = methods;
            int var6 = methods.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Method method = var5[var7];
                if (methodName.equals(method.getName())) {
                    candidates.add(method);
                }
            }

            if (candidates.size() == 1) {
                return (Method)candidates.iterator().next();
            } else if (candidates.isEmpty()) {
                throw new IllegalStateException("Expected method not found: " + clazz.getName() + '.' + methodName);
            } else {
                throw new IllegalStateException("No unique method found: " + clazz.getName() + '.' + methodName);
            }
        }
    }


    public static Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException var9) {
                return null;
            }
        } else {
            Set<Method> candidates = new HashSet(1);
            Method[] methods = clazz.getMethods();
            Method[] var5 = methods;
            int var6 = methods.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Method method = var5[var7];
                if (methodName.equals(method.getName())) {
                    candidates.add(method);
                }
            }

            if (candidates.size() == 1) {
                return (Method)candidates.iterator().next();
            } else {
                return null;
            }
        }
    }

    public static int getMethodCountForName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        int count = 0;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method[] var4 = declaredMethods;
        int var5 = declaredMethods.length;

        int var6;
        for(var6 = 0; var6 < var5; ++var6) {
            Method method = var4[var6];
            if (methodName.equals(method.getName())) {
                ++count;
            }
        }

        Class<?>[] ifcs = clazz.getInterfaces();
        Class[] var10 = ifcs;
        var6 = ifcs.length;

        for(int var11 = 0; var11 < var6; ++var11) {
            Class<?> ifc = var10[var11];
            count += getMethodCountForName(ifc, methodName);
        }

        if (clazz.getSuperclass() != null) {
            count += getMethodCountForName(clazz.getSuperclass(), methodName);
        }

        return count;
    }

    public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method[] var3 = declaredMethods;
        int var4 = declaredMethods.length;

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            if (method.getName().equals(methodName)) {
                return true;
            }
        }

        Class<?>[] ifcs = clazz.getInterfaces();
        Class[] var9 = ifcs;
        var5 = ifcs.length;

        for(int var10 = 0; var10 < var5; ++var10) {
            Class<?> ifc = var9[var10];
            if (hasAtLeastOneMethodWithName(ifc, methodName)) {
                return true;
            }
        }

        return clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName);
    }

    public static Method getInterfaceMethodIfPossible(Method method) {
        if (Modifier.isPublic(method.getModifiers()) && !method.getDeclaringClass().isInterface()) {
            for(Class current = method.getDeclaringClass(); current != null && current != Object.class; current = current.getSuperclass()) {
                Class<?>[] ifcs = current.getInterfaces();
                Class[] var3 = ifcs;
                int var4 = ifcs.length;
                int var5 = 0;

                while(var5 < var4) {
                    Class ifc = var3[var5];

                    try {
                        return ifc.getMethod(method.getName(), method.getParameterTypes());
                    } catch (NoSuchMethodException var8) {
                        ++var5;
                    }
                }
            }
        }

        return method;
    }

    public static boolean isUserLevelMethod(Method method) {
        Assert.notNull(method, "Method must not be null");
        return method.isBridge() || !method.isSynthetic() && !isGroovyObjectMethod(method);
    }

    private static boolean isGroovyObjectMethod(Method method) {
        return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
    }

    private static boolean isOverridable(Method method, Class<?> targetClass) {
        if (Modifier.isPrivate(method.getModifiers())) {
            return false;
        } else if (!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers())) {
            return targetClass == null || getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass));
        } else {
            return true;
        }
    }


    public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");

        try {
            Method method = clazz.getMethod(methodName, args);
            return Modifier.isStatic(method.getModifiers()) ? method : null;
        } catch (NoSuchMethodException var4) {
            return null;
        }
    }

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        Iterator var0 = primitiveWrapperTypeMap.entrySet().iterator();

        while(var0.hasNext()) {
            Map.Entry<Class<?>, Class<?>> entry = (Map.Entry)var0.next();
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses((Class)entry.getKey());
        }

        Set<Class> primitiveTypes = new HashSet(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        Collections.addAll(primitiveTypes, new Class[]{boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class});
        primitiveTypes.add(Void.TYPE);
        Iterator var4 = primitiveTypes.iterator();

        while(var4.hasNext()) {
            Class<?> primitiveType = (Class)var4.next();
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class, Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);
        Class<?>[] javaLanguageInterfaceArray = new Class[]{Serializable.class, Externalizable.class, Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};
        registerCommonClasses(javaLanguageInterfaceArray);
        javaLanguageInterfaces = new HashSet(Arrays.asList(javaLanguageInterfaceArray));
    }
}
