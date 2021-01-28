package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.dock.position.DockPosition;
import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.window.BaseWindow;
import com.dansoftware.libraryapp.i18n.I18N;
import javafx.collections.ListChangeListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BooksView extends DockSystem<BooksTable> {

    private final Context context;
    private final BooksTable booksTable;

    BooksView(@NotNull Context context) {
        this.context = context;
        this.booksTable = buildBooksTable();
        this.setResourceBundle(I18N.getDockSystemValues());
        this.buildUI();
    }

    private BooksTable buildBooksTable() {
        return new BooksTable(0);
    }

    private void buildUI() {
        this.setDockedCenter(this.booksTable);
        this.buildGoogleBooksDock();
    }

    @SuppressWarnings("rawtypes")
    private void buildGoogleBooksDock() {
        var dock = new BookGoogleInfoDock(context, this, booksTable.getSelectionModel().getSelectedItems());
        this.booksTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Book>) change -> {
            if (dock.isShowing()) dock.setItems(this.booksTable.getSelectionModel().getSelectedItems());
        });
        dock.setDockPosition(DockPosition.RIGHT_BOTTOM);
        dock.setStageFactory(() -> new BaseWindow() {{
            initOwner(context.getContextWindow());
        }});
        //TODO: ON SHOWN -> setting it's content, ON HIDDEN -> clearing it's content
        dock.show();
    }

    public void scrollToTop() {
        this.booksTable.scrollTo(0);
    }

    public void setItems(@NotNull List<Book> items) {
        this.booksTable.getItems().setAll(items);
    }

    public void setStartIndex(int startIndex) {
        this.booksTable.startIndexProperty().set(startIndex);
    }

    public BooksTable getBooksTable() {
        return booksTable;
    }

}
