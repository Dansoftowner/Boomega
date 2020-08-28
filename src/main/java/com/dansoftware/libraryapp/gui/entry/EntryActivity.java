package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.LoginActivity;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.entry.mainview.MainActivity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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

    private static final ObservableSet<EntryActivity> showingEntries =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private static final ObservableSet<EntryActivity> showingEntriesUnmodifiable =
            FXCollections.unmodifiableObservableSet(showingEntries);

    private final BooleanProperty showing;
    private final LoginActivity loginActivity;

    private Context subContext;

    /**
     * Creates a basic {@link EntryActivity} with empty {@link LoginData}.
     *
     * @see LoginData#empty()
     * @see EntryActivity#EntryActivity(LoginData)
     */
    public EntryActivity() {
        this(LoginData.empty());
    }

    /**
     * Creates an {@link EntryActivity} with the {@link LoginData}.
     *
     * @param loginData the login-data object that will be passed to the {@link LoginActivity}
     */
    public EntryActivity(@NotNull LoginData loginData) {
        this.loginActivity = new LoginActivity(loginData);
        this.subContext = loginActivity;
        this.showing = new SimpleBooleanProperty();
        this.showing.bind(this.loginActivity.showingProperty());
        this.showing.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                EntryActivity.showingEntries.add(this);
            } else {
                EntryActivity.showingEntries.remove(this);
            }
        });
    }

    @Override
    public void changed(ObservableValue<? extends Database> observable, Database oldValue, Database createdDatabase) {
        if (createdDatabase != null) {
            var mainActivity = new MainActivity(createdDatabase);
            this.subContext = mainActivity;
            this.showing.unbind();
            this.showing.bind(mainActivity.showingProperty());
            mainActivity.show();

            //removing this object from the listeners
            observable.removeListener(this);
        }
    }

    /**
     * Starts the {@link EntryActivity}.
     */
    public void show() {
        loginActivity.show();
        loginActivity.createdDatabaseProperty().addListener(this);
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    @Override
    public void showOverlay(Region region) {
        this.subContext.showOverlay(region);
    }

    @Override
    public void showOverlay(Region region, boolean blocking) {
        this.subContext.showOverlay(region, blocking);
    }

    @Override
    public void hideOverlay(Region region) {
        this.subContext.hideOverlay(region);
    }

    @Override
    public void showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {
        this.subContext.showErrorDialog(title, message, onResult);
    }

    @Override
    public void showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {
        this.subContext.showErrorDialog(title, message, exception, onResult);
    }

    @Override
    public void showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        this.subContext.showInformationDialog(title, message, onResult);
    }

    @Override
    public void requestFocus() {
        this.subContext.requestFocus();
    }

    /**
     * Returns a read-only set that contains all {@link EntryActivity} objects that is
     * showing.
     *
     * @return the set of AppEntry objects.
     */
    public static ObservableSet<EntryActivity> getShowingEntries() {
        return showingEntriesUnmodifiable;
    }
}
