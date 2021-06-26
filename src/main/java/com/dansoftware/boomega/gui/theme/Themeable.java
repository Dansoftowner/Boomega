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

package com.dansoftware.boomega.gui.theme;

import org.jetbrains.annotations.NotNull;

/**
 * A Themeable can handle the theme apply-requests.
 *
 * @author Daniel Gyorffy
 */
public interface Themeable {

    /**
     * Called when a new {@link Theme} is set default
     *
     * @param oldTheme the object that represents the old theme
     * @param newTheme the theme object
     */
    void handleThemeApply(@NotNull Theme oldTheme, @NotNull Theme newTheme);

    default void handleThemeApply(@NotNull Theme newTheme) {
        handleThemeApply(Theme.empty(), newTheme);
    }
}
