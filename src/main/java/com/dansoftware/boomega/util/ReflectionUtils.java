package com.dansoftware.boomega.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Utility class for making some reflection tasks easier.
 *
 * @author Daniel Gyorffy
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Returns the value of the static field-reference.
     *
     * <p>
     * If an exception occurs it throws a {@link RuntimeException} instead of
     * a checked exception.
     *
     * @param field the reflected {@link Field} object
     * @return the read object
     */
    public static Object getDeclaredStaticValue(Field field) {
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the reflected field accessible and removes the 'final' modifier from it.
     *
     * @param field the {@link Field} to operate on
     * @return the {@link Field} object itself
     */
    @NotNull
    public static Field modifiableField(@NotNull Field field) {
        setFieldModifiable(field);
        return field;
    }

    public static void setFieldModifiable(@NotNull Field field) {
        field.setAccessible(Boolean.TRUE);
        FieldUtils.removeFinalModifier(field);
    }

    /**
     * Creates an object through reflection: with a class-reference and the
     * constructor parameters.
     * <p>
     * Example:
     * <pre>{@code
     * class X {
     *     X(String arg0, int arg1) {
     *         ...
     *     }
     * }
     *
     * X instance = ReflectionUtils.constructObject(X.class, "First argument", 1024);
     *
     * }</pre>
     *
     * @param classRef the class-reference
     * @param args     the constructor arguments; empty if you want to call the default constructor
     * @param <O>      the type of the object that should constructed
     * @return the object instance
     * @throws ReflectiveOperationException if some reflection-exception occurs
     */
    public static <O> O constructObject(@NotNull Class<? extends O> classRef, Object... args)
            throws ReflectiveOperationException {
        Objects.requireNonNull(classRef);
        Class<?>[] constructorParamTypes = Stream.of(args)
                .map(Object::getClass)
                .toArray(Class[]::new);

        Constructor<? extends O> constructor = classRef.getDeclaredConstructor(constructorParamTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    public static <O> O constructObject(@NotNull Class<? extends O> classRef)
            throws ReflectiveOperationException {
        Objects.requireNonNull(classRef);
        Constructor<? extends O> constructor = classRef.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public static <O> O tryConstructObject(@NotNull Class<? extends O> classRef) {
        try {
            return constructObject(classRef);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public static <T> Class<T> forName(@NotNull Class<T> classRef)
            throws ClassNotFoundException {
        return (Class<T>) Class.forName(classRef.getName());
    }

    /**
     * Invokes the static block on the given {@code class},
     * if it's not executed yet
     *
     * @param classRef the class-reference
     */
    public static void initializeClass(@NotNull Class<?> classRef) {
        try {
            forName(classRef);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the static block on the given {@code class},
     * if it's not executed yet
     *
     * @param classRef the class-reference
     */
    public static void initializeClass(@NotNull Class<?> classRef, ClassLoader classLoader) {
        try {
            Class.forName(classRef.getName(), true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Collects all the subtypes of a class with given class reference.
     *
     * @param classRef the class reference that we want to get the subtypes from
     * @return the Set of class references
     */
    public static <T> Set<Class<? extends T>> getSubtypesOf(@NotNull Class<T> classRef) {
        return new Reflections(classRef).getSubTypesOf(classRef);
    }

    /**
     * Collects all subtypes of a class with the given class reference in
     * a given {@link ClassLoader}.
     *
     * @param classRef    the class reference that we want to get the subtypes from
     * @param classLoader the class loader
     * @return the Set of class references
     */
    public static <T> Set<Class<? extends T>> getSubtypesOf(@NotNull Class<T> classRef,
                                                            @NotNull ClassLoader classLoader) {
        Reflections reflections = new Reflections(classLoader, classRef, new SubTypesScanner());
        return reflections.getSubTypesOf(classRef);
    }
}
