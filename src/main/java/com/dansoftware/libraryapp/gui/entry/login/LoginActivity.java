package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A LoginActivity can be used for starting easily a {@link LoginWindow} with a {@link LoginView}.
 *
 * <p>
 * It can be started by the {@link LoginActivity#show()}.
 */
public class LoginActivity implements Context {

    private final BooleanProperty showing;
    private LoginView loginView;

    private LoginActivity() {
        this.showing = new SimpleBooleanProperty();
    }

    public LoginActivity(@NotNull LoginData loginData) {
        this();
        this.loginView = new LoginView(loginData);
    }

    /**
     * Waits until the user signs in or closes the login window, then
     * returns the selected {@link Database} wrapped in an {@link Optional}.
     * If the {@link Optional} is empty that means that the user closed the
     * {@link LoginWindow}.
     *
     * @return the selected {@link Database} wrapped in an {@link Optional}.
     */
    public Optional<Database> show() {
        LoginWindow loginWindow = new LoginWindow(loginView);
        this.showing.bind(loginWindow.showingProperty());
        loginWindow.showAndWait();

        return Optional.ofNullable(loginView.getSelectedDatabase());
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
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
    public void requestFocus() {
        this.loginView.getScene().getWindow().requestFocus();
    }
}
