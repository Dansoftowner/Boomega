package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.info.InformationActivity;
import com.dansoftware.libraryapp.gui.login.form.DatabaseLoginListener;
import com.dansoftware.libraryapp.gui.login.form.LoginForm;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 *
 * @author Daniel Gyorffy
 */
public class LoginView extends SimpleHeaderView<LoginView.FormBase> implements Themeable {

    private final Context context;
    private final LoginForm loginForm;
    private final ObjectProperty<Database> createdDatabase;

    public LoginView(@NotNull Context context,
                     @NotNull DatabaseLoginListener databaseLoginListener,
                     @NotNull LoginData loginData,
                     @NotNull DatabaseTracker tracker) {
        super(I18N.getGeneralWord("database.auth"), new MaterialDesignIconView(MaterialDesignIcon.LOGIN));
        this.context = Objects.requireNonNull(context, "Context shouldn't be null");
        this.createdDatabase = new SimpleObjectProperty<>();
        this.loginForm = new LoginForm(context, loginData, tracker, databaseLoginListener);
        this.setContent(new FormBase(loginForm, tracker));
        this.createToolbarControls();
        Theme.registerThemeable(this);
    }

    private void createToolbarControls() {
        this.getToolbarControlsRight().add(new ToolbarItem(
                new MaterialDesignIconView(MaterialDesignIcon.INFORMATION),
                event -> {
                    var informationActivity = new InformationActivity(context);
                    informationActivity.show();
                }));
    }

    @Override
    public void handleThemeApply(Theme newTheme) {
        newTheme.apply(this);
    }

    public LoginData getLoginData() {
        return loginForm.getLoginData();
    }

    public ReadOnlyObjectProperty<Database> createdDatabaseProperty() {
        return createdDatabase;
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
