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

package com.dansoftware.boomega.gui.dbcreator;

import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.database.tracking.DatabaseTracker;
import com.dansoftware.boomega.gui.base.BaseView;
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

    public DatabaseCreatorView(@NotNull DatabaseTracker databaseTracker) {
        this.form = new DatabaseCreatorForm(this, databaseTracker);
        this.setContent(new VBox(new DatabaseCreatorToolbar(), form));
    }

    public DatabaseMeta getCreatedDatabase() {
        return this.form.createdDatabaseProperty().get();
    }
}
