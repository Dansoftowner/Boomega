package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public class GoogleBooksImportModule extends WorkbenchModule {

    private final Context context;
    private final Database database;
    private GoogleBooksImportPanel content;

    public GoogleBooksImportModule(@NotNull Context context, @NotNull Database database) {
        super(I18N.getGoogleBooksImportValue("google.books.import.module.title"), MaterialDesignIcon.GOOGLE);
        this.context = context;
        this.database = database;
    }

    @Override
    public Node activate() {
        if (content == null) {
            content = new GoogleBooksImportPanel(context, database);
        }
        return content;
    }

    @Override
    public boolean destroy() {
        content = null;
        return true;
    }
}
