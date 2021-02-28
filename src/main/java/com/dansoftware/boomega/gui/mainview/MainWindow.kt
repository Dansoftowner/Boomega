package com.dansoftware.boomega.gui.mainview

import com.dansoftware.boomega.gui.window.BaseWindow

private class MainWindow(view: MainView, menuBar: AppMenuBar) :
    BaseWindow<MainView>("${System.getProperty("app.name")} - ${view.openedDatabase}", menuBar, view) {
    init {
        this.isMaximized = true
        this.exitDialog = true
        this.minWidth = 530.0
        this.minHeight = 530.0
    }
}