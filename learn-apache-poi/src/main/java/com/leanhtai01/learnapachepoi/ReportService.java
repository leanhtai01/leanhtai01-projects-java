package com.leanhtai01.learnapachepoi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

    public byte[] generateXlsxReport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        return generateReport(workbook);
    }

    public byte[] generateXlsReport() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        return generateReport(workbook);
    }

    private byte[] generateReport(Workbook workbook) throws IOException {
        var styles = stylesGenerator.prepareStyles(workbook);
        Sheet sheet = workbook.createSheet("Example sheet name");

        setColumnsWidth(sheet);

        createHeaderRow(sheet, styles);
        createStringRow(sheet, styles);
        createDoubleRow(sheet, styles);
        createDatesRow(sheet, styles);

        sheet.createFreezePane(0, 1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);

        out.close();
        workbook.close();

        return out.toByteArray();
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
            cell.setCellValue(new BigDecimal("%d.99".formatted(columnNumber)).doubleValue());
            cell.setCellStyle(styles.get(CustomCellStyle.RIGHT_ALIGNED));
        }
    }

    private void createDatesRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles) {
        Row row = sheet.createRow(3);
        createRowLabelCell(row, styles, "Dates row");

        for (int columnNumber = 1; columnNumber < 5; ++columnNumber) {
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(LocalDate.now());
            cell.setCellStyle(styles.get(CustomCellStyle.RIGHT_ALIGNED_DATE_FORMAT));
        }
    }
}
