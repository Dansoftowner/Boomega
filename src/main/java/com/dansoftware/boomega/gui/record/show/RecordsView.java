package com.dansoftware.boomega.gui.record.show;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.gui.record.googlebook.GoogleBookConnectionView;
import com.dansoftware.boomega.gui.record.show.dock.GoogleBookConnectionDock;
import com.dansoftware.boomega.gui.record.show.dock.RecordEditorDock;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class RecordsView extends SplitPane {

    private static final Logger logger = LoggerFactory.getLogger(RecordsView.class);

    private static final String STYLE_CLASS = "records-view";

    private final ObjectProperty<Consumer<List<Record>>> onItemsDeleted = new SimpleObjectProperty<>();

    private final Context context;
    private final Database database;
    private final RecordTable recordTable;

    private final SplitPane leftSplitPane;
    private final SplitPane rightSplitPane;

    RecordsView(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.recordTable = buildBooksTable();
        this.rightSplitPane = buildRightSplitPane();
        this.leftSplitPane = buildLeftSplitPane();
        this.getStyleClass().add(STYLE_CLASS);
        this.setOrientation(Orientation.HORIZONTAL);
        this.buildUI();
    }

    private SplitPane buildLeftSplitPane() {
        SplitPane splitPane = buildDockSplitPane();
        splitPane.getItems().add(recordTable);
        splitPane.getItems().add(buildBookEditorDock(splitPane));
        SplitPane.setResizableWithParent(splitPane, true);
        return splitPane;
    }

    private SplitPane buildRightSplitPane() {
        SplitPane splitPane = buildDockSplitPane();
        splitPane.getItems().add(buildGoogleBooksDock(splitPane));
        splitPane.setPrefWidth(500);
        splitPane.setMaxWidth(500);
        SplitPane.setResizableWithParent(splitPane, false);
        return splitPane;
    }

    private SplitPane buildDockSplitPane() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        return splitPane;
    }

    private RecordTable buildBooksTable() {
        return new RecordTable(0);
    }

    private void buildUI() {
        this.getItems().addAll(leftSplitPane, rightSplitPane);
    }

    private Node buildBookEditorDock(SplitPane dockSplitPane) {
        var recordEditor = new RecordEditor(context, database, this.recordTable.getSelectionModel().getSelectedItems());
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
        return new GoogleBookConnectionDock(dockSplitPane, dockContent);
    }

    public void setDockFullyResizable() {
        rightSplitPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
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

}
