package com.dansoftware.boomega.gui.login.form;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.appdata.logindata.LoginData;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.login.DatabaseLoginListener;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.Group;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link LoginForm} is the most important component of a {@link com.dansoftware.boomega.gui.login.LoginView}
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
        var fxmlLoader = new ImprovedFXMLLoader(fxmlController, getClass().getResource("FrameForm.fxml"), I18N.getValues());
        this.getChildren().add(fxmlLoader.load());
    }

    public LoginData getLoginData() {
        return fxmlController.getLoginData();
    }

    public ObservableStringValue titleProperty() {
        return fxmlController.titleProperty();
    }
}
