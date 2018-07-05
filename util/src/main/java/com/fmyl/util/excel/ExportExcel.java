package com.fmyl.util.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description: export excle
 * @Author: fmyl
 * @Date: 2018/7/5 下午12:44
 */
public class ExportExcel {

    private final HSSFWorkbook workbook = new HSSFWorkbook();
    private final List<SheetModel> sheets = new ArrayList();

    private final HSSFCellStyle style = CellStyle.getStyle(workbook, CellStyle.STYLE_NONE);
    private final HSSFCellStyle styleTitle = CellStyle.getStyle(workbook, CellStyle.STYLE_TITLE);
    private final HSSFCellStyle styleTop = CellStyle.getStyle(workbook, CellStyle.STYLE_TOP);
    private final HSSFCellStyle styleRed = CellStyle.getStyle(workbook, CellStyle.STYLE_RED);

    public final static String EVENT_TITLE = "^EVENT_TITLE^";
    public final static String EVENT_DATA = "^EVENT_DATA^";
    public final static String EVENT_TOP = "^EVENT_TOP^";
    public final static String EVENT_STYLE_RED = "^EVENT_STYLE_RED^";
    public final static String EVENT_STYLE_NONE = "^EVENT_STYLE_NONE^";

    /**
     * 构建sheet里的数据
     * dataList 中每一个data的第一个元素，都是一个标示位，用来标示当前列的显示形式
     *
     * @param sheetName
     * @param dataList
     * @return: void
     * @Author: fmyl
     * @Date: 2018-07-05 14:44:10
     */
    public void addSheet(String sheetName, List<List<String>> dataList) {
        sheetName = StringUtils.isBlank(sheetName) ? "sheet" + rank(5) : sheetName;
        SheetModel sheetModel = new SheetModel(sheetName, dataList);
        sheets.add(sheetModel);
    }

    private String rank(int n) {
        String rankVal = "";
        Random random = new Random();
        while (n-- > 0) {
            rankVal = rankVal + random.nextInt(9);
        }
        return rankVal;
    }

    private void buildWorkBook() {
        for (SheetModel sheetModel : sheets) {
            HSSFSheet sheet = workbook.createSheet(sheetModel.getName());
            List<RowModel> rows = sheetModel.getRows();
            if (rows == null || rows.isEmpty()) {
                throw new RuntimeException("export excel rows is null");
            }
            for (int i = 0; i < rows.size(); i++) {
                RowModel rowModel = rows.get(i);
                bulidRow(sheet, i, rowModel, sheetModel.getMaxCellNum());
            }
        }
    }

    private void bulidRow(HSSFSheet sheet, int l, RowModel rowModel, int maxCellNum) {
        sheet.setAutobreaks(true);
        HSSFRow rowm = sheet.createRow(l);
        List<CellModel> cells = rowModel.getCells();
        int c = 0;
        String event = "";
        for (CellModel cellModel : cells) {
            sheet.autoSizeColumn(c > 0 ? c - 1 : 0);
            HSSFCell cell = rowm.createCell(c > 0 ? c - 1 : 0);
            String value = cellModel.getValue();
            if (c == 0) {
                event = value;
            }
            // @# 合并单元格
            if (EVENT_TITLE.equals(event)) {
                cell.setCellStyle(styleTitle);
                sheet.addMergedRegion(new CellRangeAddress(l, l, 0, (maxCellNum - 2)));
            }
            cell.setCellValue(value);
            c++;
            // title 样式
            if (EVENT_TITLE.equals(event)) {
                cell.setCellStyle(styleTitle);
                continue;
            }
            // -- 取消样式
            if (EVENT_STYLE_NONE.equals(event)) {
                continue;
            }
            // ^^ 标题样式
            if (EVENT_TOP.equals(event)) {
                cell.setCellStyle(styleTop);
                continue;
            }
            cell.setCellStyle(style);
        }
    }

