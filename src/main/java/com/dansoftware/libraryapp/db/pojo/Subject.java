package com.dansoftware.libraryapp.db.pojo;

public class Subject extends Record {

    private String name;

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    public Subject(int id, String name) {
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
