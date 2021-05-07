package com.dansoftware.boomega.gui.pluginmngr;

import com.dansoftware.boomega.config.PreferenceKey;
import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.main.PropertiesSetup;
import com.dansoftware.boomega.plugin.PluginClassLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class PluginManagerTest extends Application {

    static {
        PropertiesSetup.setupSystemProperties();
        Theme.setDefault(Preferences.getPreferences().get(PreferenceKey.THEME));
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PluginManagerActivity pluginManagerActivity = new PluginManagerActivity();
        pluginManagerActivity.show();
    }

    @Override
    public void stop() throws Exception {
        PluginClassLoader.getInstance().close();
        System.exit(0);
    }
}
