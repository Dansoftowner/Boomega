package com.dansoftware.boomega.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentifiableWeakReferenceTest {

    @Test
    public void testSameReference() {
        Object object = new Object();

        var identifiableWeakReference = new IdentifiableWeakReference<>(object);
        var identifiableWeakReference2 = new IdentifiableWeakReference<>(object);
        Assertions.assertEquals(identifiableWeakReference, identifiableWeakReference2);
    }

    @Test
    public void testDifferentReference() {
        var identifiableWeakReference = new IdentifiableWeakReference<>(new Object());
        var identifiableWeakReference2 = new IdentifiableWeakReference<>(new Object());
        Assertions.assertNotEquals(identifiableWeakReference, identifiableWeakReference2);
    }
}
