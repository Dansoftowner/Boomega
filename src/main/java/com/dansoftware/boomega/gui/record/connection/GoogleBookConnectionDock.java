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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class GoogleBookConnectionDock extends DockView<GoogleBookConnectionView> {

    private static final String STYLE_CLASS = "google-book-dock";

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
        getStyleClass().add(STYLE_CLASS);
        buildCustomItems();
    }

    private void buildCustomItems() {
        getButtonBar().getChildren().add(0, buildConnectionRemoveButton());
        getButtonBar().getChildren().add(1, new Separator(Orientation.VERTICAL));
    }

    private Button buildConnectionRemoveButton() {
        var button = new Button();
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.disableProperty().bind(getContent().isEmpty());
        button.setPadding(new Insets(0));
        button.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DELETE));
        button.setTooltip(new Tooltip(I18N.getValue("google.books.dock.remove_connection")));
        button.getStyleClass().add("remove-button");
        button.setOnAction(event -> getContent().removeConnectionRequest());
        VBox.setVgrow(button, Priority.ALWAYS);
        return button;
    }

    private static GoogleBookConnectionView buildContent(Context context, Database database, RecordTable table) {
        var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
        dockContent.setOnRefreshed(table::refresh);
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                dockContent.setItems(table.getSelectionModel().getSelectedItems()));
        return dockContent;
    }
}
