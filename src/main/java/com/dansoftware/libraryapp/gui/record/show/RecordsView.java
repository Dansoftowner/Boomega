package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.dock.position.DockPosition;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.data.Record;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.dock.GoogleBookDockNode;
import com.dansoftware.libraryapp.gui.window.BaseWindow;
import com.dansoftware.libraryapp.i18n.I18N;
import javafx.collections.ListChangeListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecordsView extends DockSystem<RecordTable> {

    private final Context context;
    private final Database database;
    private final RecordTable recordTable;

    RecordsView(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.recordTable = buildBooksTable();
        this.setResourceBundle(I18N.getDockSystemValues());
        this.buildUI();
    }

    private RecordTable buildBooksTable() {
        return new RecordTable(0);
    }

    private void buildUI() {
        this.setDockedCenter(this.recordTable);
        this.buildGoogleBooksDock();
    }

    @SuppressWarnings("rawtypes")
    private void buildGoogleBooksDock() {
        var dock = new GoogleBookDockNode(this, context, database, recordTable.getSelectionModel().getSelectedItems());
        this.recordTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change -> {
            if (dock.isShowing()) dock.setItems(this.recordTable.getSelectionModel().getSelectedItems());
        });
        dock.setDockPosition(DockPosition.RIGHT_BOTTOM);
        dock.setStageFactory(() -> new BaseWindow() {{
            initOwner(context.getContextWindow());
        }});
        //TODO: ON SHOWN -> setting it's content, ON HIDDEN -> clearing it's content
        dock.show();
    }

    public void scrollToTop() {
        this.recordTable.scrollTo(0);
    }

    public void setItems(@NotNull List<Record> items) {
        this.recordTable.getItems().setAll(items);
    }

    public void setStartIndex(int startIndex) {
        this.recordTable.startIndexProperty().set(startIndex);
    }

    public RecordTable getBooksTable() {
        return recordTable;
    }

}
