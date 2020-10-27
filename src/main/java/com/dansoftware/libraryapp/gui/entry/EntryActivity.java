package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextDialog;
import com.dansoftware.libraryapp.gui.context.ContextSupplier;
import com.dansoftware.libraryapp.gui.login.LoginActivity;
import com.dansoftware.libraryapp.gui.login.form.DatabaseLoginListener;
import com.dansoftware.libraryapp.gui.mainview.MainActivity;
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
public class EntryActivity implements ContextSupplier, DatabaseLoginListener {

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
    public void onDatabaseOpened(@NotNull Database database) {
        var mainActivity = new MainActivity(database);
        this.subContext = mainActivity.getContext();
        mainActivity.show();
    }

    /**
     * Starts the {@link EntryActivity}.
     */
    public void show() {
        if (!this.isShowing()) {
            var loginActivity = new LoginActivity(this, loginData, databaseTracker);
            this.subContext = loginActivity.getContext();
            loginActivity.show();
        }
    }

    public boolean isShowing() {
        return subContext != null && subContext.isShowing();
    }

    @Override
    public @NotNull Context getContext() {
        return subContext;
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
