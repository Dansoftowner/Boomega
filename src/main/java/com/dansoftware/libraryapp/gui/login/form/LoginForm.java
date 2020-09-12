package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.scene.Group;
import org.jetbrains.annotations.NotNull;

public class LoginForm extends Group {

    private final FrameFormController fxmlController;

    public LoginForm(@NotNull Context context,
                     @NotNull LoginData loginData,
                     @NotNull DatabaseTracker databaseTracker,
                     @NotNull DatabaseLoginListener databaseLoginListener) {
        this.fxmlController = new FrameFormController(context, loginData, databaseTracker, databaseLoginListener);
        var fxmlLoader = new ImprovedFXMLLoader(fxmlController, getClass().getResource("FrameForm.fxml"), I18N.getFXMLValues());
        this.getChildren().add(fxmlLoader.load());
    }

    public LoginData getLoginData() {
        return fxmlController.getLoginData();
    }

}
