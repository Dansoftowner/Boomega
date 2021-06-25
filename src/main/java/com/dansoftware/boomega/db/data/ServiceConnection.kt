package com.dansoftware.boomega.db.data

/**
 * A [ServiceConnection] used for connecting/pairing a [Record]
 * to external online services like Google Books.
 *
 * @author Daniel Gyorffy
 */
class ServiceConnection {

    private val infoMap: MutableMap<String, Any?> = HashMap()

    var googleBookHandle: String?
        get() = infoMap[GOOGLE_BOOK_HANDLE]?.toString()
        set(value) {
            infoMap[GOOGLE_BOOK_HANDLE] = value
        }

    constructor()

    constructor(info: Map<String, Any?>) {
        infoMap.putAll(info)
    }

    operator fun get(key: String) = infoMap[key]

    fun getString(key: String) = this[key].toString()

    fun put(key: String, value: Any?) = infoMap.put(key, value).let { this }

    fun remove(key: String) = infoMap.remove(key)

    fun isEmpty() = infoMap.isEmpty()

    fun copy() = ServiceConnection(HashMap(this.infoMap))

    companion object {
        const val GOOGLE_BOOK_HANDLE = "google.book.handle"
    }
}