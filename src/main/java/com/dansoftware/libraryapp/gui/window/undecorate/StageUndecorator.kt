package com.dansoftware.libraryapp.gui.window.undecorate

import com.beranabyte.ui.customdecoration.CustomDecorationParameters
import com.beranabyte.ui.customdecoration.CustomDecorationWindowProc
import com.dansoftware.libraryapp.gui.util.WindowUtils
import com.dansoftware.libraryapp.gui.window.undecorate.control.TitledPane
import com.dansoftware.libraryapp.util.os.OsInfo
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class StageUndecorator(private val stage: Stage) {
    fun buildLook(content: Parent) {
        when {
            OsInfo.isWindows10() -> {
                val windowProc = CustomDecorationWindowProc()
                val titledPane = buildTitledPane(content)

                stage.scene = Scene(titledPane)
                stage.maximizedProperty().addListener { _, _, maximized ->
                    titledPane.padding = Insets(if (maximized) 7.0 else 0.0)
                }
                stage.setOnShown { windowProc.init(WindowUtils.getHwnd(stage)) }
            }
            else -> {
                stage.scene = Scene(content)
            }
        }
    }

    private fun buildTitledPane(content: Parent): TitledPane {
        val contentPane = TitledPane(stage, content)
        val titleBar = contentPane.titleBar
        val controlBox = titleBar.controlBox
        val iconArea = titleBar.iconArea

        titleBar.heightProperty().addListener { _, _, newHeight ->
            CustomDecorationParameters.setTitleBarHeight(newHeight.toInt())
        }
        controlBox.widthProperty().addListener { _, _, newWidth ->
            CustomDecorationParameters.setControlBoxWidth(newWidth.toInt())
        }
        iconArea.widthProperty().addListener { _, _, newWidth ->
            CustomDecorationParameters.setIconWidth(newWidth.toInt())
        }

        return contentPane
    }
}