package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.appdata.Preferences
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import jfxtras.styles.jmetro.JMetroStyleClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class PreferencesPane(val preferences: Preferences) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PreferencesPane::class.java)
    }

    abstract val title: String
    abstract val graphic: Node

    private var cachedContent: Content? = null

    fun getContent(): Content {
        return cachedContent ?: buildContent().also {
            logger.debug("Building content for ${javaClass.simpleName}...")
            cachedContent = it
        }
    }

    protected abstract fun buildContent(): Content

    open class Content : GridPane() {

        init {
            styleClass.add(JMetroStyleClass.BACKGROUND)
            padding = Insets(10.0)
            hgap = 100.0
            vgap = 20.0
        }

        fun addEntry(title: String, description: String?, region: Region) {
            rowCount.also { row ->
                buildDescriptionPane(title, description)
                    .also { setConstraints(it, 0, row) }
                    .let(children::add)
                region.also {
                    setConstraints(it, 1, row)
                    setHgrow(it, Priority.ALWAYS)
                    region.maxWidth = Double.MAX_VALUE
                }.let(children::add)
            }
        }

        private fun buildDescriptionPane(title: String, description: String?) = VBox(2.0).apply {
            children.add(Label(title).apply { styleClass.add("entry-title") })
            description?.let { children.add(Label(it).apply { styleClass.add("entry-description") }) }
        }
    }
}