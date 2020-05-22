package com.dansoftware.libraryapp.gui.dock.border;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DockFrameTest extends Application {

    static {
        Theme.setDefault(Theme.DARK);
    }

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

        DockFrame dockFrame = new DockFrame();
        dockFrame.allocate(DockPosition.LEFT_TOP, borderButton);
        dockFrame.allocate(DockPosition.LEFT_TOP, borderButton2);
        dockFrame.allocate(DockPosition.LEFT_BOTTOM, borderButton3);
        dockFrame.allocate(DockPosition.LEFT_BOTTOM, borderButton4);
        dockFrame.allocate(DockPosition.RIGHT_TOP, borderButton5);
        dockFrame.allocate(DockPosition.RIGHT_TOP, borderButton6);
        dockFrame.allocate(DockPosition.RIGHT_BOTTOM, borderButton7);
        dockFrame.allocate(DockPosition.RIGHT_BOTTOM, borderButton8);
        dockFrame.allocate(DockPosition.TOP_LEFT, borderButton9);
        dockFrame.allocate(DockPosition.TOP_LEFT, borderButton10);
        dockFrame.allocate(DockPosition.TOP_RIGHT, borderButton11);
        dockFrame.allocate(DockPosition.TOP_RIGHT, borderButton12);
        dockFrame.allocate(DockPosition.BOTTOM_LEFT, borderButton13);
        dockFrame.allocate(DockPosition.BOTTOM_LEFT, borderButton14);
        dockFrame.allocate(DockPosition.BOTTOM_RIGHT, borderButton15);
        dockFrame.allocate(DockPosition.BOTTOM_RIGHT, borderButton16);

        System.out.println(dockFrame.getLeft());

        Scene scene = new Scene(dockFrame);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();

        dockFrame.deAllocate(DockPosition.LEFT_TOP, borderButton);
        dockFrame.deAllocate(DockPosition.LEFT_TOP, borderButton2);

        dockFrame.deAllocate(DockPosition.TOP_LEFT, borderButton9);
        dockFrame.deAllocate(DockPosition.TOP_LEFT, borderButton10);
        dockFrame.deAllocate(DockPosition.TOP_LEFT, borderButton11);
        dockFrame.deAllocate(DockPosition.TOP_LEFT, borderButton12);

        //dockFrame.deAllocate(DockPosition.TOP_LEFT, null);
        //dockFrame.allocate(DockPosition.RIGHT_TOP, borderButton16);
    }
}
