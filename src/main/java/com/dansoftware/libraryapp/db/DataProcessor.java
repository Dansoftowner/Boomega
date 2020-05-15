package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;
import com.dansoftware.libraryapp.gui.notification.MessageBuilder;
import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.notification.NotificationLevel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class DataProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

    private static final EventHandler<WorkerStateEvent> LOADING_FAILED_EVENT_HANDLER = event -> {
        Throwable cause = event.getSource().getException();

        LOGGER.error("Couldn't load the data from the database", cause);

        Notification.create()
                .level(NotificationLevel.ERROR)
                .title("db.connection.load.failed.title")
                .cause(cause)
                .msg(MessageBuilder.create("error.details"))
                .show();
    };

    private DataPackage loadedDataPackage;
    private final DBConnection dbConnection;

    private Task<DataPackage> dataLoaderTask;

    public DataProcessor(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Loads the {@link DataPackage} in a separate Thread and calls the given {@link Consumer} with
     * the loaded data-package when it's available.
     *
     * @param dataPackageHandler
     */
    public synchronized void getDataPackage(Consumer<DataPackage> dataPackageHandler) {
        Objects.requireNonNull(dataPackageHandler, "The dataPackageHandler mustn't be null!");

        if (this.dataLoaderTask == null) {
            //getting the task from the database
            this.dataLoaderTask = dbConnection.loadAllData();

            //adding the listener to the task that will handle the data-package
            this.dataLoaderTask.valueProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends DataPackage> observable, DataPackage oldValue, DataPackage newValue) {
                    if (newValue != null) { //handle the dataPackage with the dataPackageHandler object
                        dataPackageHandler.accept(
                                //we also initialize the field
                                DataProcessor.this.loadedDataPackage = newValue
                        );

                        //remove this listener
                        observable.removeListener(this);
                    }
                }
            });

            this.dataLoaderTask.setOnFailed(LOADING_FAILED_EVENT_HANDLER);

            //send the task to the central task manager
            //CentralTaskManager.submit(dataLoaderTask);
        } else if (this.dataLoaderTask.getState() == Worker.State.RUNNING) {
            //if the dataLoaderTask is running at the moment
            this.dataLoaderTask.valueProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends DataPackage> observable, DataPackage oldValue, DataPackage newValue) {
                    if (newValue != null) {
                        dataPackageHandler.accept(newValue);
                        observable.removeListener(this);
                    }
                }
            });
        } else if (this.dataLoaderTask.getState() == Worker.State.SUCCEEDED) {
            dataPackageHandler.accept(loadedDataPackage);
        }
    }

    public synchronized void reloadRequest() {
        this.dataLoaderTask = null;
    }

    public DBConnection getDBConnection() {
        return dbConnection;
    }
}
