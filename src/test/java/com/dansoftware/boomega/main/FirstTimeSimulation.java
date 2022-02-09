package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.exception.UncaughtExceptionHandler;
import com.dansoftware.boomega.gui.app.BaseBoomegaApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;
import com.google.inject.AbstractModule;

public class FirstTimeSimulation {

    static {
        PropertiesSetup.setupSystemProperties();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        DIService.INSTANCE.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Preferences.class).toInstance(Preferences.empty());
            }
        });
    }

    public static void main(String... args) {
        BaseBoomegaApplication.launchApp(ShadowBoomegaApp.class, args);
    }

    public static class ShadowBoomegaApp extends BoomegaApp {
    }
}
