package com.dansoftware.libraryapp.gui.googlebooks.dock;

import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class GoogleBookDockNode<T> extends DockNode {

    public GoogleBookDockNode(@NotNull DockSystem<?> dockSystem,
                              @NotNull Context context,
                              @NotNull ObservableList<T> items,
                              @NotNull BiConsumer<T, Volume> joinAction,
                              @NotNull BiConsumer<T, Volume> removeAction,
                              @NotNull Function<T, SearchParameters> searchParametersSupplier,
                              @NotNull Function<T, String> volumeHandleRetriever) {
        super(dockSystem, new ImageView(new Image("/com/dansoftware/libraryapp/image/util/google_12px.png")),
                "Google Books", //TODO: i18n
                new GoogleBookDockContent<T>(context, items, joinAction, removeAction, searchParametersSupplier, volumeHandleRetriever)
        );
    }
}
