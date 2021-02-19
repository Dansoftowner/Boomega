package com.dansoftware.boomega.gui.info.contact

import com.dansoftware.boomega.gui.context.Context

class ContactActivity(private val context: Context) {
    fun show() {
        context.showOverlay(ContactOverlay())
    }
}