package com.dansoftware.boomega.gui.login;

import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.login.quick.QuickLoginActivity;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class QuickLoginUITest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new QuickLoginActivity(new DatabaseMeta("Thing", new File(".")), db -> {
        }).show();
    }
}
