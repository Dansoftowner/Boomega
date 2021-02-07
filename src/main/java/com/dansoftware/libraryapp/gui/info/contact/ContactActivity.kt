package com.dansoftware.libraryapp.gui.info.contact

import com.dansoftware.libraryapp.gui.context.Context

class ContactActivity(private val context: Context) {
    fun show() {
        context.showOverlay(ContactOverlay())
    }
}