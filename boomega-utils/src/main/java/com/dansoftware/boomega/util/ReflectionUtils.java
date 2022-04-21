/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Utilities for making some reflection tasks easier.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        // Don't instantiate
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
     * @param <O>      the type of the object that should be constructed
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

    /**
     * Constructs the instance of the given {@link Class} with the default constructor.
     *
     * @param classRef the object representing the class we want to instantiate
     * @return the instance object
     * @throws ReflectiveOperationException if the reflecive operation fails (e.g. No default constructor available)
     */
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

    /**
     * Returns the {@link Class} object associated with the class or interface with the given string name
     * without throwing any checked exceptions.
     *
     * @see Class#forName(String)
     */
    public static Class<?> forName(@NotNull String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
