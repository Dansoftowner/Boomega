package com.dansoftware.boomega.gui.record.show

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.NotifiableModule
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.model.WorkbenchModule
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

class RecordsViewModule(
    private val context: Context,
    private val preferences: Preferences,
    private val database: Database
) : WorkbenchModule(I18N.getValue("record.book.view.module.name"), MaterialDesignIcon.LIBRARY),
    NotifiableModule<RecordsViewModule.Message?> {

    private val content: ObjectProperty<RecordsView> = SimpleObjectProperty()

    override fun activate(): Node =
        content.get() ?: RecordsView(context, database, preferences).also(content::set)

    override fun destroy(): Boolean = true.also {
        content.get()?.writeConfig()
        content.set(null)
    }

    override fun commitData(data: Message?) {
        content.get()?.let { view ->
            data?.let {
                when (it) {
                    is InsertionRequest ->
                        view.insertNewRecord(it.record)
                    else -> {

                    }
                }
            }
        }
    }

    interface Message

    class InsertionRequest(val record: Record) : Message
}