package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.i18n.I18N;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A LightTheme is a {@link Theme} implementation that applies a light theme.
 *
 * @author Daniel Gyorffy
 */
public class LightTheme extends JMetroTheme {

    public LightTheme() {
        super(Style.LIGHT);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected @NotNull List<String> additionalStyleSheets() {
        return Collections.singletonList(getClass().getResource("light.css").toExternalForm());
    }

    public static ThemeMeta<LightTheme> getMeta() {
        return new ThemeMeta<>(LightTheme.class, () -> I18N.getValue("app.ui.theme.light"), InternalThemeDesigner.INSTANCE);
    }
}
