package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.gui.util.SpaceValidator;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.main.Globals;
import com.jfilegoodies.FileGoodies;
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
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.exceptions.NitriteIOException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.locale.I18N.getAlertMsg;
import static com.dansoftware.libraryapp.locale.I18N.getFXMLValues;

/**
 * A {@link DatabaseCreatorForm} is gui-form that lets the user to create
 * a new data-source file. It's usually wrapped in a {@link DatabaseCreatorView}
 * object.
 *
 * @author Daniel Gyorffy
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

    private DatabaseMeta createdDb;

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

    public Optional<DatabaseMeta> getCreatedDb() {
        return Optional.ofNullable(this.createdDb);
    }

    @FXML
    private void create() {

        // validating the inputs
        if (StringUtils.isBlank(nameField.getText())) {
            this.parent.showErrorDialog(
                    getAlertMsg("db.creator.form.invalid.missing.name.title"),
                    getAlertMsg("db.creator.form.invalid.missing.name.msg"), buttonType -> {
                        new animatefx.animation.Tada(this.nameField).play();
                    });
            return;
        }

        if (StringUtils.isBlank(dirField.getText())) {
            this.parent.showErrorDialog(
                    getAlertMsg("db.creator.form.invalid.missing.dir.title"),
                    getAlertMsg("db.creator.form.invalid.missing.dir.msg"), buttonType -> {
                        new animatefx.animation.Tada(this.dirField).play();
                    }
            );
            return;
        }

        File dirFile = new File(dirField.getText());
        File dbFile = new File(fullPathField.getText());

        if (FileGoodies.hasNotValidPath(dirFile)) {
            this.parent.showErrorDialog(
                    getAlertMsg("db.creator.form.invalid.dir.title"),
                    getAlertMsg("db.creator.form.invalid.dir.msg", dirFile), buttonType -> {
                        new animatefx.animation.Tada(this.dirField).play();
                    });
            return;
        }

        if (!dirFile.exists()) {
            this.parent.showInformationDialog(
                    getAlertMsg("db.creator.form.confirm.dir.not.exist.title", dirFile.getName()),
                    getAlertMsg("db.creator.form.confirm.dir.not.exist.msg"), (buttonType) -> {
                        try {
                            dirFile.mkdirs();
                        } catch (java.lang.SecurityException e) {
                            //handle
                        }
                    }
            );
        }

        if (dbFile.exists()) {
            this.parent.showErrorDialog(
                    getAlertMsg("db.creater.form.invalid.file.already.exists.title"),
                    getAlertMsg("db.creater.form.invalid.file.already.exists.msg",
                            FileGoodies.shortenedFilePath(dbFile, 1)),
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
                        getAlertMsg("db.creator.form.invalid.empty.user.name.title"),
                        getAlertMsg("db.creator.form.invalid.empty.user.name.msg"),
                        buttonType -> {
                            new animatefx.animation.Tada(this.usernameField).play();
                        });
                return;
            }
            if (StringUtils.isBlank(password)) {
                this.parent.showErrorDialog(
                        getAlertMsg("db.creator.form.invalid.empty.password.title"),
                        getAlertMsg("db.creator.form.invalid.empty.password.msg"),
                        buttonType -> {
                            new animatefx.animation.Tada(this.passwordField).play();
                        });
                return;
            }
        }

        // trying to create the database
        try {
            this.createdDb = new DatabaseMeta(this.nameField.getText(), dbFile);
            // We create the database object (because we want to create the db-file)
            // but we immediately close it
            new NitriteDatabase(createdDb, new Credentials(username, password)).close();

            WindowUtils.getStageOptionalOf(this).ifPresent(Stage::close);
        } catch (NullPointerException | NitriteIOException e) {
            this.createdDb = null;

            String title = getAlertMsg("login.auth.failed.io.title");
            String message = getAlertMsg("login.auth.failed.io.msg");

            this.parent.showErrorDialog(title, message, e, buttonType -> {
            });
        }
    }

    @FXML
    private void openDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(WindowUtils.getWindowOf(this));

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
                        .concat(".")
                        .concat(Globals.FILE_EXTENSION)
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
