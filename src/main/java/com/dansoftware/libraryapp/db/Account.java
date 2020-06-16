package com.dansoftware.libraryapp.db;

public class Account {
    private String username;
    private String password;
    private String filePath;

    public Account() {
    }

    public Account(String username, String password, String filePath) {
        this.username = username;
        this.password = password;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAnonymous() {
        return this.username == null && this.password == null;
    }

    public static Account anonymous() {
        return new Account();
    }
}
