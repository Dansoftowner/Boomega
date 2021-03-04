package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.preferences.pane.AppearancePane
import com.dansoftware.boomega.gui.preferences.pane.LanguagePane
import com.dansoftware.boomega.gui.preferences.pane.PreferencesPane
import com.dansoftware.boomega.gui.preferences.pane.UpdatePane
import com.dlsc.workbenchfx.SimpleHeaderView
import com.dlsc.workbenchfx.Workbench
import com.dlsc.workbenchfx.model.WorkbenchModule
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import jfxtras.styles.jmetro.JMetroStyleClass

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
        listOf<PreferencesPane>(
            AppearancePane(preferences),
            LanguagePane(asContext, preferences),
            UpdatePane(preferences)
        ).forEach(this::addPane)
    }

    private fun addPane(prefPane: PreferencesPane) {
        tabPane.tabs.add(Tab(prefPane.title).apply {
            isClosable = false
            graphic = prefPane.graphic
            content = ScrollPane(prefPane).apply {
                isFitToHeight = true
                isFitToWidth = true
            }
        })
    }
}