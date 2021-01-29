package com.dansoftware.libraryapp.gui.googlebooks.dock;

import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.data.Record;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import com.dansoftware.libraryapp.i18n.I18N;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GoogleBookDockNode extends DockNode {

    public GoogleBookDockNode(@NotNull DockSystem<?> dockSystem,
                              @NotNull Context context,
                              @NotNull Database database,
                              @Nullable List<Record> items) {
        super(dockSystem, I18N.getGoogleBooksImportValue("google.books.dock.title"),
                new ImageView(new Image("/com/dansoftware/libraryapp/image/util/google_12px.png")));
        setContent(new GoogleBookDockContent(context, database, items));
    }

    public void setItems(@Nullable List<Record> items) {
        if (this.getCenter() instanceof GoogleBookDockContent) {
            ((GoogleBookDockContent) this.getCenter()).setItems(items);
        }
    }
}
