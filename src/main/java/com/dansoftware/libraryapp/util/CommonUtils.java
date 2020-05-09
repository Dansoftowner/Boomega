package com.dansoftware.libraryapp.util;

public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * With this method we can decide that an array is empty or not.
     *
     * @param array the array to be checked; can be null
     * @return <code>true</code> - if the array is empty (or null);
     *         <code>false</code> - otherwise
     */
    public static<T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
}
