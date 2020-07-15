package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.info.InfoView;
import com.dansoftware.libraryapp.gui.info.InfoWindow;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.dizitart.no2.exceptions.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.I18N.getAlertMsg;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends SimpleHeaderView<LoginForm> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class);

    /**
     * Field that holds the selected database from the user.
     */
    private Database selectedDatabase;

    /**
     * Defines what happenes when the user wants to sign in by the login-form
     */
    private final Consumer<Account> ON_LOGIN_REQUEST = account -> {
        try {
            // File file
            this.selectedDatabase = DatabaseFactory.getDatabase(NITRITE, account);

            //starting a thread that saves the login-data
            new Thread(new LoginDataSaver(this.getLoginData())).start();
            WindowUtils.getStageOf(this).close();
        } catch (SecurityException e) {
            String title = getAlertMsg("login.auth.failed.security.title");
            String message = getAlertMsg("login.auth.failed.security.msg");
            this.showErrorDialog(title, message, buttonType -> {
            });

            LOGGER.error("Failed to create/open database (invalid auth)", e);
        } catch (NitriteIOException e) {
            String title = getAlertMsg("login.auth.failed.io.title");
            String message = getAlertMsg("login.auth.failed.io.msg");
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
        super("LibraryApp", new MaterialDesignIconView(MaterialDesignIcon.BOOK));
        super.setContent(loginForm = new LoginForm(loginData, ON_LOGIN_REQUEST));
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

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }

    public LoginData getLoginData() {
        return this.loginForm.getLoginData();
    }
}
