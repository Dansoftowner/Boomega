package com.dansoftware.libraryapp.db.loader;

import com.dansoftware.libraryapp.db.Database;

import java.util.List;

import static java.util.Objects.isNull;

public abstract class DataLoader<D extends Database, T> {

    private final D database;
    private List<T> cache;

    public DataLoader(D database) {
        this.database = database;
    }

    protected abstract List<T> load();

    public List<T> get(boolean fromCache) {
        if (isNull(this.cache) || !fromCache){
            this.cache = this.load();
        }

        return this.cache;
    }

    protected D getDatabase() {
        return database;
    }
}
