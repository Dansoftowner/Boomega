package com.dansoftware.libraryapp.locale;

import java.util.Locale;

/**
 * An {@link EnglishLanguagePack} is a {@link LanguagePack} that provides english translation
 *
 * @author Daniel Gyorffy
 */
public class EnglishLanguagePack extends InternalLanguagePack {

    static {
        registerLanguagePack(Locale.ENGLISH, EnglishLanguagePack.class);
    }

    public EnglishLanguagePack() {
        super(Locale.ENGLISH);
    }

    @Override
    protected boolean isRTL() {
        return false;
    }
}
