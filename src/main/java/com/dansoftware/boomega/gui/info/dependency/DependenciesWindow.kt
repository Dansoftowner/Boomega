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

package com.dansoftware.boomega.gui.info.dependency

import com.dansoftware.boomega.gui.info.dependency.meta.DependencyInfo
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A [Window] that is used for showing a [DependencyTable].
 *
 * @author Daniel Gyorffy
 */
class DependenciesWindow(dependencyList: List<DependencyInfo> = DependencyLister.listDependencies(), owner: Window?) :
    BaseWindow<DependencyTable>(I18N.getValue("window.dependencies.title"), DependencyTable(dependencyList)) {

    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        isResizable = false
        scene = Scene(DependencyTable(dependencyList))
    }
}