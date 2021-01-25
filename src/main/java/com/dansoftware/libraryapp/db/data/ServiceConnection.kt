package com.dansoftware.libraryapp.db.data

import org.apache.commons.lang3.StringUtils

/**
 * A [ServiceConnection] used for connecting/pairing a [Book] or a [Magazine]
 * to external online services like Google Books.
 *
 * @author Daniel Gyorffy
 */
class ServiceConnection(var googleBookLink: String?) {

    constructor() : this(null)

    fun isEmpty() = StringUtils.isBlank(googleBookLink)
}