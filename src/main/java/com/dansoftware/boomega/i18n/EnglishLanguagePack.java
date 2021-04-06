package com.dansoftware.boomega.i18n;

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
    protected Collator getABCCollator() {
        return Collator.getInstance(getLocale());
    }

    @Override
    protected boolean isRTL() {
        return false;
    }
}
