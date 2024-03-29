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

import com.dansoftware.boomega.gui.preloader.BoomegaPreloader;
import com.dansoftware.boomega.main.PropertiesSetup;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

public class PreloaderSimulation {

    public static void main(String[] args) {
        PropertiesSetup.setupSystemProperties();
        LauncherImpl.launchApplication(AppImpl.class, BoomegaPreloader.class, args);
    }

    public static class AppImpl extends Application {

        @Override
        public void init() throws Exception {
            synchronized (this) {
                for (int i = 0; i <= 100; i++) {
                    notifyPreloader(new BoomegaPreloader.MessageNotification("Message " + i));
                    if (i == 100) i = 0;
                    Thread.sleep(1000);
                }

                this.wait();
            }
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
        }
    }
}
