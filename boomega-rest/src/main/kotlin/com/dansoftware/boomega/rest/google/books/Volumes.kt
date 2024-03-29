/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.rest.google.books

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
    var saleInfo: SaleInfo? = null

    class VolumeInfo {
        companion object PrintType {
            const val MAGAZINE = "MAGAZINE"
            const val BOOK = "BOOK"
        }

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
        var printType: String? = null
        var averageRating: Double? = null
        var ratingsCount: Int = 0

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

            companion object {
                const val ISBN_10 = "ISBN_10"
                const val ISBN_13 = "ISBN_13"
            }

            var type: String? = null
            var identifier: String? = null

            val isIsbn10: Boolean
                get() = type == ISBN_10
            val isIsbn13: Boolean
                get() = type == ISBN_13

            override fun toString() =
                listOf(type?.replace('_', ' '), identifier).joinToString(": ")
        }
    }

    class SaleInfo {

        companion object Saleability {
            const val FOR_SALE = "FOR_SALE"
            const val NOT_FOR_SALE = "NOT_FOR_SALE"
        }

        var buyLink: String? = null
        var country: String? = null
        var isEbook: Boolean = false
        var listPrice: ListPrice? = null
        var retailPrice: RetailPrice? = null
        var saleability: String? = null

        class ListPrice {
            var amount: Double = 0.0
            var currencyCode: String? = null

            override fun toString() = "$amount $currencyCode"
        }

        class RetailPrice {
            var amount: Double = 0.0
            var currencyCode: String? = null

            override fun toString() = "$amount $currencyCode"
        }
    }
}










