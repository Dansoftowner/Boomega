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

package com.dansoftware.boomega.gui.recordview;

import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.db.data.ServiceConnection;
import com.dansoftware.boomega.gui.control.BaseTable;
import com.dansoftware.boomega.gui.control.ReadOnlyRating;
import com.dansoftware.boomega.gui.control.TableViewPlaceHolder;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.dansoftware.boomega.gui.control.BaseTable.ColumnType.*;

public class RecordTable extends BaseTable<Record> {

    public static final ColumnType INDEX_COLUMN =
            new ColumnType(
                    "index",
                    "record.table.column.index",
                    RecordTable.class,
                    table -> new IndexColumn(table.startIndex),
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType TYPE_INDICATOR_COLUMN =
            new ColumnType(
                    "type_indicator",
                    "record.table.column.typeindicator",
                    TypeIndicatorColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType AUTHOR_COLUMN =
            new ColumnType(
                    "author",
                    "record.table.column.author",
                    AuthorColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType MAGAZINE_NAME_COLUMN =
            new ColumnType(
                    "magazine_name",
                    "record.table.column.magazinename",
                    MagazineNameColumn::new,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType TITLE_COLUMN =
            new ColumnType(
                    "title",
                    "record.table.column.title",
                    TitleColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType SUB_TITLE_COLUMN =
            new ColumnType(
                    "subtitle",
                    "record.table.column.subtitle",
                    SubtitleColumn::new,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType ISBN_COLUMN =
            new ColumnType(
                    "isbn",
                    "record.table.column.isbn",
                    ISBNColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType PUBLISHER_COLUMN =
            new ColumnType(
                    "publisher",
                    "record.table.column.publisher",
                    PublisherColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType DATE_COLUMN =
            new ColumnType(
                    "date",
                    "record.table.column.date",
                    DateColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType COPY_COUNT_COLUMN =
            new ColumnType(
                    "copy_count",
                    "record.table.column.copycount",
                    CopyCountColumn::new,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType LANG_COLUMN =
            new ColumnType(
                    "lang",
                    "record.table.column.lang",
                    LangColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType RANK_COLUMN =
            new ColumnType(
                    "rank",
                    "record.table.column.rank",
                    RankColumn::new,
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType SERVICE_CONNECTION_COLUMN =
            new ColumnType(
                    "service_connection",
                    "record.table.column.service",
                    ServiceConnectionColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED
            );

    private static final List<ColumnType> columnsList =
            List.of(
                    INDEX_COLUMN,
                    TYPE_INDICATOR_COLUMN,
                    MAGAZINE_NAME_COLUMN,
                    TITLE_COLUMN,
                    SUB_TITLE_COLUMN,
                    ISBN_COLUMN,
                    PUBLISHER_COLUMN,
                    DATE_COLUMN,
                    COPY_COUNT_COLUMN,
                    LANG_COLUMN,
                    RANK_COLUMN,
                    SERVICE_CONNECTION_COLUMN
            );

    public static List<ColumnType> columns() {
        return columnsList;
    }

    public static Optional<ColumnType> columnById(@NotNull String id) {
        return columns().stream().filter(it -> it.getId().equals(id)).findAny();
    }

    private static final String STYLE_CLASS = "books-table";

    private final IntegerProperty startIndex;

    public RecordTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.getStyleClass().add(STYLE_CLASS);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(new PlaceHolder(this));
    }

    public void buildDefaultColumns() {
        this.getColumns().clear();
        columns().stream()
                .filter(ColumnType::isDefaultVisible)
                .forEach(this::addColumnType);
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    private static class PlaceHolder extends TableViewPlaceHolder {
        PlaceHolder(@NotNull RecordTable table) {
            super(table);
            setMinHeight(0);
        }

        @Nullable
        @Override
        protected Node contentIfEmpty() {
            VBox vBox = new VBox(5.0);
            vBox.getStyleClass().add("record-table-place-holder");
            vBox.getChildren().add(new StackPane(new ImageView()));
            vBox.getChildren().add(new StackPane(new Label(I18N.getValue("record.table.place.holder"))));
            return vBox;
        }

        @Nullable
        @Override
        protected Node contentIfNoColumns() {
            return new Label(I18N.getValue("record.table.place.holder.nocolumn"));
        }
    }

    private static class RecordTableCell<T> extends TableCell<Record, T> {

        @Override
        public final void updateIndex(int i) {
            super.updateIndex(i);
            if (i >= 0 && getTableView().getItems().size() > i) {
                updateItem(getTableView().getItems().get(i));
            } else {
                updateItem(null);
            }
        }

        protected void updateItem(@Nullable Record item) {
        }

    }

    private static final class IndexColumn extends Column<Record, Integer>
            implements Callback<TableColumn.CellDataFeatures<Record, Integer>, ObservableValue<Integer>> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        private final IntegerProperty startIndexProperty;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(INDEX_COLUMN);
            this.startIndexProperty = startIndexProperty;
            setReorderable(false);
            setSortable(false);
            setMinWidth(COLUMN_WIDTH_UNIT);
            setMaxWidth(COLUMN_WIDTH_UNIT);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<Integer> call(CellDataFeatures<Record, Integer> cellData) {
            return new ObservableValueBase<>() {
                @Override
                public Integer getValue() {
                    return startIndexProperty.get() +
                            cellData.getTableView().getItems().indexOf(cellData.getValue()) + 1;
                }
            };
        }
    }

    private static final class TypeIndicatorColumn extends Column<Record, String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {
        TypeIndicatorColumn() {
            super(TYPE_INDICATOR_COLUMN);
            setReorderable(false);
            setCellFactory(this);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new RecordTableCell<>() {
                @Override
                protected void updateItem(@Nullable Record item) {
                    if (item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(new MaterialDesignIconView(
                                item.getRecordType() == Record.Type.BOOK ?
                                        MaterialDesignIcon.BOOK : MaterialDesignIcon.NEWSPAPER));
                    }
                }
            };
        }
    }

    private static final class AuthorColumn extends SortableColumn<Record>
            implements Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>> {
        AuthorColumn() {
            super(AUTHOR_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Record, String> cellData) {
            return new ObservableValueBase<>() {
                @Override
                public String getValue() {
                    final List<String> authors = cellData.getValue().getAuthors();
                    if (authors != null)
                        return String.join(", ", authors);
                    return StringUtils.EMPTY;
                }
            };
        }
    }

    private static final class MagazineNameColumn extends SortableColumn<Record> {
        MagazineNameColumn() {
            super(MAGAZINE_NAME_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("magazineName"));
        }
    }

    private static final class TitleColumn extends SortableColumn<Record> {
        TitleColumn() {
            super(TITLE_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("title"));
        }
    }

    private static final class SubtitleColumn extends SortableColumn<Record> {
        SubtitleColumn() {
            super(SUB_TITLE_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("subtitle"));
        }
    }

    private static final class PublisherColumn extends SortableColumn<Record> {
        PublisherColumn() {
            super(PUBLISHER_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("publisher"));
        }
    }

    private static final class LangColumn extends Column<Record, String>
            implements Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>> {
        LangColumn() {
            super(LANG_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Record, String> cellData) {
            return new ObservableValueBase<String>() {
                @Override
                public String getValue() {
                    final String raw = cellData.getValue().getLanguage();
                    if (raw != null)
                        return Locale.forLanguageTag(raw).getDisplayLanguage();
                    return StringUtils.EMPTY;
                }
            };
        }
    }

    private static final class DateColumn extends Column<Record, String> {
        DateColumn() {
            super(DATE_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        }
    }

    private static final class ISBNColumn extends Column<Record, String> {
        ISBNColumn() {
            super(ISBN_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("isbn"));
        }
    }

    private static final class CopyCountColumn extends Column<Record, Integer> {
        CopyCountColumn() {
            super(COPY_COUNT_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("numberOfCopies"));
        }
    }

    private static final class RankColumn extends Column<Record, String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {
        RankColumn() {
            super(RANK_COLUMN);
            setCellFactory(this);
            setMinWidth(250);
            setMaxWidth(250);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new RecordTableCell<>() {
                @Override
                protected void updateItem(@Nullable Record item) {
                    if (item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Rating graphic = buildGraphic(Optional.ofNullable(item.getRating()).orElse(0));
                        setGraphic(graphic);
                    }
                }

                private Rating buildGraphic(int rating) {
                    return new ReadOnlyRating(5, rating);
                }
            };
        }
    }

    private static final class ServiceConnectionColumn extends Column<Record, String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {

        private static final double WIDTH = 60;

        ServiceConnectionColumn() {
            super(SERVICE_CONNECTION_COLUMN);
            this.setReorderable(false);
            this.setCellFactory(this);
            this.setMinWidth(WIDTH);
            this.setMaxWidth(WIDTH);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new RecordTableCell<>() {
                @Override
                protected void updateItem(@Nullable Record item) {
                    super.updateItem(item);
                    if (item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Optional.ofNullable(item.getServiceConnection())
                                .map(ServiceConnection::getGoogleBookHandle)
                                .map(value -> StringUtils.getIfBlank(value, null))
                                .ifPresentOrElse(
                                        value -> setGraphic(buildGoogleBooksIcon()),
                                        () -> setGraphic(null)
                                );
                    }
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                }
            };
        }

        private Node buildGoogleBooksIcon() {
            return new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png"));
        }
    }
}
