package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.context.ContextTransformable
import com.dansoftware.boomega.gui.login.DatabaseLoginListener

class QuickLoginActivity(private val databaseMeta: DatabaseMeta, loginListener: DatabaseLoginListener) :
    ContextTransformable {

    private val quickLoginView: QuickLoginView = QuickLoginView(databaseMeta, loginListener)

    fun show() = QuickLoginWindow(quickLoginView, databaseMeta).show()

    override fun getContext() = quickLoginView.context
}