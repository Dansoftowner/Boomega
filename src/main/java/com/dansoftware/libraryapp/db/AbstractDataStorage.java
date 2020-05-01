package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.SQLException;

public interface AbstractDataStorage {
    DataPackage getDataPackage() throws SQLException;
}
