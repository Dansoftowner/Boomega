package com.dansoftware.boomega.update

/**
 * This class responsible for storing the information about
 * an update
 *
 * @author Daniel Gyorffy
 */
data class UpdateInformation
/**
 * This constructor creates an UpdateInformationObject with the required values
 *
 * @param version   the new version of the update
 * @param reviewUrl the location of the review markdown-text.
 * @param binaries  the List that contains the object representations of the downloadable binaries
 */(val version: String, val reviewUrl: String, val binaries: List<DownloadableBinary>)