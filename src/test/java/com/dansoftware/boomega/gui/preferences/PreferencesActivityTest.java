package com.dansoftware.boomega.gui.preferences;

import com.dansoftware.boomega.appdata.Preferences;
import javafx.application.Application;
import javafx.stage.Stage;

public class PreferencesActivityTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new PreferencesActivity(Preferences.empty()).show(null);
    }
}
