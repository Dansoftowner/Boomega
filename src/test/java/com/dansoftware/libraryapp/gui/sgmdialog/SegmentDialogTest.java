package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class SegmentDialogTest extends Application {

    private static final Logger logger = LoggerFactory.getLogger(SegmentDialogTest.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        SegmentDialog segmentDialog =
                new SegmentDialog(
                        ResourceBundle.getBundle("com.dansoftware.libraryapp.gui.sgmdialog.TestBundle"),
                        new SequentSequenceImpl(
                                new Segment("Segment 1", new Label("Sg One")),
                                new Segment("Segment 2", new Label("Sg Two")),
                                new Segment("Segment 3", new Label("Sg Three"))
                        )
                );

        Scene scene = new Scene(segmentDialog);
        scene.getStylesheets().add("/com/dansoftware/libraryapp/gui/sgmdialog/stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static final class SequentSequenceImpl extends SegmentSequence {

        public SequentSequenceImpl(Segment... segments) {
            super(segments);
        }

        @Override
        protected void onSegmentsFinished() {
            logger.debug("Segment finished");
        }
    }
}
