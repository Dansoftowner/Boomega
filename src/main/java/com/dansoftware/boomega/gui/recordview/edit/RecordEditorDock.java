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

package com.dansoftware.boomega.gui.recordview.edit;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.recordview.RecordTable;
import com.dansoftware.boomega.gui.recordview.dock.DockView;
import com.dansoftware.boomega.i18n.I18N;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import static com.dansoftware.boomega.gui.util.Icons.icon;

public class RecordEditorDock extends DockView<RecordEditor> {
    public RecordEditorDock(@NotNull Context context,
                            @NotNull Database database,
                            @NotNull SplitPane parent,
                            @NotNull RecordTable table) {
        super(
                parent,
                icon("pencil-icon"),
                I18N.getValue("record.editor.dock.title"),
                buildContent(context, database, table)
        );
        buildCustomItems();
    }

    private void buildCustomItems() {
        getButtonBar().getChildren().add(0, buildSaveButton());
        getButtonBar().getChildren().add(1, new Separator(Orientation.VERTICAL));
    }

    private Button buildSaveButton() {
        var button = new Button();
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.disableProperty().bind(getContent().changedProperty().not());
        button.setPadding(new Insets(0));
        button.setGraphic(icon("save-icon"));
        button.setTooltip(new Tooltip(I18N.getValue("save.changes")));
        button.setOnAction(event -> getContent().saveChanges());
        return button;
    }

    private static RecordEditor buildContent(Context context,
                                             Database database,
                                             RecordTable table) {
        var recordEditor = new RecordEditor(context, database, table.getSelectionModel().getSelectedItems());
        recordEditor.setOnItemsModified(items -> table.refresh());
        return recordEditor;
    }
}
