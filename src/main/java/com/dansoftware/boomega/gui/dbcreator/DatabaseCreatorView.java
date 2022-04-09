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

package com.dansoftware.boomega.gui.dbcreator;

import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.database.api.DatabaseProvider;
import com.dansoftware.boomega.database.tracking.DatabaseTracker;
import com.dansoftware.boomega.gui.base.BaseView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * A DatabaseCreatorView is a gui object that let's the user to
 * create new database files. It should be displayed inside
 * a {@link DatabaseCreatorWindow}.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseCreatorView extends BaseView {

    private final DatabaseCreatorForm form;

    public DatabaseCreatorView() {
        var databaseType = new SimpleObjectProperty<DatabaseProvider<?>>();
        this.form = new DatabaseCreatorForm(this, databaseType);
        this.setContent(new VBox(new DatabaseCreatorToolbar(databaseType), form));
    }

    public DatabaseMeta getCreatedDatabase() {
        return this.form.getCreatedDatabase();
    }
}
