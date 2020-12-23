package com.dansoftware.libraryapp.googlebooks

/**
 * Represents a collection of [Volume]s pulled from a Google Books server
 *
 * @author Daniel Gyorffy
 */
class Volumes : Iterable<Volume> {
    var kind: String? = null
    var totalItems: Int = 0
    var items: List<Volume>? = null

    override fun iterator() = items?.iterator() ?: emptyList<Volume>().iterator()
}

/**
 * Represents a [Volume] that holds the information of a Google Book
 *
 * @author Daniel Gyorffy
 */
data class Volume(var id: String?) {
    var kind: String? = null
    var selfLink: String? = null
    var volumeInfo: VolumeInfo? = null

    class VolumeInfo {
        var title: String? = null
        var subtitle: String? = null
        var authors: List<String>? = null
        var publisher: String? = null
        var publishedDate: String? = null
        var description: String? = null
        var industryIdentifiers: List<IndustryIdentifier>? = null
        var categories: List<String>? = null
        var imageLinks: ImageLinks? = null
        var language: String? = null
        var previewLink: String? = null

        class ImageLinks {
            var extraLarge: String? = null
            var large: String? = null
            var medium: String? = null
            var small: String? = null
            var smallThumbnail: String? = null
            var thumbnail: String? = null

            fun getLargest(): String =
                listOfNotNull(extraLarge, large, medium, thumbnail, small, smallThumbnail).first()
        }

        class IndustryIdentifier {
            var type: String? = null
            var identifier: String? = null
        }
    }
}










