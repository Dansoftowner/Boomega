package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.main.Globals;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginWindow extends Stage {

    public LoginWindow(Parent root) {
        this.setScene(initScene(root));
        //this.setTitle();
        this.getIcons().add(Globals.WINDOW_ICON);
    }

    private Scene initScene(Parent root) {
        return new Scene(root);
    }

}
