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
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
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

    private Dock[] docks;

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
        SplitPane.setResizableWithParent(splitPane, true);
        return splitPane;
    }

    private SplitPane buildRightSplitPane() {
        SplitPane splitPane = buildDockSplitPane();
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

    public void setDockInfo(DockInfo dockInfo) {
        setDocks(dockInfo.docks);
        setRecordTablePos(dockInfo.recordTablePos);
    }

    public DockInfo getDockInfo() {
        return new DockInfo(docks, getRecordTablePos());
    }

    public int getRecordTablePos() {
        return leftSplitPane.getItems().indexOf(recordTable);
    }

    public void setRecordTablePos(int pos) {
        leftSplitPane.getItems().add(pos, recordTable);
    }

    public void setDocks(Dock[] docks) {
        this.docks = docks;
        for (Dock dock : docks)
            dock.align(context, database, recordTable, leftSplitPane, rightSplitPane);
    }

    public Dock[] getDocks() {
        return this.docks;
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

    public static class DockInfo {
        private Dock[] docks;
        private int recordTablePos;

        public DockInfo(Dock[] docks, int recordTablePos) {
            this.docks = docks;
            this.recordTablePos = recordTablePos;
        }

        public static DockInfo defaultInfo() {
            return new DockInfo(Dock.values(), 0);
        }
    }

    public enum Dock {

        GOOGLE_BOOK_CONNECTION() {
            @Override
            protected void align(Context context,
                                 Database database,
                                 TableView<Record> table,
                                 SplitPane leftSplitPane,
                                 SplitPane rightSplitPane) {

                var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
                dockContent.setOnRefreshed(table::refresh);
                table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                        dockContent.setItems(table.getSelectionModel().getSelectedItems()));
                rightSplitPane.getItems().add(new GoogleBookConnectionDock(rightSplitPane, dockContent));
            }
        },

        RECORD_EDITOR() {
            @Override
            protected void align(Context context,
                                 Database database,
                                 TableView<Record> table,
                                 SplitPane leftSplitPane,
                                 SplitPane rightSplitPane) {

                var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
                recordEditor.setOnItemsModified(items -> table.refresh());
                table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                        recordEditor.setItems(table.getSelectionModel().getSelectedItems()));
                leftSplitPane.getItems().add(new RecordEditorDock(leftSplitPane, recordEditor));
            }
        };

        protected abstract void align(
                Context context,
                Database database,
                TableView<Record> table,
                SplitPane leftSplitPane,
                SplitPane rightSplitPane);
    }
}
