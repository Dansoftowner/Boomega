package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.SQLException;

import static java.util.Objects.isNull;

public class DataStorage implements AbstractDataStorage {

    private DataPackage dataPackage;
    private AbstractDBConnection dbConnection;

    public DataStorage(AbstractDBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public DataPackage getDataPackage() throws SQLException {
        if (isNull(dataPackage)) {
            dataPackage = dbConnection.loadAllData();
        }

        return dataPackage;
    }
}
