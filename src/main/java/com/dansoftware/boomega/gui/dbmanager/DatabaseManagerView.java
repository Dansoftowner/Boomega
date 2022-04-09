/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

import com.dansoftware.boomega.gui.base.BaseView;
import javafx.beans.binding.IntegerBinding;
import javafx.scene.layout.VBox;

/**
 * A DBManagerView is a gui element that shows a {@link DatabaseManagerTable}
 * inside it. It's used for managing databases.
 *
 * <p>
 * It also provides some additional toolbar-item in the header.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseManagerView extends BaseView {

    private final DatabaseManagerTable table;

    public DatabaseManagerView() {
        this.table = new DatabaseManagerTable(this);
        buildUI();
    }

    private void buildUI() {
        this.setContent(new VBox(new DatabaseManagerToolbar(this), table));
    }

    public void refresh() {
        table.refresh();
    }

    public IntegerBinding tableItemsCount() {
        return table.itemsCount();
    }

    public IntegerBinding selectedTableItemsCount() {
        return table.selectedItemsCount();
    }
}
