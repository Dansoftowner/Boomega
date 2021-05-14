package com.dansoftware.boomega.gui.recordview

import com.dansoftware.boomega.db.data.Record
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.util.*
import java.util.function.Consumer

object RecordClipboard {

    private val identifier: ObjectProperty<Any> = SimpleObjectProperty()
    private val action: ObjectProperty<Action> = SimpleObjectProperty()
    private val items: ObservableList<Record> = FXCollections.observableArrayList()
    private val callback: ObjectProperty<ClipboardPush?> = SimpleObjectProperty()
    private val empty: BooleanProperty = SimpleBooleanProperty().apply { bind(Bindings.isEmpty(items)) }

    @JvmStatic
    fun pushItems(identifier: Any, action: Action, items: List<Record>): ClipboardPush =
        items.let(::ArrayList).let { records ->
            ClipboardPushImpl(identifier, records, action).also { push ->
                this.identifier.get()?.let {
                    this.callback.get()?.let {
                        it.onOverridden?.accept(it)
                    }
                }
                this.identifier.set(identifier)
                this.action.set(action)
                this.items.setAll(records)
                this.callback.set(push)
            }
        }

    @JvmStatic
    fun pullContent(): ClipboardContent =
        ClipboardContent(identifier.get(), ArrayList(this.items).let(Collections::unmodifiableList).also {
            this.callback.get()?.let { it.onPulled?.accept(it) }
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

    class ClipboardContent(val identifier: Any, val items: List<Record>)

    interface ClipboardPush {
        val identifier: Any
        val items: List<Record>
        val action: Action
        var onOverridden: Consumer<ClipboardPush>?
        var onPulled: Consumer<ClipboardPush>?

        fun onOverridden(value: Consumer<ClipboardPush>?) {
            this.onOverridden = value
        }

        fun onPulled(value: Consumer<ClipboardPush>?) {
            this.onPulled = value
        }
    }

    private class ClipboardPushImpl(
        override val identifier: Any,
        override val items: List<Record>,
        override val action: Action
    ) : ClipboardPush {
        override var onOverridden: Consumer<ClipboardPush>? = null
        override var onPulled: Consumer<ClipboardPush>? = null
    }
}