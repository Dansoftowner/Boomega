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

package com.dansoftware.boomega.gui.databaseview;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.googlebooks.GoogleBooksImportModule;
import com.dansoftware.boomega.gui.recordview.RecordsViewModule;
import com.dlsc.workbenchfx.Workbench;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

public class DatabaseView extends Workbench implements ContextTransformable {

    private final Context asContext;
    private final Preferences preferences;
    private final Database database;

    DatabaseView(@NotNull Preferences preferences, @NotNull Database database) {
        this.asContext = Context.from(this);
        this.preferences = preferences;
        this.database = database;
        initModules();
        initUiModifications();
    }

    public DatabaseMeta getOpenedDatabase() {
        return database.getMeta();
    }

    private void initUiModifications() {
        this.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene scene) {
                if (scene != null) {
                    scene.windowProperty().addListener(new ChangeListener<Window>() {
                        @Override
                        public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window window) {
                            if (window != null) {
                                window.showingProperty().addListener(new ChangeListener<Boolean>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean windowShown) {
                                        if (windowShown) {
                                            modifyAddButton();
                                            observable.removeListener(this);
                                        }
                                    }
                                });
                                observable.removeListener(this);
                            }
                        }
                    });
                    observable.removeListener(this);
                }
            }
        });
    }

    private void modifyAddButton() {
        final Button addButton = (Button) DatabaseView.this.lookup("#add-button");
        addButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.HOME));
    }

    private void initModules() {
        getModules().add(new GoogleBooksImportModule(asContext, preferences));
        getModules().add(new RecordsViewModule(asContext, preferences, database));
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
