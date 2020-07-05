package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.info.InfoView;
import com.dansoftware.libraryapp.gui.info.InfoWindow;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dansoftware.libraryapp.gui.workbench.LibraryAppWorkbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.Node;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.dizitart.no2.exceptions.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.Bundles.getNotificationMsg;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends LibraryAppWorkbench {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class);

    /**
     * Field that holds the selected database from the user.
     */
    private Database selectedDatabase;

    private final Consumer<Account> ON_LOGIN_REQUEST = account -> {
        try {
            // File file
            this.selectedDatabase = DatabaseFactory.getDatabase(NITRITE, account);

            StageUtils.getStageOf(this).close();
        } catch (SecurityException e) {
            String title = getNotificationMsg("login.auth.failed.security.title");
            String message = getNotificationMsg("login.auth.failed.security.msg");
            this.showErrorDialog(title, message, buttonType -> {
            });

            LOGGER.error("Failed to create/open database (invalid auth)", e);
        } catch (NitriteIOException e) {
            String title = getNotificationMsg("login.auth.failed.io.title");
            String message = getNotificationMsg("login.auth.failed.io.msg");
            this.showErrorDialog(title, message, e, buttonType -> {
            });

            LOGGER.error("Failed to create/open database (I/O)", e);
        }
    };

    private final LoginForm loginForm;

    public LoginView() {
        this(new LoginData());
    }

    public LoginView(LoginData loginData) {
        this.init();
        this.loginForm = new LoginForm(loginData, ON_LOGIN_REQUEST);
        this.getModules().add(new WorkbenchModule("Login", FontAwesomeIcon.SIGN_IN) {
            @Override
            public Node activate() {
                return LoginView.this.loginForm;
            }
        });
    }

    /**
     *
     */
    private void init() {

        //
        InfoWindow infoWindow = new InfoWindow(new InfoView());
        this.getToolbarControlsRight().add(new ToolbarItem(
                new MaterialDesignIconView(MaterialDesignIcon.INFORMATION),
                event -> {
                    if (infoWindow.isShowing()) {
                        infoWindow.close();
                    } else {
                        if (Objects.isNull(infoWindow.getOwner()))
                            infoWindow.initOwner(StageUtils.getStageOf(this));
                        infoWindow.show();
                    }
                }));
        this.getToolbarControlsLeft().add(new ToolbarItem("Libraryapp", new MaterialDesignIconView(MaterialDesignIcon.BOOK)));
    }

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }

    public LoginData getLoginData() {
        return this.loginForm.getLoginData();
    }
}
