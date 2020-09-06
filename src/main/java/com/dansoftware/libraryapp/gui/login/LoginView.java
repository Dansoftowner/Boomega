package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.info.InfoView;
import com.dansoftware.libraryapp.gui.info.InfoWindow;
import com.dansoftware.libraryapp.gui.login.form.DatabaseLoginListener;
import com.dansoftware.libraryapp.gui.login.form.LoginForm;
import com.dansoftware.libraryapp.gui.theme.ThemeApplier;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
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
import java.util.List;
import java.util.Objects;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends SimpleHeaderView<LoginView.FormBackground> implements Themeable {

    static final class FormBackground extends StackPane {
        private static final String STYLE_CLASS = "login-form";

        private FormBackground(LoginForm loginForm, DatabaseTracker databaseTracker) {
            super(loginForm);
            getStyleClass().add(STYLE_CLASS);
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
                            .map(DatabaseMeta::new)
                            .forEach(databaseTracker::addDatabase);
                }
            });
        }
    }

    private final LoginForm loginForm;
    private final ObjectProperty<Database> createdDatabase;

    public LoginView(@NotNull Context context,
                     @NotNull DatabaseLoginListener databaseLoginListener,
                     @NotNull LoginData loginData,
                     @NotNull DatabaseTracker tracker) {
        super("LibraryApp", new MaterialDesignIconView(MaterialDesignIcon.BOOK));
        this.createdDatabase = new SimpleObjectProperty<>();
        this.loginForm = new LoginForm(context, loginData, tracker, databaseLoginListener);
        this.setContent(new FormBackground(loginForm, tracker));
        this.init();
    }

    private void init() {
        InfoWindow infoWindow = new InfoWindow(new InfoView());
        this.getToolbarControlsRight().add(new ToolbarItem(
                new MaterialDesignIconView(MaterialDesignIcon.INFORMATION),
                event -> {
                    if (infoWindow.isShowing()) {
                        infoWindow.close();
                    } else {
                        if (Objects.isNull(infoWindow.getOwner()))
                            infoWindow.initOwner(WindowUtils.getStageOf(this));
                        infoWindow.show();
                    }
                }));
    }

    @Override
    public void handleThemeApply(@NotNull ThemeApplier globalApplier, @NotNull ThemeApplier customApplier) {
        customApplier.applyBack(this);
        globalApplier.applyBack(this);
        customApplier.apply(this);
        globalApplier.apply(this);
    }

    public ReadOnlyObjectProperty<Database> createdDatabaseProperty() {
        return createdDatabase;
    }
}
