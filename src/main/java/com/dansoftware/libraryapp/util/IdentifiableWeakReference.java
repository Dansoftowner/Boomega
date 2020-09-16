package com.dansoftware.libraryapp.util;

import java.lang.ref.WeakReference;

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

        T referent = get();
        return referent == null ? super.equals(obj) : referent.equals(((WeakReference<?>) obj).get());
    }
}
