package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.exception.UncaughtExceptionHandler;
import com.dansoftware.boomega.gui.app.BaseBoomegaApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;
import org.jetbrains.annotations.NotNull;

public class FirstTimeSimulation {

    static {
        PropertiesSetup.setupSystemProperties();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    public static void main(String... args) {
        BaseBoomegaApplication.launchApp(ShadowBoomegaApp.class, args);
    }

    public static class ShadowBoomegaApp extends BoomegaApp {
        @NotNull
        @Override
        protected Preferences buildPreferences() {
            return Preferences.empty();
        }
    }
}
