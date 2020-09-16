package com.dansoftware.libraryapp.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

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
}
