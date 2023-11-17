package com.leanhtai01.learnapachepoi;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private StylesGenerator stylesGenerator;

    public ReportService(StylesGenerator stylesGenerator) {
        this.stylesGenerator = stylesGenerator;
    }

    public void generateXlsxReport() {
        return;
    }

    private void generateReport(Workbook workbook) {
        var styles = stylesGenerator.prepareStyles(workbook);
        Sheet sheet = workbook.createSheet("Example sheet name");

    }

    private void setColumnsWidth(Sheet sheet) {
        sheet.setColumnWidth(0, 256 * 20);

        for (int columnNumber = 1; columnNumber < 5; columnNumber++) {
            sheet.setColumnWidth(columnNumber, 256 * 15);
        }
    }

    private void createHeaderRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles) {
        Row row = sheet.createRow(0);

        for (int columnNumber = 1; columnNumber < 5; columnNumber++) {
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue("Column %d".formatted(columnNumber));
            cell.setCellStyle(styles.get(CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER));
        }
    }

    private void createRowLabelCell(Row row, Map<CustomCellStyle, CellStyle> styles, String label) {
        Cell rowLabel = row.createCell(0);
        rowLabel.setCellValue(label);
        rowLabel.setCellStyle(styles.get(CustomCellStyle.RED_BOLD_ARIAL_WITH_BORDER));
    }

    private void createStringRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles) {
        Row row = sheet.createRow(1);
        createRowLabelCell(row, styles, "Strings row");

        for (int columnNumber = 1; columnNumber < 5; ++columnNumber) {
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue("String %d".formatted(columnNumber));
            cell.setCellStyle(styles.get(CustomCellStyle.RIGHT_ALIGNED));
        }
    }

    private void createDoubleRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles) {
        Row row = sheet.createRow(2);
        createRowLabelCell(row, styles, "Doubles row");

        for (int columnNumber = 1; columnNumber < 5; ++columnNumber) {
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(new BigDecimal("%d.99").doubleValue());
            cell.setCellStyle(styles.get(CustomCellStyle.RIGHT_ALIGNED));
        }
    }
}
