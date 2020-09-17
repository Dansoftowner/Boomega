package com.dansoftware.libraryapp.gui.dbmanager;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.binding.StringExpression;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * A DBManagerView is a gui element that shows a {@link DatabaseManagerTable}
 * inside it. It's used for managing databases.
 *
 * <p>
 * It also provides some additional toolbar-item in the header.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseManagerView extends SimpleHeaderView<DatabaseManagerTable> implements Themeable {

    /**
     * Creates a {@link DatabaseManagerView} with a list of database-information ({@link DatabaseMeta}) objects.
     *
     * @param databaseTracker the database-tracker
     */
    public DatabaseManagerView(@NotNull DatabaseTracker databaseTracker) {
        super(I18N.getGeneralWord("database.manager.title"), new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        super.setContent(new DatabaseManagerTable(this, databaseTracker));
        this.createToolbarControls();
        Theme.registerThemeable(this);
    }

    private void createToolbarControls() {
        //Toolbar item that shows how many items are selected from the table
        DatabaseManagerTable table = getContent();
        var selectedItemsIndicator = new ToolbarItem();
        StringExpression allItemsSlashSelected = table.itemsCount().asString()
                .concat("/")
                .concat(table.selectedItemsCount())
                .concat(StringUtils.SPACE)
                .concat(I18N.getGeneralWord("database.manager.selected"));
        selectedItemsIndicator.textProperty().bind(allItemsSlashSelected);
        this.getToolbarControlsRight().add(selectedItemsIndicator);

        //Toolbar item that can refresh the table
        this.getToolbarControlsRight().add(new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.RELOAD), event -> {
            ToolbarItem source = (ToolbarItem) event.getSource();
            this.getContent().refresh();
            new animatefx.animation.RotateIn(source.getGraphic()).play();
        }));
    }

    @Override
    public void handleThemeApply(Theme newTheme) {
        newTheme.apply(this);
    }
}
