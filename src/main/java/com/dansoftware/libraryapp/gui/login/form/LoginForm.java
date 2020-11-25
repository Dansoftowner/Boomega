package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.Group;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link LoginForm} is the most important component of a {@link com.dansoftware.libraryapp.gui.login.LoginView}
 * it is the actual "login box".
 *
 * @author Daniel Gyorffy
 */
public class LoginForm extends Group {

    private final FrameFormController fxmlController;

    public LoginForm(@NotNull Context context,
                     @NotNull Preferences preferences,
                     @NotNull LoginData loginData,
                     @NotNull DatabaseTracker databaseTracker,
                     @NotNull DatabaseLoginListener databaseLoginListener) {
        this.fxmlController = new FrameFormController(context, preferences, loginData, databaseTracker, databaseLoginListener);
        var fxmlLoader = new ImprovedFXMLLoader(fxmlController, getClass().getResource("FrameForm.fxml"), I18N.getLoginViewValues());
        this.getChildren().add(fxmlLoader.load());
    }

    public LoginData getLoginData() {
        return fxmlController.getLoginData();
    }

    public ObservableStringValue titleProperty() {
        return fxmlController.titleProperty();
    }
}
