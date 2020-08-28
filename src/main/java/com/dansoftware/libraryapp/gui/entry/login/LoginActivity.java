package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import javafx.beans.property.*;
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
    private final ObjectProperty<Database> createdDatabase;
    private LoginView loginView;

    public LoginActivity(@NotNull LoginData loginData) {
        this.showing = new SimpleBooleanProperty();
        this.loginView = new LoginView(loginData);
        this.createdDatabase = new SimpleObjectProperty<>();
        this.createdDatabase.bind(loginView.createdDatabaseProperty());
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

    public ObjectProperty<Database> createdDatabaseProperty() {
        return createdDatabase;
    }

    public Optional<Database> getCreatedDatabase() {
        return Optional.ofNullable(createdDatabase.get());
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
