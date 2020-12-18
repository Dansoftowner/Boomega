package com.dansoftware.libraryapp.gui.login.quick

import com.dansoftware.libraryapp.db.DatabaseMeta
import com.dansoftware.libraryapp.gui.context.ContextTransformable
import com.dansoftware.libraryapp.gui.login.DatabaseLoginListener

class QuickLoginActivity(private val databaseMeta: DatabaseMeta, loginListener: DatabaseLoginListener) :
    ContextTransformable {

    private val quickLoginView: QuickLoginView = QuickLoginView(databaseMeta, loginListener)

    fun show() = QuickLoginWindow(quickLoginView, databaseMeta).show()

    override fun getContext() = quickLoginView.context
}