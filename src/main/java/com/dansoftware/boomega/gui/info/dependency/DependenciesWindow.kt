package com.dansoftware.boomega.gui.info.dependency

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A [Window] that is used for showing a [DependencyTable].
 *
 * @author Daniel Gyorffy
 */
class DependenciesWindow(dependencyTable: DependencyTable, owner: Window?) :
    BaseWindow("window.dependencies.title", dependencyTable, { dependencyTable.context }) {
    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        isResizable = false
    }
}