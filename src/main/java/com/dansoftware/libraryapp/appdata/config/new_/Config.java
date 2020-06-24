package com.dansoftware.libraryapp.appdata.config.new_;

import com.google.gson.JsonObject;

public class Config {

    private JsonObject jsonObject;

    public Config(String path) {
        this.jsonObject = new JsonObject();
    }

    public JsonObject getAsJsonObject() {
        return jsonObject;
    }
}
