package com.dansoftware.libraryapp.gui.mainview

import com.dansoftware.libraryapp.gui.window.BaseWindow

private class MainWindow(view: MainView) :
    BaseWindow<MainView>("window.main.title", "-", view.openedDatabase.toString(), view) {
    init {
        this.isMaximized = true
        this.exitDialog = true
        this.minWidth = 530.0
        this.minHeight = 530.0
    }
}