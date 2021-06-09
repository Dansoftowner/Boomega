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
import com.dansoftware.boomega.gui.control.BoomegaTable;
import com.dansoftware.boomega.gui.control.ReadOnlyRating;
import com.dansoftware.boomega.gui.control.TableViewPlaceHolder;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dansoftware.boomega.gui.control.BoomegaTable.ColumnType.*;

public class RecordTable extends BoomegaTable<Record> {

    public static final ColumnType<IndexColumn> INDEX_COLUMN =
            new ColumnType<>(
                    "record.table.column.index",
                    IndexColumn.class,
                    table -> new IndexColumn(table.startIndex),
                    DEFAULT_VISIBLE
            );

    public static final ColumnType<TypeIndicatorColumn> TYPE_INDICATOR_COLUMN =
            new ColumnType<>(
                    "record.table.column.typeindicator",
                    TypeIndicatorColumn.class,
                    table -> new TypeIndicatorColumn(),
                    DEFAULT_VISIBLE
            );

    public static final ColumnType<AuthorColumn> AUTHOR_COLUMN = new ColumnType<>(
            "record.table.column.author",
            AuthorColumn.class,
            table -> new AuthorColumn(),
            DEFAULT_VISIBLE,
            TEXT_GUI_VISIBLE,
            INTERNATIONALIZED
    );

    public static final ColumnType<MagazineNameColumn> MAGAZINE_NAME_COLUMN = new ColumnType<>(
            "record.table.column.magazinename",
            MagazineNameColumn.class,
            table -> new MagazineNameColumn(),
            TEXT_GUI_VISIBLE,
            INTERNATIONALIZED
    );

    public static final ColumnType<TitleColumn> TITLE_COLUMN =
            new ColumnType<>(
                    "record.table.column.title",
                    TitleColumn.class,
                    table -> new TitleColumn(),
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType<SubtitleColumn> SUB_TITLE_COLUMN =
            new ColumnType<>(
                    "record.table.column.subtitle",
                    SubtitleColumn.class,
                    table -> new SubtitleColumn(),
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType<ISBNColumn> ISBN_COLUMN =
            new ColumnType<>(
                    "record.table.column.isbn",
                    ISBNColumn.class,
                    table -> new ISBNColumn(),
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType<PublisherColumn> PUBLISHER_COLUMN =
            new ColumnType<>(
                    "record.table.column.publisher",
                    PublisherColumn.class,
                    table -> new PublisherColumn(),
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE,
                    INTERNATIONALIZED
            );

    // ------------->
    public static final ColumnType<DateColumn> DATE_COLUMN =
            new ColumnType<>(
                    "record.table.column.date",
                    DateColumn.class,
                    table -> new DateColumn()
            );

    public static final ColumnType<CopyCountColumn> COPY_COUNT_COLUMN =
            new ColumnType<>(
                    "record.table.column.copycount",
                    CopyCountColumn.class,
                    table -> new CopyCountColumn()
            );

    public static final ColumnType<LangColumn> LANG_COLUMN =
            new ColumnType<>(
                    "record.table.column.lang",
                    LangColumn.class,
                    table -> new LangColumn()
            );

    public static final ColumnType<RankColumn> RANK_COLUMN =
            new ColumnType<>(
                    "record.table.column.rank",
                    RankColumn.class,
                    table -> new RankColumn()
            );

    public static final ColumnType<ServiceConnectionColumn> SERVICE_CONNECTION_COLUMN =
            new ColumnType<>(
                    "record.table.column.service",
                    ServiceConnectionColumn.class,
                    table -> new ServiceConnectionColumn()
            );


    private static final String STYLE_CLASS = "books-table";

    private final IntegerProperty startIndex;

    public RecordTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.init();
    }

    private void init() {
        this.getStyleClass().add(STYLE_CLASS);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(
                new TableViewPlaceHolder(
                        this,
                        () -> I18N.getValue("record.table.place.holder"),
                        () -> I18N.getValue("record.table.place.holder.nocolumn")
                )
        );
    }

    public void buildDefaultColumns() {
        this.getColumns().clear();
        Arrays.stream(ColumnType.values())
                .filter(ColumnType::isDefaultVisible)
                .forEach(this::addColumn);
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
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
            setMinWidth(25);
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
