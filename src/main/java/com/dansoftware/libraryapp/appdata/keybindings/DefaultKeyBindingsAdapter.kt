package com.dansoftware.libraryapp.appdata.keybindings

import com.google.gson.*
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import java.lang.reflect.Type


class DefaultKeyBindingsAdapter() : JsonSerializer<DefaultKeyBindings>, JsonDeserializer<DefaultKeyBindings> {

    @Suppress("UNCHECKED_CAST")
    override fun serialize(
        src: DefaultKeyBindings?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = src?.let {
        JsonObject().apply {
            src.javaClass.declaredFields
                .filter { it.type == SimpleObjectProperty::class.java }
                .forEach {
                    this.addProperty(
                        it.name,
                        it.apply { isAccessible = true }.get(src)
                            .run { this as SimpleObjectProperty<KeyCodeCombination> }
                            .get()
                            .toString()
                    )
                }
        }
    } ?: JsonNull.INSTANCE

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ) = DefaultKeyBindings.apply {
        this.javaClass.declaredFields
            .filter { it.type == SimpleObjectProperty::class.java }
//            .map { it.apply { isAccessible = true }.get(this) }
//            .map { it as ObjectProperty<KeyCombination> }
            .forEach { field ->
                field.apply { isAccessible = true }
                    .get(this)
                    .let { it as ObjectProperty<KeyCombination> }
                    .let { objectProperty ->
                        objectProperty.set(
                            json?.asJsonObject?.get(field.name)?.asString?.let { KeyCodeCombination.valueOf(it) }
                                ?: objectProperty.get()
                        )
                    }
            }
    }
}