/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.dbmanager;

import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.i18n.I18N;
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
public class DatabaseManagerView extends SimpleHeaderView<DatabaseManagerTable> implements ContextTransformable {

    private final Context asContext;

    /**
     * Creates a {@link DatabaseManagerView} with a list of database-information ({@link DatabaseMeta}) objects.
     *
     * @param databaseTracker the database-tracker
     */
    public DatabaseManagerView(@NotNull DatabaseTracker databaseTracker) {
        super(I18N.getValue("database.manager.title"), new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        this.asContext = Context.from(this);
        this.setContent(new DatabaseManagerTable(asContext, databaseTracker));
        this.createToolbarControls();
    }

    private void createToolbarControls() {
        //Toolbar item that shows how many items are selected from the table
        DatabaseManagerTable table = getContent();
        var selectedItemsIndicator = new ToolbarItem();
        StringExpression allItemsSlashSelected = table.itemsCount().asString()
                .concat("/")
                .concat(table.selectedItemsCount())
                .concat(StringUtils.SPACE)
                .concat(I18N.getValue("database.manager.selected"));
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
    public @NotNull Context getContext() {
        return asContext;
    }
}
