package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.LoginActivity;
import com.dansoftware.libraryapp.gui.entry.mainview.MainActivity;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class AppEntry implements Context {

    private final LoginActivity loginActivity;
    private Context subContext;

    public AppEntry() {
        this.loginActivity = new LoginActivity();
        this.subContext = loginActivity;
    }

    public AppEntry(@NotNull Preferences preferences) {
        this.loginActivity = new LoginActivity(preferences.get(Preferences.Key.LOGIN_DATA));
        this.subContext = loginActivity;
    }

    public boolean show() {
        Optional<Database> databaseOptional = loginActivity.show();
        databaseOptional.ifPresent(database -> {
            var mainView = new MainActivity(database);
            mainView.show();
            this.subContext = mainView;
        });

        return databaseOptional.isPresent();
    }

    @Override
    public void showOverlay(Region region) {
        Platform.runLater(() -> this.subContext.showOverlay(region));
    }

    @Override
    public void showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {
        Platform.runLater(() -> this.subContext.showErrorDialog(title, message, onResult));
    }

    @Override
    public void showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {
        Platform.runLater(() -> this.subContext.showErrorDialog(title, message, exception, onResult));
    }
}
