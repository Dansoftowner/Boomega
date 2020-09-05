package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.db.processor.LoginProcessor;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.appdata.Preferences.getPreferences;

public class InternalFormController implements Initializable {

    interface SelectedItemAccessor {

    }

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private CheckBox rememberBox;

    @FXML
    private void login() {
        DatabaseMeta dbMeta = sourceChooser.getSelectionModel().getSelectedItem();
        File dbFile = dbMeta.getFile();
        String username = StringUtils.trim(usernameInput.getText());
        String password = StringUtils.trim(passwordInput.getText());

        //creating an object that holds the credentials (username/password)
        Credentials credentials = new Credentials(username, password);

        if (rememberBox.isSelected()) {
            loginData.setAutoLoginDatabase(dbMeta);
            loginData.setAutoLoginCredentials(credentials);
        } else {
            loginData.setAutoLoginDatabase(null);
            loginData.setAutoLoginCredentials(null);
        }

        this.createdDatabase.set(LoginProcessor.of(NitriteDatabase.factory())
                .onFailed((title, message, t) -> {
                    this.loginView.showErrorDialog(title, message, ((Exception) t), buttonType -> {
                    });

                    logger.error("Failed to create/open the database", t);
                }).process(dbMeta, credentials));

        if (this.createdDatabase.get() != null) {
            //creating the database was successful
            logger.debug("Signing in was successful; closing the LoginWindow");
            //starting a thread that saves the login-data
            new Thread(() -> {
                getPreferences().editor()
                        .set(Preferences.Key.LOGIN_DATA, loginData)
                        .tryCommit();
            }).start();
            logger.debug("The window is '{}'", WindowUtils.getStageOf(this));

            WindowUtils.getStageOf(this).close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
