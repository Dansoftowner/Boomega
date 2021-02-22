package com.dansoftware.boomega.gui.mainview;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.googlebooks.GoogleBooksImportModule;
import com.dansoftware.boomega.gui.record.add.RecordAddModule;
import com.dansoftware.boomega.gui.record.show.RecordsViewModule;
import com.dlsc.workbenchfx.Workbench;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

public class MainContentView extends Workbench implements ContextTransformable {

    private final Context asContext;
    private final Preferences preferences;
    private final Database database;

    MainContentView(@NotNull Preferences preferences, @NotNull Database database) {
        this.asContext = Context.from(this);
        this.preferences = preferences;
        this.database = database;
        initModules();
        initUiModifications();
    }

    private void initUiModifications() {
        this.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene scene) {
                if (scene != null) {
                    scene.windowProperty().addListener(new ChangeListener<Window>() {
                        @Override
                        public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window window) {
                            if (window != null) {
                                window.showingProperty().addListener(new ChangeListener<Boolean>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean windowShown) {
                                        if (windowShown) {
                                            modifyAddButton();
                                            observable.removeListener(this);
                                        }
                                    }
                                });
                                observable.removeListener(this);
                            }
                        }
                    });
                    observable.removeListener(this);
                }
            }
        });
    }

    private void modifyAddButton() {
        final Button addButton = (Button) MainContentView.this.lookup("#add-button");
        addButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.HOME));
    }

    private void initModules() {
        getModules().add(new GoogleBooksImportModule(asContext, preferences));
        getModules().add(new RecordAddModule(asContext, database));
        getModules().add(new RecordsViewModule(asContext, preferences, database));
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
