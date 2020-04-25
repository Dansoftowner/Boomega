package com.dansoftware.libraryapp.db.pojo;

public class Publisher extends Record {

    private String name;

    public Publisher() {
    }

    public Publisher(String name) {
        this.name = name;
    }

    public Publisher(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
