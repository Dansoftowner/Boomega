package com.dansoftware.boomega.gui.control

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.text.Font

open class FixedFontMaterialDesignIconView(icon: MaterialDesignIcon, size: Double) : MaterialDesignIconView(icon) {
    init {
        Font.font("Material Design Icons", size).let {
            this.fontProperty().addListener { _, _, _ -> this.font = it }
            this.font = it
        }
    }
}