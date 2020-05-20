package com.dansoftware.libraryapp.gui.dock;

import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import com.dansoftware.libraryapp.gui.dock.docksystem.DockSystem;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.main.Globals;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public enum ViewMode {
    PINNED("dock.options.view.docked"){
        @Override
        public void show(DockNode dockNode) {
            DockSystem<?> dockSystem = dockNode.getDockSystem();
            dockSystem.dock(dockNode.getDockPosition(), dockNode);
        }

        @Override
        public void hide(DockNode dockNode) {
            DockSystem<?> dockSystem = dockNode.getDockSystem();
            dockSystem.hide(dockNode.getDockPosition(), dockNode);
        }
    }, FLOAT("dock.options.view.float") {

        private Stage floatingStage;
        private Pane root;

        @Override
        public void show(DockNode dockNode) {
            boolean case1 = !dockNode.isShowing();
            boolean case2 = dockNode.isShowing() && dockNode.getViewMode() != this;

            dockNode.getDockTitleBar().applyFloating();

            if (case1 || case2) {
                Scene scene = new Scene(root = new StackPane(dockNode));
                Theme.applyDefault(scene);

                floatingStage = new Stage();
                floatingStage.setScene(scene);
                floatingStage.initStyle(StageStyle.UNDECORATED);
                floatingStage.getIcons().add(Globals.WINDOW_ICON);
                floatingStage.titleProperty().bind(dockNode.titleProperty());
                floatingStage.show();
            }
        }

        @Override
        public void hide(DockNode dockNode) {
            if (floatingStage != null) {
                root.getChildren().remove(dockNode);
                floatingStage.close();
                dockNode.getDockTitleBar().disableFloating();
            }

            floatingStage = null;
        }

    }, WINDOW("dock.options.view.window") {

        private Stage stage;
        private Pane root;

        @Override
        public void show(DockNode dockNode) {
            boolean case1 = !dockNode.isShowing();
            boolean case2 = dockNode.isShowing() && dockNode.getViewMode() != this;

            if (case1 || case2) {
                Scene scene = new Scene(root = new StackPane(dockNode));
                Theme.applyDefault(scene);

                stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.DECORATED);
                stage.getIcons().add(Globals.WINDOW_ICON);
                stage.titleProperty().bind(dockNode.titleProperty());
                stage.show();
            }
        }

        @Override
        public void hide(DockNode dockNode) {
            if (stage != null) {
                root.getChildren().remove(dockNode);
                stage.close();
                dockNode.getDockTitleBar().disableFloating();
            }

            stage = null;
        }
    };

    public static final String NAME_LOCALE_KEY = "dock.options.view.mode";

    private final String id;
    private final String localeKey;

    ViewMode(String localeKey) {
        this.id = this.toString().toLowerCase();
        this.localeKey = localeKey;
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public String getId() {
        return id;
    }

    public abstract void show(DockNode dockNode);
    public abstract void hide(DockNode dockNode);
}