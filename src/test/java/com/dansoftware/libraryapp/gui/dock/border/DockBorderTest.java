package com.dansoftware.libraryapp.gui.dock.border;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DockBorderTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderButton borderButton = new BorderButton(new Rectangle(20, 20), "BorderButton1");
        BorderButton borderButton2 = new BorderButton(new Rectangle(20, 20), "BorderButton2");
        BorderButton borderButton3 = new BorderButton(new Rectangle(20, 20), "BorderButton3");
        BorderButton borderButton4 = new BorderButton(new Rectangle(20, 20), "BorderButton4");
        BorderButton borderButton5 = new BorderButton(new Rectangle(20, 20), "BorderButton5");
        BorderButton borderButton6 = new BorderButton(new Rectangle(20, 20), "BorderButton6");
        BorderButton borderButton7 = new BorderButton(new Rectangle(20, 20), "BorderButton7");
        BorderButton borderButton8 = new BorderButton(new Rectangle(20, 20), "BorderButton8");
        BorderButton borderButton9 = new BorderButton(new Rectangle(20, 20), "BorderButton9");
        BorderButton borderButton10 = new BorderButton(new Rectangle(20, 20), "BorderButton10");
        BorderButton borderButton11 = new BorderButton(new Rectangle(20, 20), "BorderButton11");
        BorderButton borderButton12 = new BorderButton(new Rectangle(20, 20), "BorderButton12");
        BorderButton borderButton13 = new BorderButton(new Rectangle(20, 20), "BorderButton13");
        BorderButton borderButton14 = new BorderButton(new Rectangle(20, 20), "BorderButton14");
        BorderButton borderButton15 = new BorderButton(new Rectangle(20, 20), "BorderButton15");
        BorderButton borderButton16 = new BorderButton(new Rectangle(20, 20), "BorderButton16");

        DockBorder dockBorder = new DockBorder();
        dockBorder.allocate(DockPosition.LEFT_TOP, borderButton);
        dockBorder.allocate(DockPosition.LEFT_TOP, borderButton2);
        dockBorder.allocate(DockPosition.LEFT_BOTTOM, borderButton3);
        dockBorder.allocate(DockPosition.LEFT_BOTTOM, borderButton4);
        dockBorder.allocate(DockPosition.RIGHT_TOP, borderButton5);
        dockBorder.allocate(DockPosition.RIGHT_TOP, borderButton6);
        dockBorder.allocate(DockPosition.RIGHT_BOTTOM, borderButton7);
        dockBorder.allocate(DockPosition.RIGHT_BOTTOM, borderButton8);
        dockBorder.allocate(DockPosition.TOP_LEFT, borderButton9);
        dockBorder.allocate(DockPosition.TOP_LEFT, borderButton10);
        dockBorder.allocate(DockPosition.TOP_RIGHT, borderButton11);
        dockBorder.allocate(DockPosition.TOP_RIGHT, borderButton12);
        dockBorder.allocate(DockPosition.BOTTOM_LEFT, borderButton13);
        dockBorder.allocate(DockPosition.BOTTOM_LEFT, borderButton14);
        dockBorder.allocate(DockPosition.BOTTOM_RIGHT, borderButton15);
        dockBorder.allocate(DockPosition.BOTTOM_RIGHT, borderButton16);

        System.out.println(dockBorder.getLeft());

        Scene scene = new Scene(dockBorder);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
