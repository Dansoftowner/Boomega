/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.simulation;

import com.dansoftware.boomega.config.DummyConfigSource;
import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.exception.UncaughtExceptionHandler;
import com.dansoftware.boomega.gui.app.BaseBoomegaApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;
import com.dansoftware.boomega.main.PropertiesSetup;
import com.dansoftware.boomega.main.bindings.ConcurrencyModule;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.dansoftware.boomega.simulation.util.DummyPluginService;
import com.dansoftware.boomega.update.UpdateSearcher;
import com.google.inject.AbstractModule;

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
