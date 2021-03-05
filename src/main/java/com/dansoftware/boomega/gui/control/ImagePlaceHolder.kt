package com.dansoftware.boomega.gui.control

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.scene.text.Font

open class ImagePlaceHolder(size: Double) : FixedFontMaterialDesignIconView(MaterialDesignIcon.IMAGE, size) {
    init {
        this.font = Font.font("Material Design Icons", size)
        this.styleClass.add("glyph-icon")
    }
}