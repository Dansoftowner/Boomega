package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.login.form.DatabaseLoginListener;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A LoginActivity can be used for starting easily a {@link LoginWindow} with a {@link LoginView}.
 *
 * <p>
 * It can be started by the {@link LoginActivity#show()}.
 */
public class LoginActivity implements Context {

    private final BooleanProperty showing;
    private final LoginView loginView;

    public LoginActivity(@NotNull DatabaseLoginListener databaseLoginListener,
                         @NotNull LoginData loginData,
                         @NotNull DatabaseTracker tracker) {
        this.showing = new SimpleBooleanProperty();
        this.loginView = new LoginView(this, databaseLoginListener, loginData, tracker);
    }

    /**
     * Shows the LoginActivity
     */
    public void show() {
        if (!this.isShowing()) {
            LoginWindow loginWindow = new LoginWindow(loginView);
            this.showing.bind(loginWindow.showingProperty());
            loginWindow.show();
        }
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    @Override
    public boolean isShowing() {
        return showing.get();
    }

    @Override
    public void showOverlay(Region region) {
        this.loginView.showOverlay(region, false);
        StackPane.setAlignment(region, Pos.CENTER);
    }

    @Override
    public void showOverlay(Region region, boolean blocking) {
        this.loginView.showOverlay(region, blocking);
        StackPane.setAlignment(region, Pos.CENTER);
    }

    @Override
    public void hideOverlay(Region region) {
        this.loginView.hideOverlay(region);
    }

    @Override
    public void showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {
        this.loginView.showErrorDialog(title, message, onResult);
    }

    @Override
    public void showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {
        this.loginView.showErrorDialog(title, message, exception, onResult);
    }

    @Override
    public void showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        this.loginView.showInformationDialog(title, message, onResult);
    }

    @Override
    public Window getContextWindow() {
        return WindowUtils.getWindowOf(loginView);
    }

    @Override
    public void requestFocus() {
        WindowUtils.getWindowOptionalOf(this.loginView).ifPresent(Window::requestFocus);
    }

    @Override
    public void toFront() {
        WindowUtils.getStageOptionalOf(this.loginView).ifPresent(Stage::toFront);
    }
}
