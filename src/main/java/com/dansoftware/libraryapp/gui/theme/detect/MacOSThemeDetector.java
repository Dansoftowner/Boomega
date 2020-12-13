package com.dansoftware.libraryapp.gui.theme.detect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Determines the dark/light theme on Mac System.
 *
 * <b>DOESN'T WORKS YET</b>
 *
 * @author Daniel Gyorffy
 */
public class MacOSThemeDetector extends OsThemeDetector {

    private static final Logger logger = LoggerFactory.getLogger(MacOSThemeDetector.class);

    private static final String CMD = "defaults read -g AppleInterfaceStyle";

    private final Pattern themeNamePattern = Pattern.compile(".*dark.*", Pattern.CASE_INSENSITIVE);

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean isDark() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(CMD);
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String readLine = reader.readLine();
                if (readLine != null) {
                    return isDarkTheme(readLine);
                }
            }
        } catch (IOException e) {
            logger.error("Couldn't execute theme name query with the Os", e);
        }
        return false;
    }

    private boolean isDarkTheme(String themeName) {
        return themeNamePattern.matcher(themeName).matches();
    }

    @Override
    public void registerListener(@NotNull Consumer<Boolean> darkThemeListener) {
    }

    @Override
    public void removeListener(@Nullable Consumer<Boolean> darkThemeListener) {
    }
}
