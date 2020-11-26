package com.dansoftware.libraryapp.util

import java.io.File
import java.io.IOException

class FileIOException(val file: File, msg: String?, cause: Throwable?) : IOException(msg, cause) {
    constructor(file: File, cause: Throwable?): this(file, null, cause)
}