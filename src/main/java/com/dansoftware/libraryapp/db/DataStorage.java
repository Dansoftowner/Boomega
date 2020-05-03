package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.SQLException;

public class DataStorage implements AbstractDataStorage {

    private DataPackage dataPackage;
    private AbstractDBConnection dbConnection;

    public DataStorage(AbstractDBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public DataPackage getDataPackage() throws SQLException {
        if (dataPackage == null) {
            dataPackage = dbConnection.loadAllData();
        }

        return dataPackage;
    }
}
