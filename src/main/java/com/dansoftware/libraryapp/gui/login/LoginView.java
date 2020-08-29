package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.info.InfoView;
import com.dansoftware.libraryapp.gui.info.InfoWindow;
import com.dansoftware.libraryapp.gui.theme.ThemeApplier;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends SimpleHeaderView<LoginForm> implements Themeable {

    private final LoginForm loginForm;
    private final ObjectProperty<Database> createdDatabase;

    public LoginView(@NotNull LoginData loginData, @NotNull DatabaseTracker tracker) {
        super("LibraryApp", new MaterialDesignIconView(MaterialDesignIcon.BOOK));
        this.createdDatabase = new SimpleObjectProperty<>();
        this.loginForm = new LoginForm(this, loginData, tracker);
        this.setContent(loginForm);
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

    public LoginData getLoginData() {
        return this.loginForm.getLoginData();
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
