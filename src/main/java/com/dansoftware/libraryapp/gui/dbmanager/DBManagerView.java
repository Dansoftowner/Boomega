package com.dansoftware.libraryapp.gui.dbmanager;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringExpression;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A DBManagerView is a gui element that shows a {@link DBManagerTable}
 * inside it. It's used for managing databases.
 *
 * <p>
 * It also provides some additional toolbar-item in the header.
 *
 * @author Daniel Gyorffy
 */
public class DBManagerView extends SimpleHeaderView<DBManagerTable> {

    /**
     * Creates a {@link DBManagerView} with a list of database-information ({@link DatabaseMeta}) objects.
     *
     * @param items the items to be displayed.
     */
    public DBManagerView(@NotNull List<DatabaseMeta> items) {
        super(I18N.getGeneralWord("database.manager.title"), new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        super.setContent(new DBManagerTable(this, items));
        this.createToolbarControls();
    }

    private void createToolbarControls() {
        //Toolbar item that shows how many items are selected from the table
        var selectedItemsIndicator = new ToolbarItem();
        IntegerBinding allItemsCount = Bindings.size(this.getContent().getItems());
        IntegerBinding selectedItemsCount = Bindings.size(this.getContent().getSelectionModel().getSelectedItems());
        StringExpression allItemsSlashSelected = allItemsCount.asString()
                .concat("/")
                .concat(selectedItemsCount)
                .concat(StringUtils.SPACE)
                .concat(I18N.getGeneralWord("database.manager.selected"));
        selectedItemsIndicator.textProperty().bind(allItemsSlashSelected);
        this.getToolbarControlsRight().add(selectedItemsIndicator);

        //Toolbar item that can refresh the table
        this.getToolbarControlsRight().add(new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.RELOAD), event -> {
            this.getContent().refresh();
        }));
    }
}
