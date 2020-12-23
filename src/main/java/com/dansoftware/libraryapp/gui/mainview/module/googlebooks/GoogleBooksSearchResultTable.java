package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.WebsiteHyperLink;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GoogleBooksSearchResultTable extends TableView<Volume.VolumeInfo> {

    private final Context context;
    private final IntegerProperty startIndex;

    GoogleBooksSearchResultTable(@NotNull Context context, int startIndex) {
        this.context = context;
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.init();
    }

    private void init() {
        this.getColumns().addAll(
                new IndexColumn(startIndex),
                new TypeIndicatorColumn(),
                new ThumbnailColumn(),
                new ISBNColumn(),
                new AuthorColumn(),
                new TitleColumn(),
                new DescriptionColumn(),
                new DateColumn(),
                new BrowserColumn()
        );
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(new PlaceHolder());
    }

    public int getStartIndex() {
        return startIndex.get();
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex.set(startIndex);
    }

    private static final class PlaceHolder extends StackPane {
        PlaceHolder() {
            getChildren().add(new Group(new VBox(
                    //TODO: ICON
                    new Label(I18N.getGoogleBooksImportValue("google.books.table.place.holder"))
            )));
        }
    }

    private static final class IndexColumn extends TableColumn<Volume.VolumeInfo, String>
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {

        private final IntegerProperty startIndexProperty;

        IndexColumn(IntegerProperty startIndexProperty) {
            this.startIndexProperty = startIndexProperty;
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(Integer.toString(startIndexProperty.get() + getIndex() + 1));
                    }
                }
            };
        }
    }

    private static final class TypeIndicatorColumn extends TableColumn<Volume.VolumeInfo, String>
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {

        TypeIndicatorColumn() {
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        setGraphic(new MaterialDesignIconView(volume.isMagazine() ? MaterialDesignIcon.NEWSPAPER : MaterialDesignIcon.BOOK));
                    }
                }
            };
        }

    }

    private static final class ThumbnailColumn extends TableColumn<Volume.VolumeInfo, String>
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {

        ThumbnailColumn() {
            super(I18N.getGoogleBooksImportValue("google.books.table.column.thumbnail"));
            setCellFactory(this);
            setMinWidth(20);
            setPrefWidth(200);
            setReorderable(false);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getImageLinks())
                                .map(Volume.VolumeInfo.ImageLinks::getThumbnail)
                                .ifPresentOrElse(thumbnail -> {
                                    setGraphic(new ImageView(thumbnail));
                                    setText(null);
                                }, () -> {
                                    setGraphic(null);
                                    setText(I18N.getGoogleBooksImportValue("google.books.table.thumbnail.not.available"));
                                });
                    }
                }
            };
        }
    }

    private static final class ISBNColumn extends TableColumn<Volume.VolumeInfo, String>
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        ISBNColumn() {
            super(I18N.getGoogleBooksImportValue("google.books.table.column.isbn"));
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getIndustryIdentifiers())
                                .map(identifiers -> identifiers.get(0))
                                .map(Volume.VolumeInfo.IndustryIdentifier::getIdentifier)
                                .ifPresentOrElse(this::setText, () -> setText("-"));
                    }
                }
            };
        }
    }

    private static final class AuthorColumn extends TableColumn<Volume.VolumeInfo, String>
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        AuthorColumn() {
            super(I18N.getGoogleBooksImportValue("google.books.table.column.author"));
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getAuthors())
                                .ifPresentOrElse(authors -> setText(String.join(",", authors)),
                                        () -> setText("-"));
                    }
                }
            };
        }
    }

    private static final class TitleColumn extends TableColumn<Volume.VolumeInfo, String> {
        TitleColumn() {
            super(I18N.getGoogleBooksImportValue("google.books.table.column.title"));
            setCellValueFactory(new PropertyValueFactory<>("title"));
        }
    }

    private static final class DescriptionColumn extends TableColumn<Volume.VolumeInfo, String> implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        DescriptionColumn() {
            setText(I18N.getGoogleBooksImportValue("google.books.table.column.desc"));
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getDescription())
                                .ifPresentOrElse(desc -> {
                                    setGraphic(new Text(desc));
                                    setTooltip(new Tooltip(desc));
                                }, () -> {
                                    setGraphic(null);
                                    setTooltip(null);
                                    setText("-");
                                });
                    }
                }
            };
        }
    }

    private static final class DateColumn extends TableColumn<Volume.VolumeInfo, String> {

        DateColumn() {
            super(I18N.getGoogleBooksImportValue("google.books.table.column.date"));
            setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        }
    }

    private static final class BrowserColumn extends TableColumn<Volume.VolumeInfo, String>
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        BrowserColumn() {
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(50);
            setPrefWidth(200);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getPreviewLink())
                                .ifPresent(link -> setGraphic(new WebsiteHyperLink(I18N.getGoogleBooksImportValue("google.books.table.browser.open"), link)));
                    }
                }
            };
        }
    }
}