    public void exportFile(String path, String fileName) {
        buildWorkBook();
        File file = new File(path + "/" + fileName);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    out = null;
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出数据
     */
    public void export(String fileName, HttpServletResponse response) throws Exception {
        try {
            fileName = fileName + "_" + System.currentTimeMillis() + ".xls";
            fileName = new String(fileName.getBytes(), "iso-8859-1");
            String headStr = "attachment; filename=\"" + fileName + "\"";
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            response.setCharacterEncoding("UTF-8");
            OutputStream out = response.getOutputStream();
            try {
                buildWorkBook();
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class SheetModel {
        private String name;
        private List<RowModel> rows;
        private int styleCode = 0;
        private int maxCellNum;
        private int maxRowNum;

        public SheetModel(String name, List<List<String>> rows) {
            this(name, rows, CellStyle.STYLE_NONE);
        }

        public SheetModel(String name, List<List<String>> rows, int styleCode) {
            this.name = name;
            this.styleCode = styleCode;
            if (rows != null && !rows.isEmpty()) {
                List<RowModel> list = new ArrayList(rows.size());
                rows.forEach(row -> {
                    list.add(new RowModel(row));
                });
                this.rows = list;
                this.maxCellNum = getMaxCellNum(rows);
                this.maxRowNum = rows.size();
            }
        }

        private int getMaxCellNum(List<List<String>> rows) {
            int maxCellNum = 0;
            for (int i = 0; i < rows.size(); i++) {
                if (i == 5) {
                    return maxCellNum;
                }
                int size = rows.get(i).size();
                if (size > maxCellNum) {
                    maxCellNum = size;
                }
            }
            return maxCellNum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<RowModel> getRows() {
            return rows;
        }

        public void setRows(List<RowModel> rows) {
            this.rows = rows;
        }

        public int getStyleCode() {
            return styleCode;
        }

        public void setStyleCode(int styleCode) {
            this.styleCode = styleCode;
        }

        public int getMaxCellNum() {
            return maxCellNum;
        }

        public void setMaxCellNum(int maxCellNum) {
            this.maxCellNum = maxCellNum;
        }

        public int getMaxRowNum() {
            return maxRowNum;
        }

        public void setMaxRowNum(int maxRowNum) {
            this.maxRowNum = maxRowNum;
        }
    }

    class RowModel {
        private List<CellModel> cells;
        private int styleCode;

        public RowModel(List<String> values) {
            this(values, CellStyle.STYLE_NONE);
        }

        public RowModel(List<String> values, int styleCode) {
            List cells = new ArrayList(values.size());
            for (String value : values) {
                cells.add(new CellModel(value));
            }
            this.cells = cells;
            this.styleCode = styleCode;
        }

        public List<CellModel> getCells() {
            return cells;
        }

        public void setCells(List<CellModel> cells) {
            this.cells = cells;
        }

        public int getStyleCode() {
            return styleCode;
        }

        public void setStyleCode(int styleCode) {
            this.styleCode = styleCode;
        }
    }

    class CellModel {
        private String value;
        private int styleCode;

        public CellModel(String value) {
            this(value, CellStyle.STYLE_NONE);
        }

        public CellModel(String value, int styleCode) {
            this.value = value;
            this.styleCode = styleCode;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getStyleCode() {
            return styleCode;
        }

        public void setStyleCode(int styleCode) {
            this.styleCode = styleCode;
        }
    }

}

class CellStyle {
    //常规
    public static int STYLE_NONE = 0;
    //标题
    public static int STYLE_TITLE = 1;
    //首行
    public static int STYLE_TOP = 2;
    //加红
    public static int STYLE_RED = 3;
    //合并单元格
    public static int STYLE_3 = 3;


    public static HSSFCellStyle getStyle(HSSFWorkbook workbook, int code) {
        if (code == STYLE_TITLE) {
            return getColumnTitleStyle(workbook);
        }
        if (code == STYLE_TOP) {
            return getColumnTopStyle(workbook);
        }
        if (code == STYLE_NONE) {
            return getStyle(workbook);
        }
        if (code == STYLE_RED) {
            return getSpecialStyle(workbook);
        }
        return null;
    }


    /*
     * 列头单元格样式
     */
    public static HSSFCellStyle getColumnTitleStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 12);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    /*
     * 列头单元格样式
     */
    public static HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    /*
  * 列数据信息单元格样式
  */
    public static HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        //font.setFontHeightInPoints((short)10);
        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }


    private static HSSFCellStyle getSpecialStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体red
        font.setColor(HSSFColor.RED.index);
        //设置字体名字
        font.setFontName("Courier New");
        HSSFCellStyle style = getStyle(workbook);
        style.setFont(font);
        return style;
    }
}
