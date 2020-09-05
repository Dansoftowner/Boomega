package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.login.LoginActivity;
import com.dansoftware.libraryapp.gui.mainview.MainActivity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An {@link EntryActivity} puts the {@link LoginActivity} and {@link MainActivity} activity together.
 *
 * <p>
 * When an {@link EntryActivity} is started, it creates a {@link LoginActivity} and shows that and after the {@link LoginActivity} exited,
 * it launches a {@link MainActivity}.
 *
 * <p>
 * Implements the {@link Context} interface so we can send message-requests to the backing activity whether it's a {@link LoginActivity}
 * or a {@link MainActivity}.
 *
 * <p>
 * The active {@link EntryActivity} objects are tracked so they are accessible through the {@link #getShowingEntries()}.
 *
 * @author Daniel Gyorffy
 */
public class EntryActivity implements Context, ChangeListener<Database> {

    private static final List<WeakReference<EntryActivity>> instances =
            Collections.synchronizedList(new LinkedList<>());

    private Context subContext;
    private final LoginData loginData;
    private final DatabaseTracker databaseTracker;

    /**
     * Creates an {@link EntryActivity} with the {@link LoginData}.
     *
     * @param loginData       the login-data object that will be passed to the {@link LoginActivity}
     * @param databaseTracker the {@link DatabaseTracker} object for observing other databases
     */
    public EntryActivity(@NotNull LoginData loginData, @NotNull DatabaseTracker databaseTracker) {
        instances.add(new WeakReference<>(this));
        this.loginData = loginData;
        this.databaseTracker = databaseTracker;
    }

    @Override
    public void changed(ObservableValue<? extends Database> observable, Database oldValue, Database createdDatabase) {
        if (createdDatabase != null) {
            var mainActivity = new MainActivity(createdDatabase);
            this.subContext = mainActivity;
            mainActivity.show();

            //removing this object from the listeners
            observable.removeListener(this);
        }
    }

    /**
     * Starts the {@link EntryActivity}.
     */
    public void show() {
        if (!this.isShowing()) {
            var loginActivity = new LoginActivity(loginData, databaseTracker);
            this.subContext = loginActivity;
            loginActivity.show();
            loginActivity.createdDatabaseProperty().addListener(this);
        }
    }

    public boolean isShowing() {
        return subContext != null && subContext.isShowing();
    }

    @Override
    public void showOverlay(Region region) {
        if (subContext != null)
            this.subContext.showOverlay(region);
    }

    @Override
    public void showOverlay(Region region, boolean blocking) {
        if (subContext != null)
            this.subContext.showOverlay(region, blocking);
    }

    @Override
    public void hideOverlay(Region region) {
        if (subContext != null)
            this.subContext.hideOverlay(region);
    }

    @Override
    public void showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {
        if (subContext != null)
            this.subContext.showErrorDialog(title, message, onResult);
    }

    @Override
    public void showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {
        if (subContext != null)
            this.subContext.showErrorDialog(title, message, exception, onResult);
    }

    @Override
    public void showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        if (subContext != null)
            this.subContext.showInformationDialog(title, message, onResult);
    }

    @Override
    public Window getContextWindow() {
        return subContext != null ? subContext.getContextWindow() : null;
    }

    @Override
    public void requestFocus() {
        if (subContext != null)
            this.subContext.requestFocus();
    }

    @Override
    public void toFront() {
        if (subContext != null)
            this.subContext.toFront();
    }

    /**
     * Returns a list of {@link EntryActivity} objects that are showing.
     *
     * @return the {@link EntryActivity} list
     */
    public static List<EntryActivity> getShowingEntries() {
        return instances.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .filter(EntryActivity::isShowing)
                .collect(Collectors.toList());
    }
}
