package com.dansoftware.libraryapp.gui.dock.docksystem;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.tool.browser.Browser;
import com.dansoftware.libraryapp.gui.tool.browser.TabWebBrowser;
import com.dansoftware.libraryapp.gui.tool.browser.WebContentRenderer;
import com.dansoftware.libraryapp.gui.tool.browser.WebViewWrapper;
import com.dansoftware.libraryapp.gui.tool.documentviewer.DocumentViewer;
import com.dansoftware.libraryapp.gui.tool.documentviewer.PDFDisplayerWrapper;
import com.dansoftware.libraryapp.gui.tool.documentviewer.TabDocumentViewer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.swing.text.Document;

public class DockSystemTest extends Application {

    static {
        Theme.setDefault(Theme.DARK);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Browser browser = new TabWebBrowser(WebViewWrapper.WEBVIEW_SUPPLIER);
        browser.load("http://google.com");

        DockSystem<Node> dockSystem = new DockSystem<>();
        dockSystem.setCenter(new StackPane(new Label("center")));

        DockNode dockNode = new DockNode(new Rectangle(20, 20, Color.BLUE), "WebBrowser, Left bottom", browser);
        dockNode.setDockSystem(dockSystem);
        dockNode.setDockPosition(DockPosition.LEFT_BOTTOM);
        dockNode.show();

        DocumentViewer documentViewer = new TabDocumentViewer(PDFDisplayerWrapper.PDF_DISPLAYER_SUPPLIER);
        documentViewer.load("JDBC","https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf");

        DockNode dockNode1 = new DockNode(new Rectangle(20,20, Color.RED), "PDF Viewer, Left bottom", documentViewer);
        dockNode1.setDockSystem(dockSystem);
        dockNode1.setDockPosition(DockPosition.LEFT_BOTTOM);
        dockNode1.show();

        DockNode dockNode2 = new DockNode(new Rectangle(20, 20, Color.GREEN), "Right top", new StackPane(new Button("Random00")));
        dockNode2.setDockSystem(dockSystem);
        dockNode2.setDockPosition(DockPosition.RIGHT_TOP);
        dockNode2.show();

        DockNode dockNode3 = new DockNode(new Rectangle(20, 20, Color.GREEN), "Right bottom", new StackPane(new Button("Random111")));
        dockNode3.setDockSystem(dockSystem);
        dockNode3.setDockPosition(DockPosition.RIGHT_BOTTOM);
        dockNode3.show();

        DockNode dockNode4 = new DockNode(new Rectangle(20, 20, Color.GREEN), "BOTTOM LEFT", new StackPane(new Button("BOTTOM LEFT")));
        dockNode4.setDockSystem(dockSystem);
        dockNode4.setDockPosition(DockPosition.BOTTOM_LEFT);
        dockNode4.show();

        DockNode dockNode5 = new DockNode(new Rectangle(20, 20, Color.GREEN), "BOTTOM RIGHT", new StackPane(new Button("BOTTOM RIGHT")));
        dockNode5.setDockSystem(dockSystem);
        dockNode5.setDockPosition(DockPosition.BOTTOM_RIGHT);
        dockNode5.show();

        DockNode dockNode6 = new DockNode(new Rectangle(20, 20, Color.CHOCOLATE), "TOP LEFT", new StackPane(new Button("TOP_LEFT")));
        dockNode6.setDockSystem(dockSystem);
        dockNode6.setDockPosition(DockPosition.TOP_LEFT);
        dockNode6.show();

        DockNode dockNode7 = new DockNode(new Rectangle(20, 20, Color.CHOCOLATE), "TOP RIGHT", new StackPane(new Button("TOP_RIGHT")));
        dockNode7.setDockSystem(dockSystem);
        dockNode7.setDockPosition(DockPosition.TOP_RIGHT);
        dockNode7.show();

        Scene scene = new Scene(dockSystem);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("mannniyy");
    }
}
