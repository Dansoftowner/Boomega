package com.dansoftware.boomega.gui.record.add;

import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.db.data.ServiceConnection;
import com.dansoftware.boomega.googlebooks.GoogleBooksQueryBuilder;
import com.dansoftware.boomega.googlebooks.Volume;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.googlebooks.SearchParameters;
import com.dansoftware.boomega.gui.googlebooks.join.GoogleBookJoinerOverlay;
import com.dansoftware.boomega.gui.googlebooks.tile.GoogleBookTile;
import com.dansoftware.boomega.gui.record.RecordValues;
import com.dansoftware.boomega.gui.util.LanguageSelections;
import com.dansoftware.boomega.i18n.I18N;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link RecordAddView} provides a UI for typing Book/Magazine data.
 *
 * <p>
 * It has a {@link Record.Type} property that represents what type of database record
 * typed.
 *
 * <p>
 * The {@link #setOnRecordAdded(Consumer)} method can be used for listening to commits
 *
 * @author Daniel Gyorffy
 */
public class RecordAddView extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(RecordAddView.class);

    private static final String STYLE_CLASS = "record-add-view";

    private final ObjectProperty<Consumer<Record>> onRecordAdded =
            new SimpleObjectProperty<>();

    private final RecordAddForm form;
    private final FormNotesEditor notesEditor;

    public RecordAddView(@NotNull Context context, @NotNull Record.Type initialType) {
        this.form = new RecordAddForm(context, initialType);
        this.notesEditor = buildNotesControl();
        this.getStyleClass().add(STYLE_CLASS);
        this.buildBaseUI();
    }

    public void setValues(RecordValues values) {
        form.setValues(values);
    }

    private void buildBaseUI() {
        this.getChildren().add(buildScrollPane());
        this.getChildren().add(buildCommitButton());
    }

    private ScrollPane buildScrollPane() {
        var scrollPane = new ScrollPane();
        scrollPane.setContent(buildSplitPane());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }

    private SplitPane buildSplitPane() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().add(form);
        splitPane.getItems().add(notesEditor);
        SplitPane.setResizableWithParent(notesEditor, false);
        return splitPane;
    }

    private Node buildCommitButton() {
        var button = new Button(
                I18N.getValue("record.add.form.commit"),
                new MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE));
        button.getStyleClass().add("record-add-button");
        button.setDefaultButton(true);
        button.disableProperty().bind(form.validProperty().not());
        //setting the mouse event handler instead of action event handler because of "html editor->enter bug"
        button.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                commitRecord();
            }
        });
        button.prefWidthProperty().bind(this.widthProperty());
        button.addEventFilter(KeyEvent.KEY_TYPED, Event::consume);
        return button;
    }

    private FormNotesEditor buildNotesControl() {
        var notesEditor = new FormNotesEditor();
        notesEditor.setPrefHeight(200);
        VBox.setMargin(notesEditor, new Insets(10, 40, 10, 40));
        return notesEditor;
    }

    private void commitRecord() {
        Consumer<Record> addAction = onRecordAdded.get();
        if (addAction != null) {
            addAction.accept(buildRecordObject());
            clearForm();
        }
    }

    public void clearForm() {
        form.clearForm();
        notesEditor.setHtmlText("");
    }

    private Record buildRecordObject() {
        return new Record.Builder(form.getRecordType())
                .title(form.getTitle())
                .subtitle(form.getSubtitle())
                .publishedDate(form.getPublishedDate())
                .publisher(form.getPublisher())
                .magazineName(form.getMagazineName())
                .authors(List.of(form.getAuthors().split(",")))
                .language(form.getLanguage())
                .isbn(form.getIsbn())
                .subject(form.getSubject())
                .notes(notesEditor.getHtmlText())
                .numberOfCopies(form.getNumberOfCopies())
                .rating(form.getRating())
                .serviceConnection(new ServiceConnection().put(ServiceConnection.GOOGLE_BOOK_HANDLE, form.getGoogleBookHandle()))
                .build();
    }

    public Consumer<Record> getOnRecordAdded() {
        return onRecordAdded.get();
    }

    public ObjectProperty<Consumer<Record>> onRecordAddedProperty() {
        return onRecordAdded;
    }

    public void setOnRecordAdded(Consumer<Record> onRecordAdded) {
        this.onRecordAdded.set(onRecordAdded);
    }

    public Record.Type getRecordType() {
        return form.recordTypeProperty().get();
    }

    public void setRecordType(Record.Type type) {
        form.recordTypeProperty().set(type);
    }

}
