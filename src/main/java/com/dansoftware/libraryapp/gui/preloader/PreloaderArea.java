package com.dansoftware.libraryapp.gui.preloader;

import com.dansoftware.libraryapp.util.ReflectionUtils;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.stream.Stream;

public class PreloaderArea {

    private static PreloaderArea preloaderArea;

    private final Stage backingWindow;

    public PreloaderArea() {
        this.backingWindow = createBackingWindow();
    }

    private Stage createBackingWindow() {
        var backing = new Stage(StageStyle.UTILITY);
        backing.setOnCloseRequest(WindowEvent::consume);
        backing.setTitle("Starting LibraryApp");
        backing.setOpacity(0);
        return backing;
    }

    private Stage configureStage(Stage stage) {
        stage.initOwner(backingWindow);
        return stage;
    }

    public Stage getStage(@NotNull StageStyle stageStyle) {
        return configureStage(new Stage(stageStyle));
    }

    public Stage getStage() {
        return getStage(StageStyle.DECORATED);
    }

    public <S extends Stage> Stage getStage(@NotNull Class<S> classRef, Object... args)
            throws ReflectiveOperationException {
        return configureStage(ReflectionUtils.constructObject(classRef, args));
    }


    public static PreloaderArea getPreloaderArea() {
        if (preloaderArea == null)
            preloaderArea = new PreloaderArea();
        return preloaderArea;
    }
}
