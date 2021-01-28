package com.dansoftware.libraryapp.gui.googlebooks.dock;

import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import com.dansoftware.libraryapp.i18n.I18N;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GoogleBookDockNode<T> extends DockNode {

    private final GoogleBookDockContent<T> content;

    public GoogleBookDockNode(@NotNull DockSystem<?> dockSystem,
                              @NotNull Context context,
                              @Nullable List<T> items,
                              @NotNull BiConsumer<T, Volume> joinAction,
                              @NotNull BiConsumer<T, Volume> removeAction,
                              @NotNull Function<T, SearchParameters> searchParametersSupplier,
                              @NotNull Function<T, String> volumeHandleRetriever) {
        super(dockSystem, I18N.getGoogleBooksImportValue("google.books.dock.title"),
                new ImageView(new Image("/com/dansoftware/libraryapp/image/util/google_12px.png")));
        this.content = new GoogleBookDockContent<T>(context, items, joinAction, removeAction, searchParametersSupplier, volumeHandleRetriever);
        setContent(content);
    }

    public void setItems(@Nullable List<T> items) {
        this.content.setItems(items);
    }
}
