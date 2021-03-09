package com.dansoftware.boomega.gui.record.show

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.collections.ObservableList
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import java.util.function.Consumer

class RecordContextMenu(
    private val records: ObservableList<Record>,
    private val deleteAction: Consumer<List<Record>>,
    private val copyAction: Consumer<List<Record>>,
    private val cutAction: Consumer<List<Record>>,
    private val pasteAction: Runnable
) : ContextMenu() {

    init {
        buildItems()
    }

    private fun buildItems() {
        buildDeleteItem()
        buildCopyItem()
        buildCutItem()
        buildSeparator()
        buildPasteItem()
    }

    private fun buildDeleteItem() {
        //TODO: i18n
        MenuItem("Delete", MaterialDesignIconView(MaterialDesignIcon.DELETE)).apply {
            setOnAction { deleteAction.accept(records.let(::ArrayList)) }
            acceleratorProperty().bind(KeyBindings.deleteRecordKeyBinding.keyCombinationProperty)
        }.let(items::add)
    }

    private fun buildCopyItem() {
        //TODO: i18n
        MenuItem("Copy", MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY)).apply {
            setOnAction { copyAction.accept(records.let(::ArrayList)) }
            acceleratorProperty().bind(KeyBindings.copyRecordKeyBinding.keyCombinationProperty)
        }.let(items::add)
    }

    private fun buildCutItem() {
        //TODO: i18n
        MenuItem("Cut", MaterialDesignIconView(MaterialDesignIcon.CONTENT_CUT)).apply {
            setOnAction { cutAction.accept(records.let(::ArrayList)) }
            acceleratorProperty().bind(KeyBindings.cutRecordKeyBinding.keyCombinationProperty)
        }.let(items::add)
    }

    private fun buildPasteItem() {
        //TODO: i18n
        MenuItem("Paste", MaterialDesignIconView(MaterialDesignIcon.CONTENT_PASTE)).apply {
            setOnAction { pasteAction.run() }
            acceleratorProperty().bind(KeyBindings.pasteRecordKeyBinding.keyCombinationProperty)
        }.let(items::add)
    }

    private fun buildSeparator() {
        items.add(SeparatorMenuItem())
    }

    class Builder(private val records: ObservableList<Record>) {
        private var deleteAction: Consumer<List<Record>>? = null
        private var cutAction: Consumer<List<Record>>? = null
        private var copyAction: Consumer<List<Record>>? = null
        private var pasteAction: Runnable? = null

        fun deleteAction(deleteAction: Consumer<List<Record>>) = this.apply {
            this.deleteAction = deleteAction
        }

        fun cutAction(cutAction: Consumer<List<Record>>) = this.apply {
            this.cutAction = cutAction
        }

        fun copyAction(copyAction: Consumer<List<Record>>) = this.apply {
            this.copyAction = copyAction
        }

        fun pasteAction(pasteAction: Runnable?) = this.apply {
            this.pasteAction = pasteAction
        }

        fun build(): RecordContextMenu =
            RecordContextMenu(
                records,
                deleteAction ?: Consumer { },
                copyAction ?: Consumer { },
                cutAction ?: Consumer { },
                pasteAction ?: Runnable { }
            )
    }

    companion object {
        @JvmStatic
        fun builder(records: ObservableList<Record>): Builder = records.let(::Builder)
    }

}