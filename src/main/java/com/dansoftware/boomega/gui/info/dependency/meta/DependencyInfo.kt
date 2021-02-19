package com.dansoftware.boomega.gui.info.dependency.meta

import java.util.*

/**
 * A [DependencyInfo] represents a software that is used by this application.
 *
 * @author Daniel Gyorffy
 */
data class DependencyInfo(val name: String, private val websiteUrl: String, val licenseInfo: LicenseInfo) {
    fun getWebsiteUrl(): Optional<String> = Optional.ofNullable(websiteUrl)
}