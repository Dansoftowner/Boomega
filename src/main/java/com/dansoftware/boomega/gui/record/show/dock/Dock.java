package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.gui.record.googlebook.GoogleBookConnectionView;
import com.dansoftware.boomega.gui.record.show.RecordTable;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import org.jetbrains.annotations.NotNull;

public enum Dock {

    RECORD_EDITOR("record.editor.dock.title") {
        @Override
        public void align(@NotNull Context context,
                          @NotNull Database database,
                          @NotNull RecordTable table,
                          @NotNull SplitPane splitPane) {
            if (splitPane.getItems().stream().noneMatch(RecordEditorDock.class::isInstance)) {
                var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
                recordEditor.setOnItemsModified(items -> table.refresh());
                table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                        recordEditor.setItems(table.getSelectionModel().getSelectedItems()));
                RecordEditorDock dock = new RecordEditorDock(splitPane, recordEditor);
                splitPane.getItems().add(dock);
            }
        }

        @Override
        public void removeFrom(@NotNull SplitPane splitPane) {
            splitPane.getItems().removeIf(RecordEditorDock.class::isInstance);
        }

        @Override
        public Node getGraphic() {
            return new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        }
    },

    GOOGLE_BOOK_CONNECTION("google.books.dock.title") {
        @Override
        public void align(@NotNull Context context,
                          @NotNull Database database,
                          @NotNull RecordTable table,
                          @NotNull SplitPane splitPane) {
            if (splitPane.getItems().stream().noneMatch(GoogleBookConnectionDock.class::isInstance)) {
                var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
                dockContent.setOnRefreshed(table::refresh);
                table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                        dockContent.setItems(table.getSelectionModel().getSelectedItems()));
                GoogleBookConnectionDock dock = new GoogleBookConnectionDock(splitPane, dockContent);
                splitPane.getItems().add(dock);
            }
        }

        @Override
        public void removeFrom(@NotNull SplitPane splitPane) {
            splitPane.getItems().removeIf(GoogleBookConnectionDock.class::isInstance);
        }

        @Override
        public Node getGraphic() {
            return new MaterialDesignIconView(MaterialDesignIcon.GOOGLE);
        }
    };

    private final String i18n;

    Dock(String i18n) {
        this.i18n = i18n;
    }

    public String getI18nKey() {
        return i18n;
    }

    /**
     * Adds the particular {@link DockView} to the given {@link SplitPane}.
     *
     * @param context the gui-context
     * @param database the database reference
     * @param table the record-table
     * @param splitPane the split pane
     */
    public abstract void align(
            @NotNull Context context,
            @NotNull Database database,
            @NotNull RecordTable table,
            @NotNull SplitPane splitPane
    );

    /**
     * Removes the dock from the split pane.
     *
     * @param splitPane the {@link SplitPane} reference
     */
    public abstract void removeFrom(@NotNull SplitPane splitPane);

    /**
     * @return the icon of the dock
     */
    public abstract Node getGraphic();
}
