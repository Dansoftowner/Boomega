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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.gui.control.PropertyTable
import com.dansoftware.boomega.gui.control.WebsiteHyperLink
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.beans.binding.Bindings
import javafx.beans.property.*

class SaleInfoTable(volume: ObjectProperty<Volume>) : PropertyTable() {

    private val eBook: BooleanProperty = SimpleBooleanProperty()
    private val country: StringProperty = SimpleStringProperty()
    private val listPrice: StringProperty = SimpleStringProperty()
    private val retailPrice: StringProperty = SimpleStringProperty()
    private val buyLink: StringProperty = SimpleStringProperty()

    init {
        volume.addListener { _, _, newVolume -> handleNewVolume(newVolume) }
        buildEntries()
    }

    private fun handleNewVolume(volume: Volume?) {
        eBook.value = volume?.saleInfo?.isEbook
        country.value = volume?.saleInfo?.country
        listPrice.value = volume?.saleInfo?.listPrice.toString()
        retailPrice.value = volume?.saleInfo?.retailPrice.toString()
        buyLink.value = volume?.saleInfo?.buyLink
    }

    private fun buildEntries() {
        items.add(Entry(i18n("google.books.details.sale.isebook"), isEbookBinding()))
        items.add(Entry(i18n("google.books.details.sale.country"), country))
        items.add(Entry(i18n("google.books.details.sale.listprice"), listPrice))
        items.add(Entry(i18n("google.books.details.sale.retailprice"), retailPrice))
        items.add(Entry(i18n("google.books.prop.sale.buy"), BuyWebsiteHyperlink()))
    }

    private fun isEbookBinding() = Bindings.createStringBinding({
        i18n(when {
            eBook.get() -> "Dialog.yes.button"
            else -> "Dialog.no.button"
        })
    }, eBook)

    private inner class BuyWebsiteHyperlink : WebsiteHyperLink(i18n("google.books.details.sale.buylink")) {
        init {
            urlProperty().bind(buyLink)
        }
    }
}