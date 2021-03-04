package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.stage.Modality
import javafx.stage.Window

class QuickLoginWindow(content: QuickLoginView, db: DatabaseMeta, owner: Window? = null) :
    BaseWindow(I18N.getValue("window.login.quick.title"), "-", db.toString(), content, { content.context }) {

    init {
        this.centerOnScreen()
        this.initOwner(owner)
        this.initModality(Modality.APPLICATION_MODAL)
        this.height = 230.0
    }
}