package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.i18n.I18N;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A DarkTheme is a {@link Theme} implementation that applies a dark-looking theme.
 *
 * @author Daniel Gyorffy
 */
public class DarkTheme extends JMetroTheme {

    public DarkTheme() {
        super(Style.DARK);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected @NotNull List<String> additionalStyleSheets() {
        return Collections.singletonList(getClass().getResource("dark.css").toExternalForm());
    }

    public static ThemeMeta<DarkTheme> getMeta() {
        return new ThemeMeta<>(DarkTheme.class, () -> I18N.getValue("theme.dark"), InternalThemeDesigner.INSTANCE);
    }
}
