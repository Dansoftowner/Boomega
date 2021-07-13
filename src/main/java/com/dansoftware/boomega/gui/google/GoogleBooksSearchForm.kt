/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.google

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.SearchTextField
import com.dansoftware.boomega.gui.control.formsfx.LanguageSelectionControl
import com.dansoftware.boomega.gui.util.onScenePresent
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQuery
import com.dlsc.formsfx.model.structure.Field
import com.dlsc.formsfx.model.structure.Form
import com.dlsc.formsfx.model.util.BindingMode
import com.dlsc.formsfx.model.util.ResourceBundleService
import com.dlsc.formsfx.view.renderer.FormRenderer
import com.dlsc.formsfx.view.util.ColSpan
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

/**
 * The [GoogleBooksSearchForm] is where the user can start the Google Books search.
 */
class GoogleBooksSearchForm(
    private val context: Context,
    private val onSearchRequest: (GoogleBooksQuery) -> Unit
) : StackPane() {

    private val generalText: StringProperty = SimpleStringProperty("")
    private val author: StringProperty = SimpleStringProperty("")
    private val title: StringProperty = SimpleStringProperty("")
    private val publisher: StringProperty = SimpleStringProperty("")
    private val subject: StringProperty = SimpleStringProperty("")
    private val isbn: StringProperty = SimpleStringProperty("")
    private val language: StringProperty = SimpleStringProperty("")

    init {
        styleClass.add("google-books-search-form")
        buildUI()
        playAnimation()
    }

    private fun playAnimation() {
        onScenePresent {
            animatefx.animation.FadeInUp(this).play()
        }
    }

    private fun buildUI() {
        children.add(buildCenterBox())
    }

    private fun buildCenterBox() = Group(
        VBox(
            10.0,
            buildLogo(),
            buildTitleLabel(),
            buildDescriptionLabel(),
            buildMainSearchTextField(),
            buildDetailsSearchArea(),
            buildSearchButton()
        )
    )

    private fun buildLogo() =
        StackPane(ImageView(Image("/com/dansoftware/boomega/image/util/google-circle_64px.png")))

    private fun buildTitleLabel() =
        Label(i18n("google.books.title")).run {
            styleClass.add("title-label")
            StackPane(this)
        }

    private fun buildDescriptionLabel() =
        Label(i18n("google.books.description")).run {
            StackPane(this)
        }

    private fun buildMainSearchTextField() = SearchTextField().apply {
        generalText.bindBidirectional(textProperty())
        textProperty().bindBidirectional(generalText)
        promptText = i18n("google.books.add.form.gtext")
    }

    private fun buildDetailsSearchArea() =
        TitledPane(
            i18n("google.books.form.details"),
            FormRenderer(
                Form.of(
                    com.dlsc.formsfx.model.structure.Group.of(
                        Field.ofStringType(author)
                            .placeholder("google.books.add.form.author.prompt")
                            .label("google.books.add.form.author")
                            .span(ColSpan.HALF),
                        Field.ofStringType(title)
                            .placeholder("google.books.add.form.title.prompt")
                            .label("google.books.add.form.title")
                            .span(ColSpan.HALF),
                        Field.ofStringType(publisher)
                            .placeholder("google.books.add.form.publisher.prompt")
                            .label("google.books.add.form.publisher")
                            .span(ColSpan.HALF),
                        Field.ofStringType(subject)
                            .placeholder("google.books.add.form.subject.prompt")
                            .label("google.books.add.form.subject")
                            .span(ColSpan.HALF),
                        Field.ofStringType(isbn)
                            .placeholder("google.books.add.form.isbn.prompt")
                            .label("google.books.add.form.isbn")
                            .span(ColSpan.HALF),
                        Field.ofStringType(language)
                            .styleClass("languageSelector")
                            .placeholder("google.books.add.form.lang.prompt")
                            .label("google.books.add.form.lang")
                            .render(LanguageSelectionControl(context))
                            .span(ColSpan.HALF),
                        //TODO: sort type
                        //TODO: print type
                    )
                ).title("google.books.add.form.ftitle")
                    .binding(BindingMode.CONTINUOUS)
                    .i18n(ResourceBundleService(I18N.getValues()))
            )
        ).apply { styleClass.add("details-pane") }

    private fun buildSearchButton() = Button().apply {
        text = I18N.getValue("google.books.add.form.search")
        graphic = FontAwesomeIconView(FontAwesomeIcon.SEARCH)
        maxWidth = Double.MAX_VALUE
        isDefaultButton = true
        setOnAction { search() }
    }

    private fun search() {
        onSearchRequest(buildGoogleBooksQuery())
    }

    private fun buildGoogleBooksQuery() =
        GoogleBooksQuery()
            .inText(generalText.get())
            .inAuthor(author.get())
            .inPublisher(publisher.get())
            .inTitle(title.get())
            .isbn(isbn.get())
            .language(language.get())
            .subject(subject.get())

}
