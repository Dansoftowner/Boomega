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
