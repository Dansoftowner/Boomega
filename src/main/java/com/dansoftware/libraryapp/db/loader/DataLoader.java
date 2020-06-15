package com.dansoftware.libraryapp.db.loader;

import com.dansoftware.libraryapp.db.Database;

import java.util.List;

import static java.util.Objects.isNull;

public abstract class DataLoader<T> {

    private Database database;
    private List<T> cache;

    public DataLoader(Database database) {
        this.database = database;
    }

    protected abstract List<T> load(boolean fromCache);

    public List<T> get(boolean fromCache) {
        if (isNull(this.cache) || !fromCache){
            this.cache = this.load(fromCache);
        }

        return this.cache;
    }

    protected Database getDatabase() {
        return database;
    }
}
