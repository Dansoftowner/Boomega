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

package com.dansoftware.boomega.gui.record.connection;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.dock.DockView;
import com.dansoftware.boomega.gui.record.RecordTable;
import com.dansoftware.boomega.i18n.I18N;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

public class GoogleBookConnectionDock extends DockView<GoogleBookConnectionView> {

    public GoogleBookConnectionDock(@NotNull Context context,
                                    @NotNull Database database,
                                    @NotNull SplitPane parent,
                                    @NotNull RecordTable table) {
        super(
                parent,
                new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png")),
                I18N.getValue("google.books.dock.title"),
                buildContent(context, database, table)
        );
    }

    private static GoogleBookConnectionView buildContent(Context context, Database database, RecordTable table) {
        var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
        dockContent.setOnRefreshed(table::refresh);
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                dockContent.setItems(table.getSelectionModel().getSelectedItems()));
        return dockContent;
    }
}
