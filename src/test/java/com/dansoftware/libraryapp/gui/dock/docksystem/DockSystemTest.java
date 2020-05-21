package com.dansoftware.libraryapp.gui.dock.docksystem;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.tool.browser.Browser;
import com.dansoftware.libraryapp.gui.tool.browser.TabWebBrowser;
import com.dansoftware.libraryapp.gui.tool.browser.WebContentRenderer;
import com.dansoftware.libraryapp.gui.tool.browser.WebViewWrapper;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DockSystemTest extends Application {

    static {
        Theme.setDefault(Theme.DARK);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Browser browser = new TabWebBrowser(WebViewWrapper.WEBVIEW_SUPPLIER);
        browser.load("http://google.com");

        DockSystem<Node> dockSystem = new DockSystem<>();

        DockNode dockNode = new DockNode(new Rectangle(20, 20, Color.BLUE), "WebBrowser", browser);
        dockNode.setDockSystem(dockSystem);
        dockNode.setDockPosition(DockPosition.LEFT_BOTTOM);
        dockNode.show();

        //dockSystem.dock(DockPosition.RIGHT_TOP, new DockNode(null , "Panel2", new StackPane(new Button("Panel2"))));
        //dockSystem.dock(DockPosition.RIGHT_BOTTOM, new DockNode(null , "WebBrowser", browser));

        Scene scene = new Scene(dockSystem);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
