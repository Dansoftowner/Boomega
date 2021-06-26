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

import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.i18n.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * A DatabaseCreatorView is a gui object that let's the user to
 * create new database files. It should be displayed inside
 * a {@link DatabaseCreatorWindow}.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseCreatorView extends SimpleHeaderView<Node> implements ContextTransformable {

    private final DatabaseCreatorForm form;
    private final Context asContext;

    public DatabaseCreatorView(@NotNull DatabaseTracker databaseTracker) {
        super(I18N.getValue("database.creator.title"),
                new MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS));
        this.asContext = Context.from(this);
        this.setContent(this.form = new DatabaseCreatorForm(asContext, databaseTracker));
    }

    public DatabaseMeta getCreatedDatabase() {
        return this.form.createdDatabaseProperty().get();
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
