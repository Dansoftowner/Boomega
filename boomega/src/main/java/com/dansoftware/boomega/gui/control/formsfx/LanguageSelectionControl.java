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

package com.dansoftware.boomega.gui.control.formsfx;

import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.control.TextFieldLanguageSelectorControl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LanguageSelectionControl extends OnActionTextControl {

    private final Context context;

    public LanguageSelectionControl(@NotNull Context context) {
        this(context, null);
    }

    public LanguageSelectionControl(@NotNull Context context, @Nullable EventHandler<ActionEvent> onAction) {
        super(onAction);
        this.context = context;
    }

    @Override
    public void initializeParts() {
        super.initializeParts();
        TextFieldLanguageSelectorControl.applyOnTextField(context, super.editableField);
    }
}
