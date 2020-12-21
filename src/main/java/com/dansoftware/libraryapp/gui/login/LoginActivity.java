package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A LoginActivity can be used for starting easily a {@link LoginWindow} with a {@link LoginView}.
 *
 * <p>
 * It can be started by the {@link LoginActivity#show()}.
 */
public class LoginActivity implements ContextTransformable {

    private static final List<WeakReference<LoginActivity>> instances = new LinkedList<>();

    private final Preferences preferences;
    private final BooleanProperty showing;
    private LoginView loginView;

    public LoginActivity(@NotNull DatabaseLoginListener databaseLoginListener,
                         @NotNull Preferences preferences,
                         @NotNull LoginData loginData,
                         @NotNull DatabaseTracker tracker) {
        this.showing = new SimpleBooleanProperty();
        this.preferences = Objects.requireNonNull(preferences);
        this.loginView = new LoginView(this, databaseLoginListener, preferences, loginData, tracker);
        instances.add(new WeakReference<>(this));
    }

    /**
     * Shows the LoginActivity
     */
    public void show() {
        if (!this.isShowing()) {
            LoginWindow loginWindow = buildWindow();
            this.showing.bind(loginWindow.showingProperty());
            loginWindow.show();
        }
    }

    private LoginWindow buildWindow() {
        final var loginWindow = new LoginWindow(loginView, preferences);
        loginWindow.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            this.loginView = null;
            this.showing.unbind();
            this.showing.set(false);
        });
        return loginWindow;
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

    public static List<LoginActivity> getActiveLoginActivites() {
        return instances.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .filter(LoginActivity::isShowing)
                .collect(Collectors.toList());
    }
}
