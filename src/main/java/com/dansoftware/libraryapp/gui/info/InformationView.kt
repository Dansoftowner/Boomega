package com.dansoftware.libraryapp.gui.info

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.context.TitledOverlayBox
import com.dansoftware.libraryapp.gui.info.dependency.DependencyViewerActivity
import com.dansoftware.libraryapp.locale.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import org.apache.commons.lang3.StringUtils
import java.util.*

class InformationViewOverlay(context: Context) :
    TitledOverlayBox(
        I18N.getInfoViewValues().getString("info.view.title"),
        MaterialDesignIconView(MaterialDesignIcon.INFORMATION),
        InformationView(context),
        resizableH = true,
        resizableV = false
    )

class InformationView(val context: Context) : VBox(5.0) {

    init {
        styleClass.add("information-view")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        buildProgramInfo()
        separator()
        buildJavaInfo()
        separator()
        buildLogInfo()
        separator()
        buildBottom()
    }

    private fun separator() = children.add(Separator())

    private fun buildProgramInfo() {
        children.add(KeyValuePair("info.version", System.getProperty("libraryapp.version")))
        children.add(KeyValuePair("info.developer", "Györffy Dániel"))
        children.add(
            HBox(
                2.0,
                KeyValuePair("info.lang", Locale.getDefault().displayLanguage),
                Label(","),
                KeyValuePair(
                    "info.lang.translator",
                    I18N.getLanguagePack().translator?.getDisplayName(Locale.getDefault())
                )
            )
        )
    }

    private fun buildJavaInfo() {
        children.add(KeyValuePair("java.home", System.getProperty("java.home")))
        children.add(KeyValuePair("java.vm", System.getProperty("java.vm.name").let { vmName ->
            System.getProperty("java.vm.vendor").let {
                if (StringUtils.isBlank(it).not()) {
                    "$vmName ${I18N.getInfoViewValues().getString("java.vm.by")} $it"
                } else null
            } ?: vmName
        }))
        children.add(KeyValuePair("java.version", System.getProperty("java.version")))
        children.add(KeyValuePair("javafx.version", System.getProperty("javafx.version")))
    }

    private fun buildLogInfo() {
        children.add(KeyValuePair("info.logs.loc", TextField(System.getProperty("log.file.path.full")).also {
            it.isEditable = false
            HBox.setMargin(it, Insets(0.0, 0.0, 0.0, 5.0))
            HBox.setHgrow(it, Priority.ALWAYS)
        }))
    }

    private fun buildBottom() = StackPane(Group(HBox(10.0).also {
        it.children.add(buildGithubButton())
        it.children.add(buildDependencyButton())
        it.children.add(buildCopyButton())
    })).also { children.add(it) }

    private fun buildDependencyButton() =
        Button(
            I18N.getInfoViewValues().getString("info.show.dependencies"),
            MaterialDesignIconView(MaterialDesignIcon.CODE_BRACES)
        ).also {
            it.setOnAction {
                DependencyViewerActivity(this.context).show()
            }
        }

    private fun buildGithubButton() =
        Button(
            I18N.getInfoViewValues().getString("info.show.github"),
            MaterialDesignIconView(MaterialDesignIcon.GITHUB_BOX)
        ).also {
            // TODO: github button action
        }

    private fun buildCopyButton() =
        Button(
            I18N.getInfoViewValues().getString("info.copy"),
            MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY)
        ).also {
            it.setOnAction {
                ClipboardContent().also { clipboardContent ->
                    clipboardContent.putString(getApplicationInfoCopy())
                    Clipboard.getSystemClipboard().setContent(clipboardContent)
                }
            }
        }

    private class KeyValuePair(i18n: String, value: Node) : HBox() {

        constructor(i18n: String, value: String?) : this(i18n, Label(value).also {
            it.padding = Insets(0.0, 0.0, 0.0, 5.0)
        })

        init {
            children.add(Label(I18N.getInfoViewValues().getString(i18n)))
            children.add(Label(":"))
            children.add(value)
        }

    }
}