package com.dansoftware.boomega.gui.dbcreator;

import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.main.PropertiesSetup;
import javafx.application.Application;
import javafx.stage.Stage;

public class DatabaseCreatorViewTest extends Application {

    static {
        PropertiesSetup.setupSystemProperties();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new DatabaseCreatorActivity().show(DatabaseTracker.getGlobal(), null);
    }
}
