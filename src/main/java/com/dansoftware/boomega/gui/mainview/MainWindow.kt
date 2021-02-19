package com.dansoftware.boomega.gui.mainview

import com.dansoftware.boomega.gui.window.BaseWindow

private class MainWindow(view: MainView) :
    BaseWindow<MainView>("app.name", "-", view.openedDatabase.toString(), view) {
    init {
        this.isMaximized = true
        this.exitDialog = true
        this.minWidth = 530.0
        this.minHeight = 530.0
    }
}