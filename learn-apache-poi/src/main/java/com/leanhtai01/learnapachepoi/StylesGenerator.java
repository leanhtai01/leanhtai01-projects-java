package com.leanhtai01.learnapachepoi;

import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class StylesGenerator {
    public Map<CustomCellStyle, CellStyle> prepareStyles(Workbook workbook) {
        Font boldArial = createBoldArialFont(workbook);
        Font redBoldArial = createRedBoldArialFont(workbook);

        CellStyle rightAlignedStyle = createRightAlignedStyle(workbook);
        CellStyle greyCenteredBoldArialWithBorderStyle =
                createGreyCenteredBoldArialWithBorderStyle(workbook, boldArial);
        CellStyle redBoldArialWithBorderStyle =
                createRedBoldArialWithBorderStyle(workbook, redBoldArial);
        CellStyle rightAlignedDateFormatStyle =
                createRightAlignedDateFormatStyle(workbook);

        return Map.of(
                CustomCellStyle.RIGHT_ALIGNED, rightAlignedStyle,
                CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER, greyCenteredBoldArialWithBorderStyle,
                CustomCellStyle.RED_BOLD_ARIAL_WITH_BORDER, redBoldArialWithBorderStyle,
                CustomCellStyle.RIGHT_ALIGNED_DATE_FORMAT, rightAlignedDateFormatStyle);
    }

    private Font createBoldArialFont(Workbook workbook) {
        Font font = workbook.createFont();

        font.setFontName("Arial");
        font.setBold(true);

        return font;
    }

    private Font createRedBoldArialFont(Workbook workbook) {
        Font font = workbook.createFont();

        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(IndexedColors.RED.index);

        return font;
    }

    private CellStyle createRightAlignedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);

        return style;
    }

    private CellStyle createBorderedStyle(Workbook workbook) {
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        CellStyle style = workbook.createCellStyle();

        style.setBorderRight(thin);
        style.setRightBorderColor(black);

        style.setBorderBottom(thin);
        style.setBottomBorderColor(black);

        style.setBorderLeft(thin);
        style.setLeftBorderColor(black);

        style.setBorderTop(thin);
        style.setTopBorderColor(black);

        return style;
    }

    private CellStyle createGreyCenteredBoldArialWithBorderStyle(Workbook workbook, Font boldArial) {
        CellStyle style = createBorderedStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(boldArial);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    private CellStyle createRedBoldArialWithBorderStyle(Workbook workbook, Font redBoldArial) {
        CellStyle style = createBorderedStyle(workbook);
        style.setFont(redBoldArial);

        return style;
    }

    private CellStyle createRightAlignedDateFormatStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat((short) 14);

        return style;
    }
}
