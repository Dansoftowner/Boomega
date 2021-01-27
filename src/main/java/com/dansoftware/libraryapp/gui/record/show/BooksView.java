package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.dock.position.DockPosition;
import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BooksView extends DockSystem<BooksTable> {

    private final Context context;
    private final BooksTable booksTable;

    BooksView(@NotNull Context context) {
        this.context = context;
        this.booksTable = new BooksTable(0);
        this.buildUI();
    }

    private void buildUI() {
        this.setDockedCenter(this.booksTable);
        this.buildGoogleBooksDock();
    }

    private void buildGoogleBooksDock() {
        var dock = new GoogleBookDockNode(
                this,
                context,
                booksTable.getSelectionModel().getSelectedItems(),
                (book, volume) -> {
                    //TODO: JOIN
                }, (book, volume) -> {

        }, book -> null, book -> book.getServiceConnection().getGoogleBookLink());
        dock.setDockPosition(DockPosition.RIGHT_BOTTOM);
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

    private static final class GoogleBookDockNode
            extends com.dansoftware.libraryapp.gui.googlebooks.dock.GoogleBookDockNode<Book> {

        public GoogleBookDockNode(@NotNull DockSystem<?> dockSystem,
                                  @NotNull Context context,
                                  @NotNull ObservableList<Book> items,
                                  @NotNull BiConsumer<Book, Volume> joinAction, @NotNull BiConsumer<Book, Volume> removeAction, @NotNull Function<Book, SearchParameters> searchParametersSupplier, @NotNull Function<Book, String> volumeHandleRetriever) {
            super(dockSystem, context, items, joinAction, removeAction, searchParametersSupplier, volumeHandleRetriever);
        }
    }
}
