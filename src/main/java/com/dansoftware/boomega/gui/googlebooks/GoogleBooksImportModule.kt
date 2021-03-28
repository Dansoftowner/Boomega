package com.dansoftware.boomega.gui.googlebooks

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.model.WorkbenchModule
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

/**
 * The [GoogleBooksImportModule] is a module that allows to search books
 * through the Google Books service and also allows to import books and add them into
 * the local database.
 *
 * @author Daniel Gyorffy
 */
class GoogleBooksImportModule(
    private val context: Context,
    private val preferences: Preferences
) : WorkbenchModule(I18N.getValue("google.books.import.module.title"), MaterialDesignIcon.GOOGLE) {

    private val content: ObjectProperty<GoogleBooksImportView> = SimpleObjectProperty()

    override fun activate(): Node {
        if (content.get() == null)
            content.set(GoogleBooksImportView(context, preferences))
        return content.get()
    }

    override fun destroy(): Boolean {
        content.get().writeConfig()
        content.set(null)
        return true
    }
}