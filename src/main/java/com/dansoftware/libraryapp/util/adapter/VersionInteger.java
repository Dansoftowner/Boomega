package com.dansoftware.libraryapp.util.adapter;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link VersionInteger} can convert a version-string (x.x.x) to
 * an {@link Integer} value.
 *
 * <pre>
 *     new VersionInteger("0.1.3").getValue()  =  13
 *     new VersionInteger("1.0.6").getValue()  =  106
 *     new VersionInteger("4.5.0").getValue()  =  450
 * </pre>
 */
public class VersionInteger implements Comparable<VersionInteger> {

    private final static String DOT = ".";

    private final int value;

    public VersionInteger(String versionString) {
        this.value = convertVersionString(versionString);
    }

    private int convertVersionString(String versionString) {
        return Integer.parseInt(versionString.replace(DOT, StringUtils.EMPTY));
    }

    public boolean isNewerThan(VersionInteger other) {
        return this.compareTo(other) > 0;
    }

    public boolean isOlderThan(VersionInteger other) {
        return this.compareTo(other) < 0;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull VersionInteger o) {
        return this.value - o.value;
    }
}
