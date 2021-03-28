package com.dansoftware.boomega.gui.googlebooks

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.control.TwoSideToolBar
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.SystemBrowser
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import java.text.Collator
import java.util.*
import java.util.function.Supplier

class GoogleBooksImportToolBar(
    private val view: GoogleBooksImportView
) : TwoSideToolBar() {

    private lateinit var columnChooserItem: MenuButton
    private lateinit var abcChooserItem: MenuButton

    private val abcLocale: ObjectProperty<Locale> =
        object : SimpleObjectProperty<Locale>(Locale.getDefault()) {
            override fun invalidated() {
                abcChooserItem.items
                    .map { it as AbcMenuItem }
                    .filter { value == it.locale }
                    .forEach { it.isSelected = true }
            }
        }

    init {
        buildUI()
    }

    fun abcLocaleProperty() = abcLocale

    fun updateColumnChooser() {
        columnChooserItem.items
            .map { it as TableColumnMenuItem }
            .forEach {
                it.isSelected = view.table.isColumnShown(it.columnType)
            }
    }

    private fun buildUI() {
        this.rightItems.add(buildRefreshItem())
        this.rightItems.add(buildScrollToTopItem())
        this.rightItems.add(buildClearItem())
        this.rightItems.add(buildBrowserItem())
        this.leftItems.add(buildGoogleLogoItem())
        this.leftItems.add(buildColumnChooserItem().also { columnChooserItem = it })
        this.leftItems.add(buildColumnResetItem())
        this.leftItems.add(buildABCChooserItem().also { abcChooserItem = it })
    }

    private fun buildGoogleLogoItem() =
        StackPane(
            Group(
                HBox(5.0,
                    ImageView("/com/dansoftware/boomega/image/util/google_24px.png"),
                    Label("Google Books").run {
                        font = Font.font(20.0)
                        StackPane(this)
                    }
                )
            )
        )

    private fun buildRefreshItem() =
        buildToolbarItem(MaterialDesignIcon.REFRESH, "google.books.toolbar.refresh") { view.refresh() }

    private fun buildScrollToTopItem() =
        buildToolbarItem(MaterialDesignIcon.BORDER_TOP, "google.books.toolbar.scrolltop") { view.scrollToTop() }

    private fun buildBrowserItem() =
        buildToolbarItem(MaterialDesignIcon.GOOGLE_CHROME, "google.books.toolbar.website") {
            SystemBrowser.browse("https://books.google.com/advanced_book_search")
        }

    private fun buildClearItem() =
        buildToolbarItem(MaterialDesignIcon.DELETE, "google.books.toolbar.clear") { view.clear() }

    private fun buildColumnChooserItem() =
        MenuButton(
            I18N.getValue("google.books.toolbar.columns"),
            FontAwesomeIconView(FontAwesomeIcon.COLUMNS)
        ).also { toolbarItem ->
            GoogleBooksTable.ColumnType.values()
                .map(::TableColumnMenuItem)
                .forEach { toolbarItem.items.add(it) }
        }

    private fun buildColumnResetItem() =
        buildToolbarItem(MaterialDesignIcon.TABLE, "google.books.toolbar.colreset") {
            view.table.buildDefaultColumns()
            columnChooserItem.items
                .map { it as TableColumnMenuItem }
                .forEach {
                    it.isSelected = it.columnType.isDefaultVisible
                }
        }

    private fun buildABCChooserItem() =
        MenuButton(I18N.getValue("google.books.abc")).also { toolbarItem ->
            val toggleGroup = ToggleGroup()
            I18N.getAvailableCollators().forEach { (locale, collatorSupplier) ->
                toolbarItem.items.add(
                    AbcMenuItem(
                        locale,
                        collatorSupplier,
                        toggleGroup
                    )
                )
            }
        }

    private fun buildToolbarItem(icon: MaterialDesignIcon, i18nTooltip: String, onClick: EventHandler<MouseEvent>) =
        Button(null, MaterialDesignIconView(icon)).apply {
            tooltip = Tooltip(I18N.getValue(i18nTooltip))
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            setOnAction { onClick.handle(null) }
        }

    private inner class AbcMenuItem(
        val locale: Locale,
        collatorSupplier: Supplier<Collator?>,
        toggleGroup: ToggleGroup
    ) :
        RadioMenuItem(locale.displayLanguage, MaterialDesignIconView(MaterialDesignIcon.TRANSLATE)) {
        init {
            userData = locale
            selectedProperty().addListener { _, _, selected: Boolean ->
                if (selected) {
                    @Suppress("UNCHECKED_CAST")
                    view.table.setSortingComparator(collatorSupplier.get() as Comparator<String>)
                    abcLocale.set(locale)
                }
            }
            setToggleGroup(toggleGroup)
        }
    }

    private inner class TableColumnMenuItem(val columnType: GoogleBooksTable.ColumnType) :
        CheckMenuItem(I18N.getValue(columnType.i18Nkey)) {
        init {
            setOnAction {
                when {
                    this.isSelected.not() -> view.table.removeColumn(columnType)
                    else -> {
                        view.table.removeAllColumns()
                        columnChooserItem.items.stream()
                            .map { it as TableColumnMenuItem }
                            .filter { it.isSelected }
                            .map { it.columnType }
                            .forEach(view.table::addColumn)
                    }
                }
            }
        }
    }
}