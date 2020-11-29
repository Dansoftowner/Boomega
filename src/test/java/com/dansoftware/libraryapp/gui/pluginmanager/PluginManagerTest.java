package com.dansoftware.libraryapp.gui.pluginmanager;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.main.PropertiesResponsible;
import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class PluginManagerTest extends Application {

    static {
        PropertiesResponsible.setupSystemProperties();
        Theme.setDefault(Preferences.getPreferences().get(Preferences.Key.THEME));
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
