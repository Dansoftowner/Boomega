package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.SQLException;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DataStorage {

    private DataPackage dataPackage;
    private final DBConnection dbConnection;

    private Task<DataPackage> dataLoaderTask;

    public DataStorage(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }


    public synchronized Task<DataPackage> getDataPackage() {
        if (isNull(this.dataLoaderTask)) {
            this.dataLoaderTask = dbConnection.loadAllData();
            this.dataLoaderTask.valueProperty().addListener((var observable, var oldValue, var newValue) -> {
                if (nonNull(newValue) && isNull(this.dataPackage)) {
                    this.dataPackage = newValue;
                }
            });

            return this.dataLoaderTask;
        } else if (this.dataLoaderTask.isRunning()) {
            return this.dataLoaderTask;
        }

        return new Task<>() {
            @Override
            protected DataPackage call() {
                return dataPackage;
            }
        };
    }

    public DBConnection getDBConnection() {
        return dbConnection;
    }
}
