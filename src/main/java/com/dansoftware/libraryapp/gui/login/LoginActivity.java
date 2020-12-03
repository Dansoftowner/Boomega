package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.login.form.DatabaseLoginListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A LoginActivity can be used for starting easily a {@link LoginWindow} with a {@link LoginView}.
 *
 * <p>
 * It can be started by the {@link LoginActivity#show()}.
 */
public class LoginActivity implements ContextTransformable {

    private final Preferences preferences;
    private final BooleanProperty showing;
    private final LoginView loginView;

    public LoginActivity(@NotNull DatabaseLoginListener databaseLoginListener,
                         @NotNull Preferences preferences,
                         @NotNull LoginData loginData,
                         @NotNull DatabaseTracker tracker) {
        this.showing = new SimpleBooleanProperty();
        this.preferences = Objects.requireNonNull(preferences);
        this.loginView = new LoginView(databaseLoginListener, preferences, loginData, tracker);
    }

    /**
     * Shows the LoginActivity
     */
    public void show() {
        if (!this.isShowing()) {
            LoginWindow loginWindow = new LoginWindow(loginView, preferences);
            this.showing.bind(loginWindow.showingProperty());
            loginWindow.show();
        }
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    public boolean isShowing() {
        return showing.get();
    }

    @Override
    public @NotNull Context getContext() {
        return loginView.getContext();
    }
}
