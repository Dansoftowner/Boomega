package com.dansoftware.boomega.gui.login;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.appdata.logindata.LoginData;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.login.form.LoginForm;
import com.dansoftware.boomega.i18n.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 *
 * @author Daniel Gyorffy
 */
public class LoginView extends SimpleHeaderView<LoginView.FormBase> implements ContextTransformable {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final LoginActivity loginActivity;

    private final Context asContext;
    private final Preferences preferences;
    private final LoginForm loginForm;
    private final ObjectProperty<Database> createdDatabase;

    public LoginView(@NotNull LoginActivity loginActivity,
                     @NotNull DatabaseLoginListener databaseLoginListener,
                     @NotNull Preferences preferences,
                     @NotNull LoginData loginData,
                     @NotNull DatabaseTracker tracker) {
        super(I18N.getValue("database.auth"), new MaterialDesignIconView(MaterialDesignIcon.LOGIN));
        this.loginActivity = loginActivity;
        this.preferences = preferences;
        this.asContext = Context.from(this);
        this.createdDatabase = new SimpleObjectProperty<>();
        this.loginForm = new LoginForm(asContext, preferences, loginData, tracker, databaseLoginListener);
        this.setContent(new FormBase(loginForm, tracker));
        this.createToolbarControls();
    }

    private void createToolbarControls() {
        this.getToolbarControlsRight().addAll(new ToolbarItemsBuilder(asContext, preferences).build());
    }

    public LoginData getLoginData() {
        return loginForm.getLoginData();
    }

    public ReadOnlyObjectProperty<Database> createdDatabaseProperty() {
        return createdDatabase;
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }

    protected ObservableStringValue titleProperty() {
        return this.loginForm.titleProperty();
    }

    /**
     * A {@link FormBase} is the parent of a {@link LoginForm}.
     * It can be styled in css through the <i>login-form</i> class-name.
     *
     * <p>
     * It has drag-support which means that the user can drag files into it
     * and it's adding them into the {@link DatabaseTracker}.
     */
    static final class FormBase extends StackPane {
        private static final String STYLE_CLASS = "login-form";

        private final DatabaseTracker databaseTracker;

        private FormBase(@NotNull LoginForm loginForm,
                         @NotNull DatabaseTracker databaseTracker) {
            super(loginForm);
            this.databaseTracker = databaseTracker;
            this.getStyleClass().add(STYLE_CLASS);
            this.enableDragSupport();
        }

        private void enableDragSupport() {
            setOnDragOver(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            });

            setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    dragboard.getFiles().stream()
                            .filter(File::isFile)
                            .map(DatabaseMeta::new)
                            .forEach(databaseTracker::addDatabase);
                }
            });
        }
    }
}
