package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.exception.UncaughtExceptionHandler;
import com.dansoftware.boomega.gui.app.BaseApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;

public class FirstTimeSimulation {

    static {
        PropertiesSetup.setupSystemProperties();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        Preferences.setDefault(Preferences.empty());
    }

    public static void main(String... args) {
        BaseApplication.launchApp(BoomegaApp.class, args);
    }
}
