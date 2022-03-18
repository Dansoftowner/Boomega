package com.dansoftware.boomega.main;

import com.dansoftware.boomega.gui.preloader.BoomegaPreloader;
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
