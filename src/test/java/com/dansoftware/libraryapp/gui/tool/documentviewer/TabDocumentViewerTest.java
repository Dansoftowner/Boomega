package com.dansoftware.libraryapp.gui.tool.documentviewer;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.CookieManager;

public class TabDocumentViewerTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Button light = new Button("Light");
        light.setOnAction(e -> {
            //Theme.LIGHT.apply(primaryStage.getScene());
            CookieManager.setDefault(null);
            System.gc();

        });

        Button dark = new Button("Dark");
        //dark.setOnAction(e -> Theme.DARK.apply(primaryStage.getScene()));

        HBox hBox = new HBox(10, light, dark);

        DocumentViewer documentViewer = new TabDocumentViewer(PDFDisplayerWrapper.PDF_DISPLAYER_SUPPLIER);
        documentViewer.load("JDBC TUTORIAL", "https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf");
        documentViewer.load("Java tutorial","https://www.tutorialspoint.com/java/java_tutorial.pdf");

        Scene scene = new Scene(new VBox(10, hBox, documentViewer));

        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
