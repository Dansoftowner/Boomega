package com.dansoftware.boomega.gui.firsttime.imp

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.config.PreferencesImporter
import com.dansoftware.boomega.config.PreferencesImporter.InvalidZipContentException
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class ConfigurationImportViewContent(
    private val context: Context,
    private val preferences: Preferences
) : VBox(10.0) {

    private val radioGroup: ToggleGroup = ToggleGroup()
    private val fileChooser: FileChooser = buildFileChooser()

    private val customLocationInputText: StringProperty = SimpleStringProperty()
    private val chosenFile: ObjectProperty<File> = SimpleObjectProperty()
    private val import: BooleanProperty = SimpleBooleanProperty()
    private val imported: BooleanProperty = SimpleBooleanProperty()

    val isImported: Boolean
        get() = imported.get()

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        children.add(buildImportLabel())
        children.add(buildImportRadioButton())
        children.add(buildCustomLocationInputArea())
        children.add(buildNotImportRadioButton())
        children.add(buildOkArea())
    }

    private fun buildFileChooser() = FileChooser().apply {
        extensionFilters.add(FileChooser.ExtensionFilter("All files", "*"))
        FileChooser.ExtensionFilter("Zip files", "*.zip").let {
            extensionFilters.add(it)
            selectedExtensionFilter = it
        }
    }

    private fun buildImportLabel() = Label(I18N.getValue("configuration.import.label"))

    private fun buildImportRadioButton() = RadioButton().apply {
        text = I18N.getValue("configuration.import.custom.loc")
        toggleGroup = radioGroup
        import.bind(selectedProperty())
    }

    private fun buildCustomLocationInputArea() = HBox(5.0).apply {
        padding = Insets(2.0, 10.0, 10.0, 10.0)
        children.add(buildCustomLocationInput())
        children.add(buildFileOpenerButton())
    }

    private fun buildCustomLocationInput() = TextField().apply {
        isEditable = false
        minHeight = 30.0
        textProperty().bind(customLocationInputText)
        disableProperty().bind(import.not())
        HBox.setHgrow(this, Priority.ALWAYS)
    }

    private fun buildFileOpenerButton() = Button().apply {
        graphic = MaterialDesignIconView(MaterialDesignIcon.FOLDER)
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        minHeight = 30.0
        disableProperty().bind(import.not());
        setOnAction {
            showFileOpenDialog()
        }
    }

    private fun buildNotImportRadioButton() = RadioButton().apply {
        text = I18N.getValue("configuration.import.not")
        toggleGroup = radioGroup
        isSelected = true
    }

    private fun buildOkArea() = StackPane().apply {
        children.add(buildOkButton())
    }

    private fun buildOkButton() = Button(I18N.getValue("Dialog.ok.button")).apply {
        setOnAction { close() }
    }

    private fun showFileOpenDialog() {
        fileChooser.showOpenDialog(context.contextWindow).let {
            customLocationInputText.set(it.absolutePath)
            chosenFile.set(it)
        }
    }

    private fun close() {
        chosenFile.get()?.let {
            try {
                val preferencesImporter = PreferencesImporter(preferences)
                preferencesImporter.importFromZip(chosenFile.get(), preferences)
                imported.set(true)
                context.close()
            } catch (e: IOException) {
                logger.error("Failed to import configurations", e)
                context.showErrorDialog(
                    I18N.getValue("configuration.import.failed.title"),
                    I18N.getValue("configuration.import.failed.msg"),
                    e
                )
            } catch (e: InvalidZipContentException) {
                logger.error("Failed to import configurations", e)
                context.showErrorDialog(
                    I18N.getValue("configuration.import.failed.title"),
                    I18N.getValue("configuration.import.failed.msg"),
                    e
                )
            }
        } ?: context.close()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ConfigurationImportViewContent::class.java)
    }
}