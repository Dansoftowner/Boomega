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
