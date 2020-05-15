package com.dansoftware.libraryapp.gui.notification;

public class MessageBuilder {
    private String msg;
    private Object[] args;

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

    public static MessageBuilder create(String msg) {
        var instance = new MessageBuilder();
        instance.msg = msg;
        return instance;
    }

    public MessageBuilder msg(String msg) {
        this.msg = msg;
        return this;
    }

    public MessageBuilder args(Object[] args) {
        this.args = args;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Object[] getArgs() {
        return args;
    }
}
