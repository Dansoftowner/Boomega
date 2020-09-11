package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.db.processor.LoginProcessor;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.util.FurtherFXMLLoader;
import com.dansoftware.libraryapp.gui.util.SpaceValidator;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.main.Globals;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.jfilegoodies.FileGoodies;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    private static final Logger logger = LoggerFactory.getLogger(DatabaseCreatorForm.class);

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

    private final DatabaseTracker databaseTracker;

    private final DatabaseCreatorView parent;

    private final SimpleObjectProperty<DatabaseMeta> createdDatabase = new SimpleObjectProperty<>();

    public DatabaseCreatorForm(@NotNull DatabaseCreatorView parent, @NotNull DatabaseTracker tracker) {
        this.databaseTracker = Objects.requireNonNull(tracker, "DatabaseTracker shouldn't be null");
        this.parent = parent;
        this.loadGui();
    }

    private void loadGui() {
        var fxmlLoader = new FurtherFXMLLoader(this, getClass().getResource("Form.fxml"), getFXMLValues());
        this.getChildren().add(fxmlLoader.load());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        setBehaviour();
        setDragSupport();
    }

    public ReadOnlyObjectProperty<DatabaseMeta> createdDatabaseProperty() {
        return createdDatabase;
    }

    @FXML
    private void create() {

        // validating the inputs
        var validator = this.new Validator();
        validator.ifNameEmpty(
                getAlertMsg("db.creator.form.invalid.missing.name.title"),
                getAlertMsg("db.creator.form.invalid.missing.name.msg"),
                buttonType -> new animatefx.animation.Tada(this.nameField).play()
        ).ifDirEmpty(
                getAlertMsg("db.creator.form.invalid.missing.dir.title"),
                getAlertMsg("db.creator.form.invalid.missing.dir.msg"),
                buttonType -> new animatefx.animation.Tada(this.dirField).play()
        ).ifDirHasNotValidPath(
                getAlertMsg("db.creator.form.invalid.dir.title"),
                getAlertMsg("db.creator.form.invalid.dir.msg", validator.dirFile),
                buttonType -> new animatefx.animation.Tada(this.dirField).play()
        ).ifDbFileExists(
                getAlertMsg("db.creater.form.invalid.file.already.exists.title"),
                getAlertMsg("db.creater.form.invalid.file.already.exists.msg",
                        FileGoodies.shortenedFilePath(validator.dbFile, 1)),
                buttonType -> {
                }
        ).ifUsernameEmpty(
                getAlertMsg("db.creator.form.invalid.empty.user.name.title"),
                getAlertMsg("db.creator.form.invalid.empty.user.name.msg"),
                buttonType -> new animatefx.animation.Tada(this.usernameField).play()
        ).ifPasswordEmpty(
                getAlertMsg("db.creator.form.invalid.empty.password.title"),
                getAlertMsg("db.creator.form.invalid.empty.password.msg"),
                buttonType -> new animatefx.animation.Tada(this.passwordField).play()
        ).ifDirFileNotExists(
                getAlertMsg("db.creator.form.confirm.dir.not.exist.title", validator.dirFile.getName()),
                getAlertMsg("db.creator.form.confirm.dir.not.exist.msg"), (buttonType) -> {
                    try {
                        validator.dirFile.mkdirs();
                    } catch (java.lang.SecurityException e) {
                        //handle
                    }
                }
        ).onSuccess((createdDatabase, credentials) -> {
            this.createdDatabase.set(createdDatabase);
            LoginProcessor.of(NitriteDatabase.factory())
                    .onFailed((title, message, t) -> {
                        this.createdDatabase.set(null);
                        this.parent.showErrorDialog(title, message, (Exception) t, buttonType -> {
                        });
                    }).touch(createdDatabase, credentials);
            databaseTracker.addDatabase(createdDatabase);
            WindowUtils.getStageOptionalOf(this).ifPresent(Stage::close);
        });
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
        this.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean filesDetected = dragboard.hasFiles();
            boolean onlyOneFile = dragboard.getFiles().size() == 1;
            boolean isDirectory = dragboard.getFiles().get(0).isDirectory();

            event.acceptTransferModes((filesDetected && onlyOneFile && isDirectory) ?
                    TransferMode.COPY_OR_MOVE : TransferMode.NONE);
        });

        this.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            List<File> files = dragboard.getFiles();
            File draggedFile = files.get(0);
            boolean filesDetected = dragboard.hasFiles();
            boolean onlyOneFile = files.size() == 1;
            boolean isDirectory = draggedFile.isDirectory();

            if (filesDetected && onlyOneFile && isDirectory)
                dirField.setText(draggedFile.getAbsolutePath());
        });
    }

    //TODO: validator problems fix (file-path not valid)

    /**
     * A {@link Validator} is used for validating the inputs in the form.
     *
     * <p>
     * It gives a modern, functional way for handling the validation-problems and showing a dialog for the user.
     * <br>
     * Example:
     * <pre>{@code
     * new Validator()
     *      .ifNameEmpty(
     *          "Database name undefined",
     *          "You should specify the database name",
     *          buttonType -> { //action when the user clicks the "OK" button in the dialog }
     *       ).onSuccess((databaseMeta, credentials) -> { //the database is created} );
     * }</pre>
     */
    private class Validator {

        private WorkbenchDialog lastDialog;
        private boolean dialogShowing;

        private final boolean dirIsEmpty;
        private final boolean nameIsEmpty;
        private final boolean dirIsNotValid;
        private final boolean dirFileNotExists;
        private final boolean dbFileExists;
        private final boolean usernameEmpty;
        private final boolean passwordEmpty;

        private final File dirFile;
        private final File dbFile;

        Validator() {
            dirFile = new File(dirField.getText());
            dbFile = new File(fullPathField.getText());

            //calculating the criteria(s)
            dirIsEmpty = StringUtils.isBlank(dirField.getText());
            nameIsEmpty = StringUtils.isBlank(nameField.getText());
            dirIsNotValid = FileGoodies.hasNotValidPath(dirFile);
            dirFileNotExists = !dirFile.exists();
            dbFileExists = dbFile.exists();
            usernameEmpty = authCheckBox.isSelected() && StringUtils.isBlank(usernameField.getText());
            passwordEmpty = authCheckBox.isSelected() && StringUtils.isBlank(passwordField.getText());
        }

        /**
         * Shows an error dialog and updates the necessary fields
         *
         * @param dialogTitle the title of the dialog
         * @param dialogMsg   the message of the dialog
         * @param onResult    the event-handler that runs when the user clicks on the dialog-btn
         * @param criteria    the criteria for showing a dialog; {@code true} if it should be shown
         */
        private void showErrorDialog(@NotNull String dialogTitle,
                                     @NotNull String dialogMsg,
                                     @NotNull Consumer<ButtonType> onResult,
                                     boolean criteria) {
            if (!dialogShowing && criteria) {
                this.lastDialog = DatabaseCreatorForm.this.parent.showErrorDialog(dialogTitle, dialogMsg, buttonType -> {
                    onResult.accept(buttonType);
                    Validator.this.dialogShowing = false;
                });
                Validator.this.dialogShowing = true;
            }
        }

        /**
         * Shows an info dialog and updates the necessary fields
         *
         * @param dialogTitle the title of the dialog
         * @param dialogMsg   the message of the dialog
         * @param onResult    the event-handler that runs when the user clicks on the dialog-btn
         * @param criteria    the criteria for showing a dialog; {@code true} if it should be shown
         */
        private void showInformationDialog(@NotNull String dialogTitle,
                                           @NotNull String dialogMsg,
                                           @NotNull Consumer<ButtonType> onResult,
                                           boolean criteria) {
            if (!dialogShowing && criteria) {
                this.lastDialog = DatabaseCreatorForm.this.parent.showInformationDialog(dialogTitle, dialogMsg, buttonType -> {
                    onResult.accept(buttonType);
                    Validator.this.dialogShowing = false;
                });
                Validator.this.dialogShowing = true;
            }
        }

        Validator ifNameEmpty(@NotNull String dialogTitle,
                              @NotNull String dialogMsg,
                              @NotNull Consumer<ButtonType> onResult) {
            showErrorDialog(dialogTitle, dialogMsg, onResult, nameIsEmpty);
            return this;
        }

        Validator ifDirEmpty(@NotNull String dialogTitle,
                             @NotNull String dialogMsg,
                             @NotNull Consumer<ButtonType> onResult) {
            showErrorDialog(dialogTitle, dialogMsg, onResult, dirIsEmpty);
            return this;
        }

        Validator ifDirHasNotValidPath(@NotNull String dialogTitle,
                                       @NotNull String dialogMsg,
                                       @NotNull Consumer<ButtonType> onResult) {
            showErrorDialog(dialogTitle, dialogMsg, onResult, dirIsNotValid);
            return this;
        }

        Validator ifDbFileExists(@NotNull String dialogTitle,
                                 @NotNull String dialogMsg,
                                 @NotNull Consumer<ButtonType> onResult) {
            showErrorDialog(dialogTitle, dialogMsg, onResult, dbFileExists);
            return this;
        }

        Validator ifUsernameEmpty(@NotNull String dialogTitle,
                                  @NotNull String dialogMsg,
                                  @NotNull Consumer<ButtonType> onResult) {
            showErrorDialog(dialogTitle, dialogMsg, onResult, usernameEmpty);
            return this;
        }

        Validator ifPasswordEmpty(@NotNull String dialogTitle,
                                  @NotNull String dialogMsg,
                                  @NotNull Consumer<ButtonType> onResult) {
            showErrorDialog(dialogTitle, dialogMsg, onResult, passwordEmpty);
            return this;
        }

        Validator ifDirFileNotExists(@NotNull String dialogTitle,
                                     @NotNull String dialogMsg,
                                     @NotNull Consumer<ButtonType> onResult) {
            showInformationDialog(dialogTitle, dialogMsg, onResult, dirFileNotExists);
            return this;
        }

        void onSuccess(@NotNull BiConsumer<DatabaseMeta, Credentials> action) {
            Runnable actionWrapped = () -> action.accept(
                    new DatabaseMeta(nameField.getText(), dbFile),
                    new Credentials(usernameField.getText(), passwordField.getText())
            );

            if (dialogShowing && lastDialog.getType() != WorkbenchDialog.Type.ERROR)
                lastDialog.setOnHidden(event -> actionWrapped.run());
            else if (!dialogShowing)
                actionWrapped.run();
        }
    }
}
