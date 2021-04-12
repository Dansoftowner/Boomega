package com.dansoftware.boomega.config.logindata;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LoginDataAdapter implements JsonDeserializer<LoginData>, JsonSerializer<LoginData> {

    private final LoginDataSerializer serializer;
    private final LoginDataDeserializer deserializer;

    public LoginDataAdapter() {
        this.serializer = new LoginDataSerializer();
        this.deserializer = new LoginDataDeserializer();
    }

    @Override
    public LoginData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserializer.deserialize(json, typeOfT, context);
    }

    @Override
    public JsonElement serialize(LoginData src, Type typeOfSrc, JsonSerializationContext context) {
        return serializer.serialize(src, typeOfSrc, context);
    }
}
