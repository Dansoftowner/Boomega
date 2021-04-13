package com.dansoftware.boomega.config.logindata;

import com.dansoftware.boomega.config.ConfigAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

public class LoginDataAdapter implements ConfigAdapter<LoginData> {

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
