package com.dansoftware.boomega.update

import java.io.InputStream
import java.net.URL

open class DownloadableBinary(val name: String, val fileExtension: String, val downloadUrl: String) {
    open fun openStream(): InputStream = URL(downloadUrl).openStream()
}