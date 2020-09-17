package com.dansoftware.libraryapp.util;

import java.lang.ref.WeakReference;

/**
 * An {@link IdentifiableWeakReference} is a {@link WeakReference} that provides
 * equals() and hashCode() methods that uses the referent objects equals() and hashCode() methods.
 *
 * @param <T>
 */
public class IdentifiableWeakReference<T> extends WeakReference<T> {
    public IdentifiableWeakReference(T referent) {
        super(referent);
    }

    @Override
    public int hashCode() {
        T referent = get();
        return referent == null ? super.hashCode() : referent.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof WeakReference))
            return false;

        WeakReference<?> weakReferenceObj = (WeakReference<?>) obj;
        T referent = get();

        return referent == null ? super.equals(obj) : referent.equals(weakReferenceObj.get());
    }
}
