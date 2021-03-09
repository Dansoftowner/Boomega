package com.dansoftware.boomega.gui.record

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.util.copy
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.util.*

object RecordClipboard {

    private val identifier: ObjectProperty<Any> = SimpleObjectProperty()
    private val action: ObjectProperty<Action> = SimpleObjectProperty()
    private val items: ObservableList<Record> = FXCollections.observableArrayList()
    private val callback: ObjectProperty<Callback?> = SimpleObjectProperty()
    private val empty: BooleanProperty = SimpleBooleanProperty().apply { bind(Bindings.isEmpty(items)) }

    @JvmStatic
    fun pushItems(identifier: Any, action: Action, records: List<Record>, callback: Callback?) {
        this.identifier.get()?.let {
             this.callback.get()?.call(this.identifier.get(), this.items.copy(), null)
        }
        this.identifier.set(identifier)
        this.action.set(action)
        this.items.setAll(records)
        this.callback.set(callback)
    }

    @JvmStatic
    fun pullContent(): PullContent =
        PullContent(identifier.get(), this.items.copy().let(Collections::unmodifiableList).also {
            callback.get()?.call(this.identifier.get(), it, action.get())
            if (action.get() == Action.CUT) {
                this.items.clear()
                this.callback.set(null)
                this.identifier.set(null)
            }
        })


    @JvmStatic
    fun emptyProperty(): ReadOnlyBooleanProperty = empty

    @JvmStatic
    fun identifierProperty(): ReadOnlyObjectProperty<Any> = identifier

    enum class Action {
        CUT, COPY
    }

    class PullContent(val identifier: Any, val items: List<Record>)

    fun interface Callback {
        fun call(identifier: Any, items: List<Record>, action: Action?)
    }
}