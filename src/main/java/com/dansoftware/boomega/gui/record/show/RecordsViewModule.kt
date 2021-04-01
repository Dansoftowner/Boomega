package com.dansoftware.boomega.gui.record.show

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.NotifiableModule
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.model.WorkbenchModule
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node

class RecordsViewModule(
    private val context: Context,
    private val preferences: Preferences,
    private val database: Database
) : WorkbenchModule(I18N.getValue("record.book.view.module.name"), MaterialDesignIcon.LIBRARY),
    NotifiableModule<RecordsViewModule.Message?> {

    private val baseItems: ObservableList<Record> = FXCollections.observableArrayList()
    private val content: ObjectProperty<RecordsView> = SimpleObjectProperty()

    private val table: RecordTable
        get() = content.get().table

    override fun activate(): Node =
        content.get() ?: RecordsView(context, database, preferences, baseItems).also(content::set)

    override fun destroy(): Boolean = true.also {
        content.get()?.writeConfig()
        content.set(null)
        baseItems.clear()
    }

    override fun commitData(data: Message?) {
        content.get()?.let {
            when (data?.action) {
                Message.Action.DELETED -> {
                    baseItems.removeAll(data.records)
                }
                Message.Action.INSERTED -> {
                    baseItems.addAll(data.records)
                }
                Message.Action.UPDATED -> {
                }
            }
            table.refresh()
        }
    }

    class Message(val records: List<Record>, val action: Action) {
        constructor(record: Record, action: Action) : this(listOf(record), action)

        enum class Action {
            DELETED, INSERTED, UPDATED
        }
    }
}