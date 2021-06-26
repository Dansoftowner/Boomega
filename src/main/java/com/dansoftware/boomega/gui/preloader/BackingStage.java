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

package com.dansoftware.boomega.gui.preloader;

import com.dansoftware.boomega.util.ReflectionUtils;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class BackingStage extends Stage {

    public BackingStage() {
        super(StageStyle.UTILITY);
        setTitle(StringUtils.EMPTY);
        setOpacity(0);
    }

    private <S extends Stage> S configureStage(S stage) {
        stage.initOwner(this);
        return stage;
    }

    public Stage createChild(@NotNull StageStyle stageStyle) {
        return configureStage(new Stage(stageStyle));
    }

    public Stage createChild() {
        return createChild(StageStyle.DECORATED);
    }

    public <S extends Stage> S createChild(@NotNull Class<S> classRef, Object... args)
            throws ReflectiveOperationException {
        S stage = ReflectionUtils.constructObject(classRef, args);
        return configureStage(stage);
    }
}
