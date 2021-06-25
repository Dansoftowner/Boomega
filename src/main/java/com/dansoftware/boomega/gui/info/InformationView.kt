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

package com.dansoftware.boomega.gui.info

import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.TitledOverlayBox
import com.dansoftware.boomega.gui.control.HighlightableLabel
import com.dansoftware.boomega.gui.info.dependency.DependencyViewerActivity
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.getDisplayName
import com.dansoftware.boomega.util.SystemBrowser
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Insets
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import org.apache.commons.lang3.StringUtils
import java.util.*

class InformationView(val context: Context) : VBox(5.0) {

    companion object {
        const val GITHUB_REPO_URL = "https://github.com/Dansoftowner/LibraryApp2020"
        const val LICENSE_URL = "https://github.com/Dansoftowner/Boomega/blob/master/LICENSE"
        const val LICENSE_NAME = "GNU General Public License v3.0"
    }

    init {
        styleClass.add("information-view")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        buildUI()
    }

    private fun buildUI() {
        buildProgramInfo()
        children.add(Separator())
        buildLanguageInfo()
        children.add(Separator())
        buildJavaInfo()
        children.add(Separator())
        buildLogInfo()
        children.add(Separator())
        buildBottom()
    }

    private fun buildProgramInfo() {
        children.add(buildVersionLabel())
        children.add(buildDeveloperLabel())
        children.add(buildLicenseLabel())
    }

    private fun buildLanguageInfo() {
        children.add(buildLangLabel())
        children.add(buildLangTranslatorLabel())
    }

    private fun buildVersionLabel() =
        KeyValuePair("info.version", System.getProperty("boomega.version"))

    private fun buildDeveloperLabel() =
        KeyValuePair("info.developer", I18N.getLanguagePack().displayPersonName("Dániel", "Györffy"));

    private fun buildLicenseLabel() = KeyValuePair(
        "info.license",
        Label(LICENSE_NAME).apply {
            cursor = Cursor.HAND
            tooltip = Tooltip(LICENSE_URL)
            setOnMouseClicked {
                if (it.button == MouseButton.PRIMARY) {
                    SystemBrowser.browse(LICENSE_URL)
                }
            }
        }
    )

    private fun buildLangLabel() =
        KeyValuePair("info.lang", Locale.getDefault().displayLanguage)

    private fun buildLangTranslatorLabel() =
        KeyValuePair(
            "info.lang.translator",
            I18N.getLanguagePack().translator?.getDisplayName()
        )

    private fun buildJavaInfo() {
        children.add(KeyValuePair("java.home", System.getProperty("java.home")))
        children.add(KeyValuePair("java.vm", System.getProperty("java.vm.name").let { vmName ->
            System.getProperty("java.vm.vendor").let {
                if (StringUtils.isBlank(it).not()) {
                    "$vmName ${I18N.getValues().getString("java.vm.by")} $it"
                } else null
            } ?: vmName
        }))
        children.add(KeyValuePair("java.version", System.getProperty("java.version")))
        children.add(KeyValuePair("javafx.version", System.getProperty("javafx.version")))
    }

    private fun buildLogInfo() {
        children.add(KeyValuePair("info.logs.loc", System.getProperty("log.file.path.full")))
    }

    private fun buildBottom() =
        StackPane(
            Group(
                HBox(10.0).apply {
                    children.add(buildGithubButton())
                    children.add(buildDependencyButton())
                }
            )
        ).also { children.add(it) }

    private fun buildDependencyButton() =
        Button(
            I18N.getValues().getString("info.show.dependencies"),
            MaterialDesignIconView(MaterialDesignIcon.CODE_BRACES)
        ).also {
            it.setOnAction {
                DependencyViewerActivity(this.context).show()
            }
        }

    private fun buildGithubButton() =
        Button(
            I18N.getValues().getString("info.show.github"),
            MaterialDesignIconView(MaterialDesignIcon.GITHUB_BOX)
        ).also {
            it.setOnAction {
                SystemBrowser.browse(GITHUB_REPO_URL)
            }
        }

    private class KeyValuePair(i18n: String, value: Node) : HBox() {

        constructor(i18n: String, value: String?) : this(i18n, HighlightableLabel(value).also {
            setHgrow(it, Priority.ALWAYS)
        })

        init {
            children.add(Label(I18N.getValues().getString(i18n)))
            children.add(Label(":").also {
                it.padding = Insets(0.0, 5.0, 0.0, 0.0)
            })
            children.add(value)
        }

    }
}