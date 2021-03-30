package com.dansoftware.boomega.gui.record.add

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.control.TwoSideToolBar
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.control.*

class RecordAddToolbar(private val view: RecordAddView) : TwoSideToolBar() {

    private lateinit var typeChooserItem: MenuButton

    init {
        buildUI()
    }

    private fun buildUI() {
        rightItems.add(buildClearItem())
        leftItems.add(buildRecordTypeChooserItem())
    }

    private fun buildClearItem(): Button =
        Button(null, MaterialDesignIconView(MaterialDesignIcon.DELETE)).apply {
            //TODO: tooltip
            setOnAction {
                //TODO: confirmation dialog before clearing
                view.clearForm()
            }
        }

    private fun buildRecordTypeChooserItem(): MenuButton =
        MenuButton().also { menuButton ->
            typeChooserItem = menuButton

            ToggleGroup().also { tglGroup ->
                tglGroup.selectedToggleProperty().addListener { _, _, toggle ->
                    (toggle as MenuItem).apply {
                        menuButton.text = text
                        menuButton.graphic = MaterialDesignIconView(userData as MaterialDesignIcon)
                    }
                }

                fun createItem(i18n: String, recordType: Record.Type, icon: MaterialDesignIcon) {
                    menuButton.items.add(RadioMenuItem(I18N.getValue(i18n), MaterialDesignIconView(icon)).apply {
                        userData = icon
                        toggleGroup = tglGroup
                        isSelected = view.recordType == recordType
                        setOnAction { view.recordType = recordType }
                    })
                }

                createItem(
                    "record.add.rectype.book",
                    Record.Type.BOOK,
                    MaterialDesignIcon.BOOK
                )
                createItem(
                    "record.add.rectype.magazine",
                    Record.Type.MAGAZINE,
                    MaterialDesignIcon.NEWSPAPER
                )
            }
        }

}