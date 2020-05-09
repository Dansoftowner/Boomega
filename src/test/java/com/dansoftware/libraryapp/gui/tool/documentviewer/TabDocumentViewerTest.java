package com.dansoftware.libraryapp.gui.tool.documentviewer;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Locale;

/**
 * @deprecated the test cases are too absolute and not works on a different PC
 */
@Deprecated
public class TabDocumentViewerTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Button light = new Button("Light");
        light.setOnAction(e -> Theme.LIGHT.apply(primaryStage.getScene()));

        Button dark = new Button("Dark");
        dark.setOnAction(e -> Theme.DARK.apply(primaryStage.getScene()));

        HBox hBox = new HBox(10, light, dark);

        DocumentViewer documentViewer = new TabDocumentViewer(PDFDisplayerWrapper.PDF_DISPLAYER_SUPPLIER);
        documentViewer.load(new File("C:\\Users\\judal\\Documents\\Jedlik\\GoogleClassroom\\Math\\DOGA\\GyD.pdf"));
        documentViewer.load("Valami lol", new File("C:\\Users\\judal\\Documents\\Java\\SomeJavaMaterial\\Head First Design Patterns by Elisabeth Freeman, Eric Freeman, Bert Bates, Kathy Sierra (z-lib.org).pdf"));

        Scene scene = new Scene(new VBox(10, hBox, documentViewer));

        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
