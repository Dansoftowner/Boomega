package com.dansoftware.libraryapp.gui.login.quick;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.login.DatabaseLoginListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class QuickFormController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(QuickFormController.class);

    private final Context context;
    private final DatabaseMeta databaseMeta;
    private final DatabaseLoginListener loginListener;

    QuickFormController(@NotNull Context context,
                        @NotNull DatabaseMeta databaseMeta,
                        @NotNull DatabaseLoginListener loginListener) {
        this.context = context;
        this.databaseMeta = databaseMeta;
        this.loginListener = loginListener;
    }

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private void login(ActionEvent event) {
        String username = StringUtils.trim(usernameInput.getText());
        String password = StringUtils.trim(passwordInput.getText());

        Database database = NitriteDatabase.getAuthenticator()
                .onFailed((title, message, t) -> {
                    this.context.showErrorDialog(title, message, ((Exception) t));
                    logger.error("Failed to create/open the database", t);
                }).auth(databaseMeta, new Credentials(username, password));

        if (database != null) {
            //creating the database was successful
            logger.debug("Quick login in was successful");
            loginListener.onDatabaseOpened(database);
            context.close();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
