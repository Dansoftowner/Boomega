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

@file:JvmName("MenuUtils")

package com.dansoftware.boomega.gui.util

import javafx.beans.property.ObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCombination

/**
 * Sets the action of the [MenuItem] and then returns the object itself
 */
fun <M : MenuItem> M.action(onAction: EventHandler<ActionEvent>): M = this.also { this.onAction = onAction }

/**
 * Sets the key combination of the [MenuItem] and then returns the object itself
 */
fun <M : MenuItem> M.keyCombination(combination: KeyCombination): M = this.also { it.accelerator = combination }

/**
 * Binds the key combination property of the [MenuItem] to the given property and then returns the object itself
 */
fun <M : MenuItem, T : KeyCombination> M.keyCombination(combination: ObjectProperty<T>) =
    this.apply { acceleratorProperty().bind(combination) }

/**
 * Sets the icon of the [MenuItem] and then returns the object itself
 */
fun <M : MenuItem> M.graphic(iconStyleClass: String): M = this.also { it.graphic = icon(iconStyleClass) }

/**
 * Adds a sub menu item into the [Menu] and then returns the object itself
 */
fun <M : Menu> M.menuItem(item: MenuItem): M = this.also { items.add(item) }

/**
 * Adds a [SeparatorMenuItem] into the [Menu] and then returns the object itself
 */
fun <M : Menu> M.separator(): M = this.also { this.items.add(SeparatorMenuItem()) }