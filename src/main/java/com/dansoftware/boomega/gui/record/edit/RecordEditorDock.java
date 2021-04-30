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

package com.dansoftware.boomega.gui.record.edit;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.dock.DockView;
import com.dansoftware.boomega.gui.record.RecordTable;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

public class RecordEditorDock extends DockView<RecordEditor> {
    public RecordEditorDock(@NotNull Context context,
                            @NotNull Database database,
                            @NotNull SplitPane parent,
                            @NotNull RecordTable table) {
        super(
                parent,
                new FontAwesomeIconView(FontAwesomeIcon.EDIT),
                I18N.getValue("record.editor.dock.title"),
                buildContent(context, database, table)
        );
    }

    private static RecordEditor buildContent(Context context,
                                             Database database,
                                             RecordTable table) {
        var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
        recordEditor.setOnItemsModified(items -> table.refresh());
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                recordEditor.setItems(table.getSelectionModel().getSelectedItems()));
        return recordEditor;
    }
}
