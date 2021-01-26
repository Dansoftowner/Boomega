package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.libraryapp.db.data.Book;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BooksView extends DockSystem<BooksTable> {

    private final BooksTable booksTable;

    BooksView() {
        this.booksTable = new BooksTable(0);
        this.buildUI();
    }

    private void buildUI() {
        this.setDockedCenter(this.booksTable);
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
