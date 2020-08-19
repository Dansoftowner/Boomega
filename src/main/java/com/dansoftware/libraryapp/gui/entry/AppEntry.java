package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.login.LoginActivity;
import com.dansoftware.libraryapp.gui.entry.login.LoginData;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class AppEntry implements Context, ChangeListener<Boolean> {

    private static final ObservableSet<AppEntry> showingEntries =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private static final ObservableSet<AppEntry> showingEntriesUnmodifiable =
            FXCollections.unmodifiableObservableSet(showingEntries);

    private static final ObservableSet<DatabaseMeta> openedDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private static final ObservableSet<DatabaseMeta> openedDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(openedDatabases);

    private final BooleanProperty showing;
    private final LoginActivity loginActivity;

    private Context subContext;
    private DatabaseMeta openedDatabase;

    public AppEntry() {
        this(LoginData.empty());
    }

    public AppEntry(@NotNull LoginData loginData) {
        this.loginActivity = new LoginActivity(loginData);
        this.subContext = loginActivity;
        this.showing = new SimpleBooleanProperty();
        this.showing.bind(this.loginActivity.showingProperty());
        this.showing.addListener(this);
    }

    @Deprecated
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean showing) {
        if (observable == this.showing) {
            if (showing) {
                AppEntry.showingEntries.add(this);
                if (this.openedDatabase != null)
                    AppEntry.openedDatabases.add(this.openedDatabase);
            } else {
                AppEntry.showingEntries.remove(this);
                AppEntry.openedDatabases.remove(this.openedDatabase);
            }
        }
    }

    public boolean show() {
        Optional<Database> databaseOptional = loginActivity.show();
        databaseOptional.ifPresent(database -> {
            this.openedDatabase = database.getMeta();
            var mainActivity = new MainActivity(database);
            this.subContext = mainActivity;
            this.showing.unbind();
            this.showing.bind(mainActivity.showingProperty());
            mainActivity.show();
        });

        return databaseOptional.isPresent();
    }

    @Nullable
    public DatabaseMeta getOpenedDatabase() {
        return this.openedDatabase;
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
     * Returns a read-only set that contains all {@link AppEntry} objects that is
     * showing.
     *
     * @return the set of AppEntry objects.
     */
    public static ObservableSet<AppEntry> getShowingEntries() {
        return showingEntriesUnmodifiable;
    }

    /**
     * Returns a read-only set of {@link DatabaseMeta} object.
     * Each object represents a database that is successfully opened
     * with an {@link AppEntry}.
     *
     * @return a set of DatabaseMeta objects.
     */
    public static ObservableSet<DatabaseMeta> getOpenedDatabases() {
        return openedDatabasesUnmodifiable;
    }

    @NotNull
    public static Optional<AppEntry> getEntryOf(@Nullable DatabaseMeta databaseMeta) {
        if (databaseMeta == null)
            return Optional.empty();

        return getShowingEntries()
                .stream()
                .filter(entry -> databaseMeta.equals(entry.getOpenedDatabase()))
                .findAny();
    }
}
