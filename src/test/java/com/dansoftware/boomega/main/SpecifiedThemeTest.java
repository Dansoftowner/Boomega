package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.gui.theme.LightTheme;
import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecifiedThemeTest {

    private static final Logger logger = LoggerFactory.getLogger(SpecifiedThemeTest.class);

    public static void main(String... args) throws ReflectiveOperationException {
        Preferences preferences = Preferences.getPreferences();
        preferences.editor()
                .put(Preferences.Key.THEME, parseArgs(args))
                .tryCommit();

        logger.debug("Preferences theme: {}", preferences.get(Preferences.Key.THEME).getClass());
        Main.main(args);
    }

    private static Theme parseArgs(String[] args) throws ReflectiveOperationException {
        if (ArrayUtils.isEmpty(args)) {
            return new LightTheme();
        }
        return parseArg(args[0]);
    }

    private static Theme parseArg(String arg) throws ReflectiveOperationException {
        return (Theme) ReflectionUtils.constructObject(Class.forName(arg));
    }

}
