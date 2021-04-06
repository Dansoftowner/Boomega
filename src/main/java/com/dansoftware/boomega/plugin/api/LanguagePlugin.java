package com.dansoftware.boomega.plugin.api;

import com.dansoftware.boomega.i18n.LanguagePack;

/**
 * Allows to add a custom {@link LanguagePack} to the application.
 *
 * @author Daniel Gyoerffy
 */
public interface LanguagePlugin extends BoomegaPlugin {
    LanguagePack getLanguagePack();
}
