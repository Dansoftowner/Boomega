package com.dansoftware.libraryapp.util

import com.jfilegoodies.explorer.FileExplorers
import java.io.File

fun File.revealInExplorer() {
    FileExplorers.get().openSelect(this)
}

fun <I, T : Collection<I>> T.ifNotEmpty(block: (T) -> Unit): T {
    if (this.isNotEmpty())
        block(this)
    return this
}

fun String.surrounding(prefixSuffix: String) = this.surrounding(prefixSuffix, prefixSuffix)

fun String.surrounding(prefix: String, suffix: String) = (prefix + this + suffix)