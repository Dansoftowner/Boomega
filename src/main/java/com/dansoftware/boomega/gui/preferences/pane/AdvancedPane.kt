/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.concurrent.Task
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.ContentDisplay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AdvancedPane(private val context: Context, preferences: Preferences) : PreferencesPane(preferences) {

    override val title: String = I18N.getValue("preferences.tab.advanced")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.PALETTE_ADVANCED)

    override fun buildContent(): Content =
        object : Content() {

            init {
                buildItems()
            }

            private fun buildItems() {
                items.add(buildGCControl())
                items.add(buildResetControl())
            }

            private fun buildResetControl(): PreferencesControl =
                PairControl(
                    I18N.getValue("preferences.advanced.reset"),
                    I18N.getValue("preferences.advanced.reset.desc"),
                    buildResetButton()
                )

            private fun buildResetButton() =
                Button().apply {
                    contentDisplay = ContentDisplay.GRAPHIC_ONLY
                    graphic = MaterialDesignIconView(MaterialDesignIcon.DELETE_FOREVER)
                    setOnAction {
                        context.showConfirmationDialog(
                            I18N.getValue("preferences.advanced.reset.confirm.title"),
                            I18N.getValue("preferences.advanced.reset.confirm.msg")
                        ) {
                            if (it.typeEquals(ButtonType.YES)) {
                                reset()
                            }
                        }
                    }
                }


            private fun buildGCControl(): PreferencesControl =
                PairControl(
                    I18N.getValue("preferences.advanced.gc"),
                    I18N.getValue("preferences.advanced.gc.desc"),
                    buildGCButton()
                )

            private fun buildGCButton() =
                Button("System.gc()").apply {
                    setOnAction {
                        System.gc()
                        logger.debug("Garbage collection request made.")
                    }
                }

            private fun reset() {
                CachedExecutor.submit(object : Task<Unit>() {

                    init {
                        setOnRunning { context.showIndeterminateProgress() }
                        setOnFailed {
                            context.stopProgress()
                            logger.error("Couldn't reset the application", it.source.exception)
                            // TODO: error dialog
                        }
                        setOnSucceeded {
                            ApplicationRestart().restartApp()
                        }
                    }

                    override fun call() {
                        preferences.editor().reset()
                    }
                })
            }
        }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AdvancedPane::class.java)
    }
}