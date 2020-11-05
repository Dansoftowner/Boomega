package com.dansoftware.libraryapp.util

import java.lang.ref.WeakReference

/**
 * An [IdentifiableWeakReference] is a [WeakReference] that provides
 * equals() and hashCode() methods that uses the referent objects equals() and hashCode() methods.
 *
 * @param <T>
 */
class IdentifiableWeakReference<T>(referent: T) : WeakReference<T>(referent) {
    override fun hashCode(): Int = get()?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is WeakReference<*> -> false
        else -> if (get() == null) super.equals(other) else get() == other.get()
    }
}