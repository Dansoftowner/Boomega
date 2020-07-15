package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import com.dansoftware.libraryapp.db.DBMeta;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.dansoftware.libraryapp.locale.I18N.getGeneralWord;

/**
 * A DBManagerView is a gui element that shows a {@link DBManagerTable}
 * inside it. It's used for managing databases.
 *
 * <p>
 * It also provides a reload button for refreshing the items.
 *
 * @author Daniel Gyorffy
 */
public class DBManagerView extends SimpleHeaderView<DBManagerTable> {

    /**
     * Creates a {@link DBManagerView} with a list of database-information ({@link DBMeta}) objects.
     *
     * @param items the items to be displayed.
     */
    public DBManagerView(@NotNull List<DBMeta> items) {
        super(getGeneralWord("database.manager.title"), new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        super.setContent(new DBManagerTable(this, items));
        this.getToolbarControlsRight().add(new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.RELOAD), event -> {
            this.getContent().refresh();
        }));
    }
}
