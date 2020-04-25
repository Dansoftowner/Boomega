package com.dansoftware.libraryapp.db.pojo;

public class Author extends Record {

    private String name;
    private String description;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public Author(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Author(int id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
