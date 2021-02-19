package com.dansoftware.boomega.gui.record.show;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.gui.record.show.dock.googlebook.GoogleBookConnectionView;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class RecordsView extends SplitPane {

    private static final String STYLE_CLASS = "records-view";

    private final ObjectProperty<Consumer<List<Record>>> onItemsDeleted = new SimpleObjectProperty<>();

    private final Context context;
    private final Database database;
    private final RecordTable recordTable;

    private final SplitPane dockSplitPane;


    RecordsView(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.recordTable = buildBooksTable();
        this.dockSplitPane = buildDockSplitPane();
        this.getStyleClass().add(STYLE_CLASS);
        this.setOrientation(Orientation.HORIZONTAL);
        this.buildUI();
    }

    private SplitPane buildDockSplitPane() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setPrefWidth(500);
        splitPane.setMaxWidth(500);
        splitPane.getItems().add(buildBookEditorDock(splitPane));
        splitPane.getItems().add(buildGoogleBooksDock(splitPane));
        SplitPane.setResizableWithParent(splitPane, false);
        return splitPane;
    }

    private RecordTable buildBooksTable() {
        return new RecordTable(0);
    }

    private void buildUI() {
        this.getItems().addAll(recordTable, dockSplitPane);
    }

    private Node buildBookEditorDock(SplitPane dockSplitPane) {
        var recordEditor = new RecordEditor(context, database, this.recordTable.getSelectionModel().getSelectedItems());
        recordEditor.setOnItemsDeleted(items -> {
            this.recordTable.getItems().removeAll(items);
            if (onItemsDeleted.get() != null) {
                onItemsDeleted.get().accept(items);
            }
        });
        recordEditor.setOnItemsModified(items -> recordTable.refresh());
        this.recordTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change -> {
            recordEditor.setItems(this.recordTable.getSelectionModel().getSelectedItems());
        });
        return new RecordEditorDock(dockSplitPane, recordEditor);
    }

    private Node buildGoogleBooksDock(SplitPane dockSplitPane) {
        var dockContent = new GoogleBookConnectionView(context, database, recordTable.getSelectionModel().getSelectedItems());
        dockContent.setOnRefreshed(recordTable::refresh);
        this.recordTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change -> {
            dockContent.setItems(this.recordTable.getSelectionModel().getSelectedItems());
        });
        return new GoogleBookDock(dockSplitPane, dockContent);
    }

    public void setDockFullyResizable() {
        dockSplitPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    }

    public void scrollToTop() {
        this.recordTable.scrollTo(0);
    }

    public void setItems(@NotNull List<Record> items) {
        this.recordTable.getItems().setAll(items);
    }

    public void setOnItemsDeleted(Consumer<List<Record>> onItemsDeleted) {
        this.onItemsDeleted.set(onItemsDeleted);
    }

    public void setStartIndex(int startIndex) {
        this.recordTable.startIndexProperty().set(startIndex);
    }

    public RecordTable getBooksTable() {
        return recordTable;
    }

    private static abstract class TitledDock<T extends Node> extends VBox {

        private final T content;

        TitledDock(@NotNull SplitPane parent,
                   @NotNull Node icon,
                   @NotNull String title,
                   @NotNull T content) {
            this.content = content;
            this.buildUI(parent, icon, title);
        }

        private void buildUI(SplitPane parent, Node icon, String title) {
            SplitPane.setResizableWithParent(this, false);
            VBox.setVgrow(content, Priority.ALWAYS);
            getStyleClass().add("titled-dock");
            getChildren().add(buildTitleBar(parent, icon, title));
            getChildren().add(content);
        }

        private Node buildTitleBar(SplitPane parent, Node icon, String title) {
            var buttonBar = new HBox(5.0, buildUpButton(parent), buildDownButton(parent));
            var iconTitleBar = new HBox(10.0, new StackPane(icon), new StackPane(new Label(title)));
            var titleBar = new BorderPane(null, null, buttonBar, null, iconTitleBar);
            titleBar.getStyleClass().add("dock-title-bar");
            return titleBar;
        }

        private Button buildUpButton(SplitPane parent) {
            Button btn = buildRearrangeButton(MaterialDesignIcon.ARROW_UP);
            parent.getItems().addListener((ListChangeListener<? super Node>) modification -> {
                btn.setDisable(parent.getItems().size() < 2 || parent.getItems().indexOf(this) == 0);
            });
            btn.setOnAction(e -> {
                int index = parent.getItems().indexOf(this);
                parent.getItems().remove(this);
                parent.getItems().add(index -1, this);
            });
            return btn;
        }

        private Button buildDownButton(SplitPane parent) {
            Button btn = buildRearrangeButton(MaterialDesignIcon.ARROW_DOWN);
            parent.getItems().addListener((ListChangeListener<? super Node>) modification -> {
                btn.setDisable(parent.getItems().size() < 2 || parent.getItems().indexOf(this) == parent.getItems().size() -1);
            });
            btn.setOnAction(e -> {
                int index = parent.getItems().indexOf(this);
                parent.getItems().remove(this);
                parent.getItems().add(index + 1, this);
            });
            return btn;
        }

        private Button buildRearrangeButton(MaterialDesignIcon icon) {
            var btn = new Button(null, new MaterialDesignIconView(icon));
            btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btn.setPadding(new Insets(0));
            return btn;
        }
    }

    private static final class GoogleBookDock extends TitledDock<GoogleBookConnectionView> {

        private final GoogleBookConnectionView content;

        GoogleBookDock(@NotNull SplitPane parent, @NotNull GoogleBookConnectionView content) {
            super(
                    parent,
                    new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png")),
                    I18N.getValue("google.books.dock.title"),
                    content
            );
            this.content = content;
        }
    }

    private static final class RecordEditorDock extends TitledDock<RecordEditor> {

        RecordEditorDock(@NotNull SplitPane parent,
                         @NotNull RecordEditor content) {
            super(
                    parent,
                    new FontAwesomeIconView(FontAwesomeIcon.EDIT),
                    I18N.getValue("record.editor.dock.title"),
                    content
            );
        }
    }

}
