package com.dansoftware.libraryapp.db.pojo;

import java.util.Objects;

/**
 * This abstract class represents a database record
 * that has an id.
 */
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
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() == obj.getClass()) {
            Record record = (Record) obj;
            return this.id == record.id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName(), id);
    }
}
