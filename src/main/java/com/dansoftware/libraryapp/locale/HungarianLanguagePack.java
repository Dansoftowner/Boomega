package com.dansoftware.libraryapp.locale;

import java.util.Locale;

/**
 * A {@link HungarianLanguagePack} is a {@link LanguagePack} that provides translation for the Hungarian language.
 *
 * @author Daniel Gyorffy
 */
public class HungarianLanguagePack extends InternalLanguagePack {

    private static final Locale LOCALE = new Locale("hu", "HU");

    static {
        registerLanguagePack(LOCALE, HungarianLanguagePack.class);
    }

    public HungarianLanguagePack() {
        super(LOCALE);
    }

    @Override
    protected boolean isRTL() {
        return false;
    }
}
