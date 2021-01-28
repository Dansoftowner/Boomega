package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.db.data.ServiceConnection;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import com.dansoftware.libraryapp.gui.googlebooks.dock.GoogleBookDockNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BookGoogleInfoDock extends GoogleBookDockNode<Book> {

    public BookGoogleInfoDock(@NotNull Context context,
                              @NotNull BooksView booksView,
                              @Nullable List<Book> items) {
        super(
                booksView,
                context,
                items,
                buildJoinAction(),
                buildRemoveAction(),
                buildSearchParametersSupplier(),
                buildVolumeHandlerRetriever()
        );
    }

    private static BiConsumer<Book, Volume> buildJoinAction() {
        return (book, volume) -> {
            //TODO: Building JOIN ACTION
        };
    }

    private static BiConsumer<Book, Volume> buildRemoveAction() {
        return (book, volume) -> {
            //TODO: Building REMOVE ACTION
        };
    }

    private static Function<Book, SearchParameters> buildSearchParametersSupplier() {
        return book -> new SearchParameters()
                .printType(GoogleBooksQueryBuilder.PrintType.BOOKS)
                .title(book.getTitle())
                ; //TODO: finish search params
    }

    private static Function<Book, String> buildVolumeHandlerRetriever() {
        return book -> Optional.ofNullable(book.getServiceConnection())
                .map(ServiceConnection::getGoogleBookLink)
                .orElse(null);
    }

}
