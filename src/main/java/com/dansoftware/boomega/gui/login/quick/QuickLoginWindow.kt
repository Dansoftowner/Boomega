package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

class QuickLoginWindow(content: QuickLoginView, db: DatabaseMeta, owner: Window? = null) :
    BaseWindow<QuickLoginView>("window.login.quick.title", "-", db.toString(), content) {

    init {
        this.centerOnScreen()
        this.initOwner(owner)
        this.initModality(Modality.APPLICATION_MODAL)
        this.height = 230.0
    }
}