package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.gui.record.googlebook.GoogleBookConnectionView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;

public enum Dock {

    GOOGLE_BOOK_CONNECTION("google.books.dock.title") {
        @Override
        public void align(Context context,
                          Database database,
                          TableView<Record> table,
                          SplitPane vertical,
                          SplitPane horizontal) {
            if (horizontal.getItems().stream().noneMatch(GoogleBookConnectionDock.class::isInstance)) {
                var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
                dockContent.setOnRefreshed(table::refresh);
                table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                        dockContent.setItems(table.getSelectionModel().getSelectedItems()));
                GoogleBookConnectionDock dock = new GoogleBookConnectionDock(horizontal, dockContent);
                dock.setPrefWidth(RIGHT_DOCK_PANE_PREF_WIDTH);
                horizontal.getItems().add(dock);
            }
        }

        @Override
        public void remove(SplitPane vertical, SplitPane horizontal) {
            horizontal.getItems().removeIf(GoogleBookConnectionDock.class::isInstance);
        }

        @Override
        public Node getGraphic() {
            return new MaterialDesignIconView(MaterialDesignIcon.GOOGLE);
        }
    },

    RECORD_EDITOR("record.editor.dock.title") {
        @Override
        public void align(Context context,
                          Database database,
                          TableView<Record> table,
                          SplitPane vertical,
                          SplitPane horizontal) {
            if (horizontal.getItems().stream().noneMatch(RecordEditorDock.class::isInstance)) {
                var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
                recordEditor.setOnItemsModified(items -> table.refresh());
                table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                        recordEditor.setItems(table.getSelectionModel().getSelectedItems()));
                RecordEditorDock dock = new RecordEditorDock(horizontal, recordEditor);
                dock.setPrefHeight(RECORD_EDITOR_PREF_HEIGHT);
                horizontal.getItems().add(0, dock);
            }
        }

        @Override
        public void remove(SplitPane vertical, SplitPane horizontal) {
            horizontal.getItems().removeIf(RecordEditorDock.class::isInstance);
        }

        @Override
        public Node getGraphic() {
            return new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        }
    };

    private static final double RECORD_EDITOR_PREF_HEIGHT = 400;
    private static final double RIGHT_DOCK_PANE_PREF_WIDTH = 500;

    private final String i18n;

    Dock(String i18n) {
        this.i18n = i18n;
    }

    public String getI18nKey() {
        return i18n;
    }

    public abstract void align(
            Context context,
            Database database,
            TableView<Record> table,
            SplitPane vertical,
            SplitPane horizontal);

    public abstract void remove(SplitPane vertical, SplitPane horizontal);

    public abstract Node getGraphic();
}
