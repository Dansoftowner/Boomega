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

package com.dansoftware.boomega.i18n;

import com.dansoftware.boomega.i18n.api.LanguagePack;
import com.dansoftware.boomega.util.Person;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.dansoftware.boomega.i18n.DefaultResourceBundle.DEFAULT_RESOURCE_BUNDLE_NAME;

/**
 * An {@link EnglishLanguagePack} is a {@link LanguagePack} that provides english translation
 *
 * @author Daniel Gyorffy
 */
@Singleton // make sure it's constructed only once if used through DI framework
public class EnglishLanguagePack extends LanguagePack {

    public EnglishLanguagePack() {
        super(Locale.ENGLISH);
    }

    @Nullable
    @Override
    public Person getTranslator() {
        return new Person("Györffy", "Dániel", "dansoftwareowner@gmail.com");
    }

    @Override
    public @NotNull ResourceBundle getValues() {
        return ResourceBundle.getBundle(DEFAULT_RESOURCE_BUNDLE_NAME);
    }

    @Override
    protected boolean isRTL() {
        return false;
    }
}
