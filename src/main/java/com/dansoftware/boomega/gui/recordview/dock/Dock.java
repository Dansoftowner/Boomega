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

package com.dansoftware.boomega.gui.recordview.dock;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.recordview.RecordTable;
import com.dansoftware.boomega.gui.recordview.connection.GoogleBookConnectionDock;
import com.dansoftware.boomega.gui.recordview.edit.RecordEditorDock;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.dansoftware.boomega.gui.util.Icons.icon;
import static com.dansoftware.boomega.i18n.I18NUtils.i18n;

public enum Dock {

    RECORD_EDITOR(RecordEditorDock.class, "record.editor.dock.title") {
        @Override
        protected DockView<?> buildView(@NotNull Context context,
                                        @NotNull Database database,
                                        @NotNull RecordTable table,
                                        @NotNull SplitPane splitPane) {
            return new RecordEditorDock(context, database, splitPane, table);
        }

        @Override
        public Node getGraphic() {
            return icon("pencil-icon");
        }
    },

    GOOGLE_BOOK_CONNECTION(GoogleBookConnectionDock.class, "google.books.volume_information") {
        @Override
        protected DockView<?> buildView(@NotNull Context context,
                                        @NotNull Database database,
                                        @NotNull RecordTable table,
                                        @NotNull SplitPane splitPane) {

            return new GoogleBookConnectionDock(context, database, splitPane, table);
        }

        @Override
        public Node getGraphic() {
            return icon("google-icon");
        }
    };

    private final Class<? extends DockView<?>> dockViewClass;
    private final String i18n;

    Dock(@NotNull Class<? extends DockView<?>> dockViewClass, @NotNull String i18n) {
        this.dockViewClass = dockViewClass;
        this.i18n = i18n;
    }

    public String getI18nKey() {
        return i18n;
    }

    protected abstract DockView<?> buildView(
            @NotNull Context context,
            @NotNull Database database,
            @NotNull RecordTable table,
            @NotNull SplitPane splitPane
    );

    public final void align(@NotNull Context context,
                            @NotNull Database database,
                            @NotNull RecordTable table,
                            @NotNull SplitPane splitPane) {
        if (splitPane.getItems().stream().noneMatch(dockViewClass::isInstance)) {
            splitPane.getItems().add(buildView(context, database, table, splitPane));
        }
    }

    public final void removeFrom(@NotNull SplitPane splitPane) {
        splitPane.getItems().removeIf(dockViewClass::isInstance);
    }

    /**
     * @return the icon of the dock
     */
    public abstract Node getGraphic();

    @Override
    public String toString() {
        return i18n(i18n);
    }

    @Nullable
    public static Dock parse(Class<? extends DockView<?>> dockViewClass) {
        return Arrays.stream(values()).filter(it -> it.dockViewClass.equals(dockViewClass)).findFirst().orElse(null);
    }
}
