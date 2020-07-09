package com.dansoftware.libraryapp.gui.entry.login.dbcreator;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.util.SpaceValidator;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.util.FileUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.exceptions.NitriteIOException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.I18N.getFXMLValues;
import static com.dansoftware.libraryapp.locale.I18N.getNotificationMsg;

/**
 * A {@link DatabaseCreatorForm} is gui-form that lets the user to create
 * a new data-source file. It's usually wrapped in a {@link DatabaseCreatorView}
 * object.
 */
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

    private final DatabaseCreatorView parent;

    private Account createdAccount;

    public DatabaseCreatorForm(DatabaseCreatorView parent) {
        this.loadGui();
        this.parent = parent;
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

    public Optional<Account> getCreatedAccount() {
        return Optional.ofNullable(this.createdAccount);
    }

    @FXML
    private void create() {

        File dir = new File(dirField.getText());
        File dbFile = new File(fullPathField.getText());

        // validating the inputs

        if (StringUtils.isBlank(this.nameField.getText())) {
            this.parent.showErrorDialog(
                    getNotificationMsg("db.creator.form.invalid.missing.name.title"),
                    getNotificationMsg("db.creator.form.invalid.missing.name.msg"), buttonType -> {
                        new animatefx.animation.Tada(this.nameField).play();
                    });
            return;
        }

        if (StringUtils.isBlank(this.dirField.getText())) {
            this.parent.showErrorDialog(
                    getNotificationMsg("db.creator.form.invalid.missing.dir.title"),
                    getNotificationMsg("db.creator.form.invalid.missing.dir.msg"), buttonType -> {
                        new animatefx.animation.Tada(this.dirField).play();
                    }
            );
        }

        if (FileUtils.hasNotValidPath(dir)) {
            this.parent.showErrorDialog(
                    getNotificationMsg("db.creator.form.invalid.dir.title"),
                    getNotificationMsg("db.creator.form.invalid.dir.msg", dir), buttonType -> {
                        new animatefx.animation.Tada(this.dirField).play();
                    });
            return;
        }

        if (!dir.exists()) {
            this.parent.showInformationDialog(
                    getNotificationMsg("db.creator.form.confirm.dir.not.exist.title", dir.getName()),
                    getNotificationMsg("db.creator.form.confirm.dir.not.exist.msg"), (buttonType) -> {
                        try {
                            dir.mkdirs();
                        } catch (java.lang.SecurityException e) {
                            //handle
                        }
                    }
            );
        }

        if (dbFile.exists()) {
            this.parent.showErrorDialog(
                    getNotificationMsg("db.creater.form.invalid.file.already.exists.title"),
                    getNotificationMsg("db.creater.form.invalid.file.already.exists.msg",
                            FileUtils.shortenedFilePath(dbFile, 1)),
                    buttonType -> {
                    });
            return;
        }


        String username = null;
        String password = null;
        if (authCheckBox.isSelected()) {
            username = usernameField.getText();
            password = passwordField.getText();

            if (StringUtils.isBlank(username)) {
                this.parent.showErrorDialog(
                        getNotificationMsg("db.creator.form.invalid.empty.user.name.title"),
                        getNotificationMsg("db.creator.form.invalid.empty.user.name.msg"),
                        buttonType -> {
                            new animatefx.animation.Tada(this.usernameField).play();
                        });
                return;
            }
            if (StringUtils.isBlank(password)) {
                this.parent.showErrorDialog(
                        getNotificationMsg("db.creator.form.invalid.empty.password.title"),
                        getNotificationMsg("db.creator.form.invalid.empty.password.msg"),
                        buttonType -> {
                            new animatefx.animation.Tada(this.passwordField).play();
                        });
                return;
            }
        }

        // trying to create the database

        Account account = new Account(dbFile, username, password);

        try {
            // We create the database object (because we want to create the db-file)
            // but we immediately close it
            DatabaseFactory.getDatabase(NITRITE, account).close();

            this.createdAccount = account;
            StageUtils.getStageOf(this).close();
        } catch (NullPointerException | NitriteIOException e) {
            String title = getNotificationMsg("login.auth.failed.io.title");
            String message = getNotificationMsg("login.auth.failed.io.msg");

            this.parent.showErrorDialog(title, message, e, buttonType -> {
            });
        }
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


        //We don't allow to put spaces in the following inputs
        this.nameField.setTextFormatter(new SpaceValidator());
        this.usernameField.setTextFormatter(new SpaceValidator());
        this.passwordField.setTextFormatter(new SpaceValidator());
    }

    private void setBehaviour() {
        this.usernameField.disableProperty().bind(this.authCheckBox.selectedProperty().not());
        this.passwordField.disableProperty().bind(this.authCheckBox.selectedProperty().not());
    }

    private void setDragSupport() {
        /*
        this.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles() && dragboard.getFiles().get(0).isDirectory()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                this.parent.showOverlay(new StackPane(new Rectangle(20, 20)), false);
            }
        });
        this.setOnDragExited(event -> {
            this.parent.showOverlay(null, false);
        });
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
        });*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        setBehaviour();
        setDragSupport();
    }
}
