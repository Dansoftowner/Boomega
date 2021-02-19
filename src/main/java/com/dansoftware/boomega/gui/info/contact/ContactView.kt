package com.dansoftware.boomega.gui.info.contact

import com.dansoftware.boomega.gui.context.TitledOverlayBox
import com.dansoftware.boomega.gui.util.HighlightableLabel
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

class ContactOverlay() : TitledOverlayBox(
    I18N.getValues().getString("contact.view.title"),
    MaterialDesignIconView(MaterialDesignIcon.CONTACT_MAIL),
    ContactView(),
    false,
    false
)

class ContactView() : VBox(5.0) {

    init {
        this.padding = Insets(5.0)
        this.styleClass.add(JMetroStyleClass.BACKGROUND)
        this.children.apply {
            add(buildEntry(MaterialDesignIcon.EMAIL, ContactValues.EMAIL))
            add(buildEntry(MaterialDesignIcon.TWITTER, ContactValues.TWITTER))
            add(buildEntry(MaterialDesignIcon.GITHUB_CIRCLE, ContactValues.GITHUB))
        }
    }

    private fun buildEntry(icon: MaterialDesignIcon, content: String): Node =
        buildEntry(MaterialDesignIconView(icon), HighlightableLabel(content))

    private fun buildEntry(icon: Node, content: Node): Node =
        HBox(5.0).apply {
            children.add(icon)
            children.add(content)
        }

}