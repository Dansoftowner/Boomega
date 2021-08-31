package com.dansoftware.boomega.plugin.api;

import com.dansoftware.boomega.i18n.LanguagePack;
import org.jetbrains.annotations.NotNull;

/**
 * Allows to add a custom {@link LanguagePack} to the application.
 *
 * @author Daniel Gyoerffy
 */
public interface LanguagePlugin extends BoomegaPlugin {
    @NotNull
    LanguagePack getLanguagePack();
}
