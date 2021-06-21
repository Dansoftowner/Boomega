package com.dansoftware.boomega.i18n;

import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.Locale;

/**
 * An {@link EnglishLanguagePack} is a {@link LanguagePack} that provides english translation
 *
 * @author Daniel Gyorffy
 */
public class EnglishLanguagePack extends InternalLanguagePack {

    public EnglishLanguagePack() {
        super(Locale.ENGLISH);
    }

    @Override
    protected boolean isRTL() {
        return false;
    }
}
