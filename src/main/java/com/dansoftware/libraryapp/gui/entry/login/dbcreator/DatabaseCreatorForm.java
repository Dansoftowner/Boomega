package com.dansoftware.libraryapp.gui.entry.login.dbcreator;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.util.SpaceValidator;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dansoftware.libraryapp.main.Globals;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.exceptions.NitriteIOException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.Bundles.getFXMLValues;
import static com.dansoftware.libraryapp.locale.Bundles.getNotificationMsg;

public class DatabaseCreatorForm extends StackPane implements Initializable {

    @FXML
    private VBox vBox;

    @FXML
    private Button dirOpenerBtn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField dirField;

    @FXML
    private TextField fullPathField;

    @FXML
    private CheckBox authCheckBox;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button createBtn;

    private Account createdAccount;

    public DatabaseCreatorForm() {
        loadGui();
    }

    private void loadGui() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DatabaseCreatorForm.fxml"), getFXMLValues());
        fxmlLoader.setController(this);

        try {
            this.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @return
     */
    public Optional<Account> getCreatedAccount() {
        return Optional.ofNullable(this.createdAccount);
    }

    private void showError(String message) {
        JFXDialog jfxDialog = new JFXDialog(this, new Label(message), JFXDialog.DialogTransition.CENTER);
        jfxDialog.show();
    }

    @FXML
    private void create() {

        File dir = new File(dirField.getText());
        File dbFile = new File(fullPathField.getText());

        //--------------------->
        // VALIDATING THE INPUTS

        if (StringUtils.isBlank(this.nameField.getText())) {
            showError("You have to specify a name!");
            return;
        }

        if (!dir.exists()) {
            boolean directoriesCreated = dir.mkdirs();
            if (!directoriesCreated) {
                //
            }
        }

        if (dbFile.exists()) {
            showError("File '" + dbFile + "' already exists.");
            return;
        }

        String username = null;
        String password = null;
        if (authCheckBox.isSelected()) {
            username = usernameField.getText();
            password = passwordField.getText();
        }

        if (StringUtils.isBlank(username)) {
            //
        }
        if (StringUtils.isBlank(password)) {
            //
        }

        // <---------------------

        //----------------------->
        // TRYING TO CREATE THE DATABASE

        Account account = new Account(dbFile, username, password);

        try {
            // File file
            DatabaseFactory.getDatabase(NITRITE, account);

            //StageUtils.getStageOf(this).close();
        } catch (NitriteIOException e) {
            String title = getNotificationMsg("login.auth.failed.io.title");
            String message = getNotificationMsg("login.auth.failed.io.msg");
        }

        //<-----------------------------

    }

    @FXML
    private void openDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(StageUtils.getWindowOf(this));

        if (Objects.nonNull(selectedDir)) {
            this.dirField.setText(selectedDir.getAbsolutePath());

            if (StringUtils.isBlank(this.nameField.getText())) {
                new animatefx.animation.Tada(this.nameField).play();
            }
        }
    }

    private void setDefaults() {
        this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.dirOpenerBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN, "20px"));
        this.fullPathField.textProperty()
                .bind(this.dirField
                        .textProperty()
                        .concat(File.separator)
                        .concat(this.nameField.textProperty())
                        .concat("." + Globals.FILE_EXTENSION)
                );

        //the user can drag a directory into the field.
        this.dirField.setOnDragOver(event -> event.acceptTransferModes(TransferMode.MOVE));
        this.dirField.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                List<File> files = dragboard.getFiles();
                File draggedFile = files.get(0);

                if (draggedFile.isDirectory()) {
                    TextField field = (TextField) event.getSource();
                    field.setText(draggedFile.getAbsolutePath());
                }
            }
        });

        //We don't allow to put spaces in the following inputs
        this.nameField.setTextFormatter(new SpaceValidator());
        this.usernameField.setTextFormatter(new SpaceValidator());
        this.passwordField.setTextFormatter(new SpaceValidator());
    }

    private void setBehaviour() {
        this.usernameField.disableProperty().bind(this.authCheckBox.selectedProperty().not());
        this.passwordField.disableProperty().bind(this.authCheckBox.selectedProperty().not());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        setBehaviour();
    }
}
