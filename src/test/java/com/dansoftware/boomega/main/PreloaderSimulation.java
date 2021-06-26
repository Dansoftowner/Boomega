package com.dansoftware.boomega.main;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

public class PreloaderSimulation extends Application {
    public static void main(String[] args) {
        PropertiesSetup.setupSystemProperties();
        LauncherImpl.launchApplication(PreloaderSimulation.class, Preloader.class, args);
    }

    @Override
    public void init() throws Exception {
        synchronized (this) {
            for (int i = 0; i <= 100; i++) {
                notifyPreloader(new Preloader.MessageNotification(false, "Message {0}", i));
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
