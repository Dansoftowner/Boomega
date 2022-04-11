/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.util

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType

/**
 * Provides internationalized [ButtonType] constants for the app.
 *
 * @author Daniel Gyorffy
 */
object I18NButtonTypes {
    @JvmField
    val APPLY = createButtonType("Dialog.apply.button", ButtonBar.ButtonData.APPLY)

    @JvmField
    val OK = createButtonType("Dialog.ok.button", ButtonBar.ButtonData.OK_DONE)

    @JvmField
    val CANCEL = createButtonType("Dialog.cancel.button", ButtonBar.ButtonData.CANCEL_CLOSE)

    @JvmField
    val CLOSE = createButtonType("Dialog.close.button", ButtonBar.ButtonData.CANCEL_CLOSE)

    @JvmField
    val YES = createButtonType("Dialog.yes.button", ButtonBar.ButtonData.YES)

    @JvmField
    val NO = createButtonType("Dialog.no.button", ButtonBar.ButtonData.NO)

    @JvmField
    val FINISH = createButtonType("Dialog.finish.button", ButtonBar.ButtonData.FINISH)

    @JvmField
    val NEXT = createButtonType("Dialog.next.button", ButtonBar.ButtonData.NEXT_FORWARD)

    @JvmField
    val PREVIOUS = createButtonType("Dialog.previous.button", ButtonBar.ButtonData.BACK_PREVIOUS)

    @JvmField
    val RETRY = createButtonType("Dialog.retry.button", ButtonBar.ButtonData.YES)

    private fun createButtonType(key: String, buttonData: ButtonBar.ButtonData) =
        ButtonType(com.dansoftware.boomega.i18n.api.I18N.getValues().getString(key), buttonData)
}