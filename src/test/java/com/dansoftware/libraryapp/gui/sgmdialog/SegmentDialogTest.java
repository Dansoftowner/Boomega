package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class SegmentDialogTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SegmentDialog segmentDialog =
                new SegmentDialog(new BundleImpl(Map.of(
                        "segment.dialog.button.next", "Next",
                        "segment.dialog.button.finish", "Finish",
                        "segment.dialog.button.prev", "Previous"
                )), new SequentSequenceImpl(
                        new Segment("NA 1", new Label("Egy")),
                        new Segment("Na 2", new Label("Ketto")))
                );

        primaryStage.setScene(new Scene(segmentDialog));
        primaryStage.show();
    }

    private static final class SequentSequenceImpl extends SegmentSequence {

        public SequentSequenceImpl(Segment... segments) {
            super(segments);
        }

        @Override
        protected void onSegmentsFinished() {

        }
    }

    private static final class BundleImpl extends ResourceBundle {

        private final Map<String, String> map;

        BundleImpl(Map<String, String> map) {
            this.map = map;
        }

        @Override
        protected Object handleGetObject(@NotNull String key) {
            return map.get(key);
        }

        @NotNull
        @Override
        public Enumeration<String> getKeys() {
            return new Enumeration<String>() {
                private Iterator<String> iterator = map.keySet().iterator();

                @Override
                public boolean hasMoreElements() {
                    return iterator.hasNext();
                }

                @Override
                public String nextElement() {
                    return iterator.next();
                }
            };
        }
    }
}
