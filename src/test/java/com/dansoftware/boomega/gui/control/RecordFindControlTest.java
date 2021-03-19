package com.dansoftware.boomega.gui.control;

import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.record.show.RecordTable;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class RecordFindControlTest extends Application {

    private final ObservableList<Record> records = FXCollections.observableArrayList(
            new Record.Builder(Record.Type.BOOK)
                    .authors(List.of("A", "B"))
                    .title("Abc1")
                    .publisher("Pub1")
                    .isbn(String.valueOf(Math.random()))
                    .build(),
            new Record.Builder(Record.Type.BOOK)
                    .title("Abc2")
                    .publisher("Pub1")
                    .isbn(String.valueOf(Math.random()))
                    .build(),
            new Record.Builder(Record.Type.BOOK)
                    .title("Bcd")
                    .publisher("Pub2")
                    .isbn(String.valueOf(Math.random()))
                    .build(),
            new Record.Builder(Record.Type.BOOK)
                    .title("Bcd2")
                    .publisher("Pub")
                    .isbn(String.valueOf(Math.random()))
                    .build(),
            new Record.Builder(Record.Type.BOOK)
                    .title("Abcdce1")
                    .publisher("Pub2")
                    .isbn(String.valueOf(Math.random()))
                    .build(),
            new Record.Builder(Record.Type.BOOK)
                    .title("Abcde2")
                    .publisher("XXX")
                    .isbn(String.valueOf(Math.random()))
                    .build()
    );

    @Override
    public void start(Stage primaryStage) throws Exception {
        var control = new RecordFindControl(records);

        RecordTable table = new RecordTable(0);
        table.getItems().addAll(records);
        table.buildDefaultColumns();

        control.setOnNewResults(items -> table.getItems().setAll(items));

        primaryStage.setScene(new Scene(new VBox(5, control, table)));
        primaryStage.show();
    }
}
