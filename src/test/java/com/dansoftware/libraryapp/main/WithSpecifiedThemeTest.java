package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.theme.LightTheme;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;

public class WithSpecifiedThemeTest {

    public static void main(String... args) throws ReflectiveOperationException {
        Preferences preferences = Preferences.getPreferences();
        preferences.editor()
                .put(Preferences.Key.THEME, parseArgs(args))
                .tryCommit();
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
