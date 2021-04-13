package com.dansoftware.boomega.util

import java.util.*

class InMemoryResourceBundle(private val map: Map<String, String>) : ResourceBundle() {

    override fun handleGetObject(key: String) = map[key]

    override fun getKeys(): Enumeration<String> = Collections.enumeration(map.keys)

    class Builder {
        private val map: MutableMap<String, String> = HashMap()

        fun put(key: String, value: String) = apply { map[key] = value }

        fun build() = InMemoryResourceBundle(map)
    }
}