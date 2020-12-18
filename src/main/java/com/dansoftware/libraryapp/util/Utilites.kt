package com.dansoftware.libraryapp.util

import com.jfilegoodies.explorer.FileExplorers
import java.io.File

fun File.revealInExplorer() {
    FileExplorers.get().openSelect(this)
}