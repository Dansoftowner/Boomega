package com.dansoftware.boomega.appdata.keybinding

import com.dansoftware.boomega.gui.keybinding.KeyBinding
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.google.gson.*
import javafx.scene.input.KeyCodeCombination
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Type


class KeyBindingsAdapter() : JsonSerializer<KeyBindings>, JsonDeserializer<KeyBindings> {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(KeyBindingsAdapter::class.java)
    }


    @Suppress("UNCHECKED_CAST")
    override fun serialize(
        src: KeyBindings?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = src?.let {
        JsonObject().apply {
            src.javaClass.declaredFields
                .filter { it.type == KeyBinding::class.java }
                .map { it.apply { isAccessible = true }.get(src) }
                .map { it as KeyBinding }
                .forEach { keyBinding ->
                    keyBinding.takeIf { it.keyCombination != it.defaultKeyCombination }?.let {
                        logger.debug("Writing key combination for '{}': '{}'...", it.id, it.keyCombination)
                        this.addProperty(
                            it.id,
                            it.keyCombination.toString()
                        )
                    }
                }
        }
    } ?: JsonNull.INSTANCE

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ) = KeyBindings.apply {
        this.javaClass.declaredFields
            .filter { it.type == KeyBinding::class.java }
            .forEach { field ->
                field.apply { isAccessible = true }
                    .get(this)
                    .let { it as KeyBinding }
                    .let { keyBinding ->
                        try {
                            keyBinding.keyCombinationProperty.set(
                                json?.asJsonObject?.get(field.name)?.asString?.let {
                                    logger.debug("Parsing key combination for '${keyBinding.id}': '$it'...")
                                    KeyCodeCombination.valueOf(it)
                                } ?: keyBinding.defaultKeyCombination
                            )
                        } catch (e: RuntimeException) {
                            logger.error("Couldn't parse key binding", e)
                        }
                    }
            }
    }
}