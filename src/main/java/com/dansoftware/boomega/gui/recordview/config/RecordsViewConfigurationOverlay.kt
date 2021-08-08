/*
 * Boomega
 * Copyright (C)  $originalComment.match("Copyright (\d+)", 1, "-")2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.recordview.config

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.base.TitledOverlayBox
import com.dansoftware.boomega.gui.recordview.RecordsView
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n

class RecordsViewConfigurationOverlay(view: RecordsView, preferences: Preferences) : TitledOverlayBox(
    i18n("record.panel_config"), icon("tune-icon"), RecordsViewConfigurationPanel(view)
)