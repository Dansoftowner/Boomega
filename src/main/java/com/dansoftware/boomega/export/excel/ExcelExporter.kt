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

package com.dansoftware.boomega.export.excel

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.export.api.BaseExporter
import com.dansoftware.boomega.export.api.ExportProcessObserver
import com.dansoftware.boomega.gui.export.ConfigurationDialog
import com.dansoftware.boomega.gui.export.excel.ExcelConfigurationDialog
import com.dansoftware.boomega.gui.util.icon
import javafx.scene.Node
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.time.LocalDate

/**
 * An [ExcelExporter] allows to export [Record]s into Excel (.xlsx) format.
 */
class ExcelExporter : BaseExporter<ExcelExportConfiguration>() {

    override val name: String
        get() = "Excel"

    override val icon: Node
        get() = icon("excel-icon")

    override val configurationDialog: ConfigurationDialog<ExcelExportConfiguration>
        get() = ExcelConfigurationDialog()

    override val contentType: String
        get() = "xlsx"

    override val contentTypeDescription: String
        get() = "Excel OpenXML documents" // TODO: i18n

    override fun write(
        items: List<Record>,
        output: OutputStream,
        config: ExcelExportConfiguration,
        observer: ExportProcessObserver
    ) {
        val sortedItems = sortRecords(items, config)
        buildWorkbook(sortedItems, config).use { workbook ->
            output.buffered().use(workbook::write)
            if (workbook is SXSSFWorkbook) workbook.dispose()
        }
    }

    /**
     * Builds the [Workbook] object with all the data.
     */
    private fun buildWorkbook(items: List<Record>, config: ExcelExportConfiguration): Workbook {
        // excel 'OOXML' (.xlsx) format
        // using SXSSF instead of XSSF for low-memory footprint
        // see https://poi.apache.org/components/spreadsheet/
        val workbook = SXSSFWorkbook()

        val sheet: SXSSFSheet =
            config.sheetName
                ?.let(WorkbookUtil::createSafeSheetName)
                ?.let(workbook::createSheet)
                ?: workbook.createSheet()

        val createdRowCount = createHeaderRows(workbook, sheet, config)
        createRegularRows(workbook, sheet, items, config, createdRowCount)

        sheet.autoSizeColumns(config.requiredFields.indices)

        return workbook
    }

    /**
     * Creates the header row(s).
     *
     * @return the count of the rows it created
     */
    private fun createHeaderRows(workbook: SXSSFWorkbook, sheet: SXSSFSheet, config: ExcelExportConfiguration): Int {
        val row = sheet.createRow(0)
        row.height = -1
        val cellStyle = config.headerCellStyle.asPoiCellStyle(workbook.xssfWorkbook)
        config.requiredFields.forEachIndexed { index, field ->
            val cell = row.createCell(index)
            cell.setCellValue(field.name)
            cell.cellStyle = cellStyle
            sheet.trackColumnForAutoSizing(index)
        }
        return 1
    }

    /**
     * Creates the rows representing the [Record]s.
     */
    private fun createRegularRows(
        workbook: SXSSFWorkbook,
        sheet: Sheet,
        items: List<Record>,
        config: ExcelExportConfiguration,
        initialRowCount: Int
    ) {
        val cellStyle = config.regularCellStyle.asPoiCellStyle(workbook.xssfWorkbook)
        items.forEachIndexed { index, it ->
            createRowForRecord(sheet, cellStyle, it, config, index + initialRowCount)
        }
    }

    /**
     * Creates a single row for the a record in the given [Sheet].
     */
    private fun createRowForRecord(
        sheet: Sheet,
        cellStyle: CellStyle,
        record: Record,
        config: ExcelExportConfiguration,
        rowIndex: Int
    ) {
        val row = sheet.createRow(rowIndex)
        row.height = -1
        config.requiredFields.forEachIndexed { index, field ->
            val cell = row.createCell(index)
            cell.cellStyle = cellStyle
            cell.setValue(field.getValue(record), config.emptyCellPlaceHolder)
        }
    }

    /**
     * Converts an [ExcelExportConfiguration.CellStyle] into a POI [CellStyle]
     */
    private fun ExcelExportConfiguration.CellStyle.asPoiCellStyle(workbook: XSSFWorkbook): CellStyle =
        workbook.createCellStyle().apply {
            setFont(getPoiFont(workbook))
            backgroundColor?.let {
                fillPattern = FillPatternType.SOLID_FOREGROUND
                setFillForegroundColor(it.asXSSFColor(workbook))
            }
        }

    /**
     * Extracts a POI [Font] from the given [ExcelExportConfiguration.CellStyle]
     */
    private fun ExcelExportConfiguration.CellStyle.getPoiFont(workbook: XSSFWorkbook): Font =
        workbook.createFont().apply {
            this@getPoiFont.fontName?.let { fontName = it }
            bold = this@getPoiFont.isBold
            italic = this@getPoiFont.isItalic
            strikeout = this@getPoiFont.isStrikeout
            underline = this@getPoiFont.underline
            fontColor?.let { setColor(it.asXSSFColor(workbook)) }
            fontSize?.let { fontHeightInPoints = it }
        }

    private fun Cell.setValue(value: Any?, valueIfNull: String?) {
        when (value) {
            is String -> setCellValue(value)
            is Boolean -> setCellValue(value)
            is Double -> setCellValue(value)
            is java.util.Date -> setCellValue(value)
            is java.time.LocalDateTime -> setCellValue(value)
            is java.util.Calendar -> setCellValue(value)
            is java.util.Locale -> setCellValue(value.displayLanguage)
            is LocalDate -> setDate(value)
            is List<*> -> setCellValue(value.joinToString(separator = ", "))
            else -> setCellValue(value?.toString() ?: valueIfNull)
        }
    }

    /**
     * Auto sizes the columns with the given indices.
     */
    private fun SXSSFSheet.autoSizeColumns(indices: IntRange) {
        indices.forEach {
            autoSizeColumn(it)
        }
    }

    /**
     * Converts an [java.awt.Color] into an [XSSFColor]
     */
    private fun java.awt.Color.asXSSFColor(workbook: XSSFWorkbook): XSSFColor =
        XSSFColor(this, workbook.stylesSource.indexedColors)

    /**
     * Sets the value and configures it to be formatted as date
     */
    private fun Cell.setDate(value: LocalDate) {
        cellStyle?.dataFormat = sheet.workbook.creationHelper
            .createDataFormat()
            .getFormat("yyyy-MM-dd")
        setCellValue(value)
    }
}