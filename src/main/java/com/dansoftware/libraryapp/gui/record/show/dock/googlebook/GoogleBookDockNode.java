package com.dansoftware.libraryapp.gui.record.show.dock.googlebook;

import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.DockSystem;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.data.Record;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.window.BaseWindow;
import com.dansoftware.libraryapp.i18n.I18N;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoogleBookDockNode extends DockNode {

    @SuppressWarnings("rawtypes")
    public GoogleBookDockNode(@NotNull DockSystem<?> dockSystem,
                              @NotNull Context context,
                              @NotNull Database database,
                              @Nullable List<Record> items) {
        super(dockSystem, I18N.getGoogleBooksValue("google.books.dock.title"),
                new ImageView(new Image("/com/dansoftware/libraryapp/image/util/google_12px.png")));
        setContent(new GoogleBookDockContent(context, database, items));
        setStageFactory(() -> new BaseWindow() {{
            initOwner(context.getContextWindow());
            setAlwaysOnTop(true);
        }});
    }

    public void setItems(@Nullable List<Record> items) {
        final GoogleBookDockContent content = getContent();
        if (content != null) content.setItems(items);
    }

    public void setOnRefreshed(@Nullable Runnable onRefreshed) {
        final GoogleBookDockContent content = getContent();
        if (content != null) content.setOnRefreshed(onRefreshed);
    }

    private GoogleBookDockContent getContent() {
        return this.getCenter() instanceof GoogleBookDockContent ? (GoogleBookDockContent) this.getCenter() : null;
    }
}
