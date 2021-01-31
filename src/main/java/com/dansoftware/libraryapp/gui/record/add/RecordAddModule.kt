package com.dansoftware.libraryapp.gui.record.add

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.context.NotifiableModule
import com.dansoftware.libraryapp.gui.record.RecordValues
import com.dansoftware.libraryapp.gui.record.show.RecordsViewModule
import com.dansoftware.libraryapp.i18n.I18N
import com.dlsc.workbenchfx.model.WorkbenchModule
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.ToggleGroup
import javafx.util.Duration
import java.util.function.Consumer

/**
 * A [RecordAddModule] is a module that allows the user to add records to the database.
 *
 * @author Daniel Gyorffy
 */
class RecordAddModule(
    private val context: Context,
    private val database: Database
) : WorkbenchModule(I18N.getRecordAddFormValue("record.add.module.title"), MaterialDesignIcon.PLUS_BOX),
    NotifiableModule<RecordValues?> {

    private val content: ObjectProperty<RecordAddForm> = SimpleObjectProperty()
    private lateinit var typeChooserItem: ToolbarItem

    init {
        buildToolbar()
    }

    private fun buildToolbar() {
        toolbarControlsLeft.add(buildRecordTypeChooserItem())
    }

    private fun buildRecordTypeChooserItem(): ToolbarItem =
        ToolbarItem().also { toolbarItem ->
            typeChooserItem = toolbarItem
            ToggleGroup().also { toggleGroup ->
                toggleGroup.selectedToggleProperty()
                    .addListener { _, _, newSelected -> toolbarItem.text = (newSelected as MenuItem?)?.text }

                fun createItem(i18n: String, recordType: Record.Type) =
                    toolbarItem.items.add(RadioMenuItem(I18N.getRecordAddFormValue(i18n)).also {
                        it.toggleGroup = toggleGroup
                        it.userData = recordType
                        it.setOnAction { content.get().recordTypeProperty().set(recordType) }
                        content.addListener { _, _, newForm: RecordAddForm? ->
                            it.isSelected = recordType == newForm?.recordTypeProperty()?.get()
                        }
                    })


                createItem(
                    "record.add.rectype.book",
                    Record.Type.BOOK
                )
                createItem(
                    "record.add.rectype.magazine",
                    Record.Type.MAGAZINE
                )
            }
        }

    override fun commitData(data: RecordValues?) {
        content.get()?.also { content ->
            data?.let {
                content.setValues(data)
                typeChooserItem.items
                    .map { it as RadioMenuItem }
                    .first { it.userData === content.recordType }
                    .isSelected = true
            }
        }
    }

    override fun activate(): Node = content.get() ?: buildForm().also { content.set(it) }

    override fun destroy(): Boolean = content.set(null).let { true }

    private fun buildForm(): RecordAddForm =
        RecordAddForm(
            context,
            Record.Type.BOOK
        ).also {
            it.onRecordAdded = buildRecordAddAction()
        }

    private fun buildRecordAddAction() = Consumer<Record> { record ->
        try {
            database.insertRecord(record)
            context.showInformationNotification(
                I18N.getRecordAddFormValue(
                    when (record.recordType) {
                        Record.Type.BOOK -> "record.book.success.notification"
                        Record.Type.MAGAZINE -> "record.magazine.success.notification"
                    }
                ),
                null,
                Duration.millis(5000.0)
            )
            context.notifyModule(
                RecordsViewModule::class.java,
                RecordsViewModule.Message(record, RecordsViewModule.Message.Action.INSERTED)
            )
        } catch (e: RuntimeException) {
            context.showErrorDialog(
                I18N.getRecordAddFormValue(
                    when (record.recordType) {
                        Record.Type.BOOK -> "record.book.error.title"
                        Record.Type.MAGAZINE -> "record.magazine.error.title"
                    }
                ),
                I18N.getRecordAddFormValue(
                    when (record.recordType) {
                        Record.Type.BOOK -> "record.book.error.msg"
                        Record.Type.MAGAZINE -> "record.magazine.error.msg"
                    }
                ),
                e
            )
        }
    }
}

