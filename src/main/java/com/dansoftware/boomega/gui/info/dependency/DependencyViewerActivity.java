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

package com.dansoftware.boomega.gui.info.dependency;

import com.dansoftware.boomega.gui.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link DependencyViewerActivity} used for launching a {@link DependenciesWindow} with
 * a {@link DependencyTable} easily.
 *
 * @author Daniel Gyorffy
 */
public class DependencyViewerActivity {

    private final Context context;

    public DependencyViewerActivity(@NotNull Context context) {
        this.context = context;
    }

    public void show() {
        var table = new DependencyTable(DependencyLister.listDependencies());
        var window = new DependenciesWindow(table, context.getContextWindow());
        window.show();
    }
}
