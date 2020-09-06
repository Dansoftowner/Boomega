package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.util.FurtherFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.scene.Group;
import org.jetbrains.annotations.NotNull;

public class LoginForm extends Group {

    public LoginForm(@NotNull Context context,
                     @NotNull LoginData loginData,
                     @NotNull DatabaseTracker databaseTracker,
                     @NotNull DatabaseLoginListener databaseLoginListener) {
        var fxmlController = new FrameFormController(context, loginData, databaseTracker, databaseLoginListener);
        var fxmlLoader = new FurtherFXMLLoader(fxmlController, getClass().getResource("FrameForm.fxml"), I18N.getFXMLValues());
        this.getChildren().add(fxmlLoader.load());
    }

}
