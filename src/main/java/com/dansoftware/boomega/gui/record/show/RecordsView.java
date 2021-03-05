package com.dansoftware.boomega.gui.record.show;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.gui.record.googlebook.GoogleBookConnectionView;
import com.dansoftware.boomega.gui.record.show.dock.GoogleBookConnectionDock;
import com.dansoftware.boomega.gui.record.show.dock.RecordEditorDock;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RecordsView extends SplitPane {

    private static final Logger logger = LoggerFactory.getLogger(RecordsView.class);

    private static final String STYLE_CLASS = "records-view";

    private static final double RECORD_EDITOR_PREF_HEIGHT = 400;
    private static final double RIGHT_DOCK_PANE_PREF_WIDTH = 500;

    private final Context context;
    private final Database database;
    private final RecordTable recordTable;

    private final SplitPane leftSplitPane;
    private final SplitPane rightSplitPane;

    private final ObservableList<Dock> docks;

    private final ObjectProperty<Consumer<List<Record>>> onItemsDeleted = new SimpleObjectProperty<>();

    RecordsView(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.recordTable = buildBooksTable();
        this.rightSplitPane = buildRightSplitPane();
        this.leftSplitPane = buildLeftSplitPane();
        this.docks = buildDocksList();
        this.getStyleClass().add(STYLE_CLASS);
        this.setOrientation(Orientation.HORIZONTAL);
        this.buildUI();
    }

    private ObservableList<Dock> buildDocksList() {
        ObservableList<Dock> docks = FXCollections.observableArrayList();
        docks.addListener((ListChangeListener<Dock>) change -> {
            while (change.next()) {
                change.getRemoved().forEach(dock -> dock.remove(leftSplitPane, rightSplitPane));
                change.getAddedSubList().forEach(dock -> dock.align(context, database, recordTable, leftSplitPane, rightSplitPane));
            }

            if (rightSplitPane.getItems().isEmpty()) this.getItems().remove(rightSplitPane);
            else if (!this.getItems().contains(rightSplitPane)) {
                this.getItems().add(rightSplitPane);
                rightSplitPane.setPrefWidth(RIGHT_DOCK_PANE_PREF_WIDTH);
            }
        });
        return docks;
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
        this.getItems().add(leftSplitPane);
    }

    public void setDockInfo(DockInfo dockInfo) {
        getDocks().setAll(dockInfo.docks);
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

    public ObservableList<Dock> getDocks() {
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
        private final List<Dock> docks;
        private final int recordTablePos;

        public DockInfo(List<Dock> docks, int recordTablePos) {
            this.docks = docks;
            this.recordTablePos = recordTablePos;
        }

        public static DockInfo defaultInfo() {
            return new DockInfo(Arrays.asList(Dock.values()), 0);
        }
    }

    public enum Dock {

        GOOGLE_BOOK_CONNECTION("google.books.dock.title") {
            @Override
            protected void align(Context context,
                                 Database database,
                                 TableView<Record> table,
                                 SplitPane leftSplitPane,
                                 SplitPane rightSplitPane) {
                if (rightSplitPane.getItems().stream().noneMatch(GoogleBookConnectionDock.class::isInstance)) {
                    var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
                    dockContent.setOnRefreshed(table::refresh);
                    table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                            dockContent.setItems(table.getSelectionModel().getSelectedItems()));
                    GoogleBookConnectionDock dock = new GoogleBookConnectionDock(rightSplitPane, dockContent);
                    dock.setPrefWidth(RIGHT_DOCK_PANE_PREF_WIDTH);
                    rightSplitPane.getItems().add(dock);
                }
            }

            @Override
            protected void remove(SplitPane leftSplitPane, SplitPane rightSplitPane) {
                rightSplitPane.getItems().removeIf(GoogleBookConnectionDock.class::isInstance);
            }

            @Override
            protected Node getGraphic() {
                return new MaterialDesignIconView(MaterialDesignIcon.GOOGLE);
            }
        },

        RECORD_EDITOR("record.editor.dock.title") {
            @Override
            protected void align(Context context,
                                 Database database,
                                 TableView<Record> table,
                                 SplitPane leftSplitPane,
                                 SplitPane rightSplitPane) {
                if (leftSplitPane.getItems().stream().noneMatch(RecordEditorDock.class::isInstance)) {
                    var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
                    recordEditor.setOnItemsModified(items -> table.refresh());
                    table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                            recordEditor.setItems(table.getSelectionModel().getSelectedItems()));
                    RecordEditorDock dock = new RecordEditorDock(leftSplitPane, recordEditor);
                    dock.setPrefHeight(RECORD_EDITOR_PREF_HEIGHT);
                    leftSplitPane.getItems().add(dock);
                }
            }

            @Override
            protected void remove(SplitPane leftSplitPane, SplitPane rightSplitPane) {
                leftSplitPane.getItems().removeIf(RecordEditorDock.class::isInstance);
            }

            @Override
            protected Node getGraphic() {
                return new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            }
        };

        private final String i18n;

        Dock(String i18n) {
            this.i18n = i18n;
        }

        public String getI18nKey() {
            return i18n;
        }

        protected abstract void align(
                Context context,
                Database database,
                TableView<Record> table,
                SplitPane leftSplitPane,
                SplitPane rightSplitPane);

        protected abstract void remove(SplitPane leftSplitPane,
                                       SplitPane rightSplitPane);

        protected abstract Node getGraphic();
    }
}
