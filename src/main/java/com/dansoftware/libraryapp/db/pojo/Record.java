package com.dansoftware.libraryapp.db.pojo;

import java.util.Objects;

public abstract class Record {

    private int id;

    public Record() {
    }

    public Record(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName(), id);
    }
}
