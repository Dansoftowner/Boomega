package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.Preferences;

public class FirstTimeSimulation {

    static {
        PropertiesSetup.setupSystemProperties();
        Preferences.setDefault(Preferences.empty());
    }

    public static void main(String... args) {
        Main.main(args);
    }
}
