package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.preferences.pane.*
import com.dlsc.workbenchfx.Workbench
import com.dlsc.workbenchfx.model.WorkbenchModule
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image

class PreferencesView(private val preferences: Preferences) : Workbench() {

    private val asContext: Context = Context.from(this)
    private lateinit var tabPane: TabPane

    init {
        styleClass.add("preferences-view")
        initUI()
        initPanes()
    }

    private fun initUI() {
        tabPane = TabPane()
        modules.add(object : WorkbenchModule("", null as Image?) {
            override fun activate(): Node = tabPane
        })
    }

    private fun initPanes() {
        listOf(
            AppearancePane(preferences),
            KeyBindingPane(preferences),
            LanguagePane(asContext, preferences),
            UpdatePane(preferences)
        ).forEach(this::addPane)
    }

    private fun addPane(prefPane: PreferencesPane) {
        tabPane.tabs.add(Tab(prefPane.title).apply {
            isClosable = false
            graphic = prefPane.graphic
            selectedProperty().addListener(object : ChangeListener<Boolean> {
                override fun changed(
                    observable: ObservableValue<out Boolean>,
                    oldValue: Boolean,
                    isSelected: Boolean
                ) {
                    if (isSelected) {
                        this@apply.content = ScrollPane(prefPane.getContent()).apply {
                            isFitToHeight = true
                            isFitToWidth = true
                        }
                        observable.removeListener(this)
                    }
                }
            })
        })
    }
}