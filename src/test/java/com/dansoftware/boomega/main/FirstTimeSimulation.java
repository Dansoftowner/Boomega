package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.DummyConfigSource;
import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.exception.UncaughtExceptionHandler;
import com.dansoftware.boomega.gui.app.BaseBoomegaApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;
import com.dansoftware.boomega.plugin.DummyPluginService;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.dansoftware.boomega.update.UpdateSearcher;
import com.google.inject.AbstractModule;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class FirstTimeSimulation {

    static {
        PropertiesSetup.setupSystemProperties();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        DIService.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ConfigSource.class).toInstance(new DummyConfigSource(true));
                bind(PluginService.class).to(DummyPluginService.class);
                bind(UpdateSearcher.class).toInstance(mock(UpdateSearcher.class));
            }
        }, new ConcurrencyModule());
    }

    public static void main(String... args) {
        BaseBoomegaApplication.launchApp(ShadowBoomegaApp.class, args);
    }

    public static class ShadowBoomegaApp extends BoomegaApp {
    }
}
