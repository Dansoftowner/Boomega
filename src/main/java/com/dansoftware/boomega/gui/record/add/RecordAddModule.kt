package com.dansoftware.boomega.gui.record.add

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.NotifiableModule
import com.dansoftware.boomega.gui.record.RecordValues
import com.dansoftware.boomega.gui.record.show.RecordsViewModule
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.model.WorkbenchModule
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.RadioMenuItem
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
) : WorkbenchModule(I18N.getValue("record.add.module.title"), MaterialDesignIcon.PLUS_BOX),
    NotifiableModule<RecordValues?> {

    private val content: ObjectProperty<RecordAddView> = SimpleObjectProperty()
    private lateinit var typeChooserItem: ToolbarItem

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

    private fun buildForm(): RecordAddView =
        RecordAddView(
            context,
            Record.Type.BOOK
        ).also {
            it.onRecordAdded = buildRecordAddAction()
        }

    private fun buildRecordAddAction() = Consumer<Record> { record ->
        try {
            database.insertRecord(record)
            context.showInformationNotification(
                I18N.getValue(
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
                I18N.getValue(
                    when (record.recordType) {
                        Record.Type.BOOK -> "record.book.error.title"
                        Record.Type.MAGAZINE -> "record.magazine.error.title"
                    }
                ),
                I18N.getValue(
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

