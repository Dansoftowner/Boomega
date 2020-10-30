package com.dansoftware.libraryapp.update

/**
 * This class responsible for storing the information about
 * an update
 */
class UpdateInformation
/**
 * This constructor creates an UpdateInformationObject with the required values
 *
 * @param version   the new version of the update
 * @param reviewUrl the location of the review web page that describes
 * the features of the new update (http://example.com/libraryappreview.html).
 * @param binaries  the Map that contains the binary types and the location
 * of each downloadable binary on the web.
 */(val version: String, val reviewUrl: String, val binaries: List<DownloadableBinary>)