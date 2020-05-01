package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.AbstractDataStorage;
import com.dansoftware.libraryapp.db.DBConnection;
import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.SQLException;

public class DataStorage implements AbstractDataStorage {

    private DataPackage dataPackage;

    @Override
    public DataPackage getDataPackage() throws SQLException {
        if (dataPackage == null) {
            dataPackage = DBConnection.getInstance().loadAllData();
        }

        return dataPackage;
    }
}
