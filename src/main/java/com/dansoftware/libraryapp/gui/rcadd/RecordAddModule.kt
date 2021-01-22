package com.dansoftware.libraryapp.gui.rcadd

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Book
import com.dansoftware.libraryapp.db.data.Magazine
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.context.NotifiableModule
import com.dansoftware.libraryapp.locale.I18N
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
    NotifiableModule<RecordAddForm.Values?> {

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

                fun createItem(i18n: String, recordType: RecordAddForm.RecordType) =
                    toolbarItem.items.add(RadioMenuItem(I18N.getRecordAddFormValue(i18n)).also {
                        it.toggleGroup = toggleGroup
                        it.userData = recordType
                        it.setOnAction { content.get().recordTypeProperty().set(recordType) }
                        content.addListener { _, _, newForm: RecordAddForm? ->
                            it.isSelected = recordType == newForm?.recordTypeProperty()?.get()
                        }
                    })


                createItem("record.add.rectype.book", RecordAddForm.RecordType.BOOK)
                createItem("record.add.rectype.magazine", RecordAddForm.RecordType.MAGAZINE)
            }
        }

    override fun commitData(data: RecordAddForm.Values?) {
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
        RecordAddForm(context, RecordAddForm.RecordType.BOOK).also {
            it.onBookAdded = buildBookAddAction()
            it.onMagazineAdded = buildMagazineAddAction()
        }

    private fun buildBookAddAction() = Consumer<Book> { book ->
        try {
            database.insertBook(book)
            context.showInformationNotification(
                I18N.getRecordAddFormValue("record.book.success.notification"),
                null,
                Duration.millis(5000.0)
            )
        } catch (e: RuntimeException) {
            context.showErrorDialog(
                I18N.getRecordAddFormValue("record.book.error.title"),
                I18N.getRecordAddFormValue("record.book.error.msg"), e
            )
        }
    }


    private fun buildMagazineAddAction() = Consumer<Magazine> { magazine ->
        try {
            database.insertMagazine(magazine)
            context.showInformationNotification(
                I18N.getRecordAddFormValue("record.magazine.success.notification"),
                null,
                Duration.millis(5000.0)
            )
        } catch (e: RuntimeException) {
            context.showErrorDialog(
                I18N.getRecordAddFormValue("record.magazine.error.title"),
                I18N.getRecordAddFormValue("record.magazine.error.msg"), e
            )
        }
    }
}

