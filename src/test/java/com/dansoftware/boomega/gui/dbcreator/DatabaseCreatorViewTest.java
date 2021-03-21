package com.dansoftware.boomega.gui.dbcreator;

import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.main.PropertiesResponsible;
import javafx.application.Application;
import javafx.stage.Stage;

public class DatabaseCreatorViewTest extends Application {

    static {
        PropertiesResponsible.setupSystemProperties();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new DatabaseCreatorActivity().show(DatabaseTracker.getGlobal(), null);
    }
}
