package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.gui.record.show.RecordTable;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

public class RecordEditorDock extends DockView<RecordEditor> {
    public RecordEditorDock(@NotNull Context context,
                            @NotNull Database database,
                            @NotNull SplitPane parent,
                            @NotNull RecordTable table) {
        super(
                parent,
                new FontAwesomeIconView(FontAwesomeIcon.EDIT),
                I18N.getValue("record.editor.dock.title"),
                buildContent(context, database, table)
        );
    }

    private static RecordEditor buildContent(Context context,
                                             Database database,
                                             RecordTable table) {
        var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
        recordEditor.setOnItemsModified(items -> table.refresh());
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                recordEditor.setItems(table.getSelectionModel().getSelectedItems()));
        return recordEditor;
    }
}
