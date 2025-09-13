package org.jeecg.modules.jxcmanage.utils;

/*
 * ClassName: ExcelExportUtil
 * Package: org.jeecg.modules.jxcmanage.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/3 - 19:04
 * @Version: v1.0
 */

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.modules.jxcmanage.entity.ExcelColumn;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel导出工具类，支持自定义格式
 */
public class ExcelExportUtil {


    /**
     * 导出Excel
     *
     * @param title   表格标题
     * @param columns 列定义
     * @param data    数据列表
     * @return 导出的Excel文件字节数组
     */
    public static <T> byte[] exportExcel(String title, List<ExcelColumn> columns, List<T> data) {
        // 创建工作簿
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 创建工作表
            Sheet sheet = workbook.createSheet(title);

            // 创建标题样式
            CellStyle titleStyle = createTitleStyle(workbook);

            // 创建内容样式
            CellStyle contentStyle = createContentStyle(workbook);

            // 创建标题行
            Row titleRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                ExcelColumn column = columns.get(i);
                Cell cell = titleRow.createCell(i);
                cell.setCellValue(column.getTitle());
                cell.setCellStyle(titleStyle);

                // 设置列宽
                if (column.getWidth() > 0) {
                    sheet.setColumnWidth(i, column.getWidth() * 256); // Excel列宽单位是1/256个字符宽度
                }
            }


            // 填充数据
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1); // 从第二行开始
                T item = data.get(i);

                for (int j = 0; j < columns.size(); j++) {
                    ExcelColumn column = columns.get(j);
                    Cell cell = dataRow.createCell(j);
                    // 获取单元格值
                    Object value;
                    if (j == 0) {
                        value = i + 1;
                    } else {
                        value = getFieldValue(item, column.getField());
                        if (column.getField().equals("status")) {
                            int status = Integer.parseInt((String) value);
                            if (status == 1) {
                                value = "合格";
                            } else {
                                value = "残次";
                            }
                        }
                    }
                    setCellValue(cell, value, column, workbook);
                }
            }

            // 自动调整列宽（对未设置固定宽度的列）
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).getWidth() == -1) {
                    sheet.autoSizeColumn(i);
                }
            }

//            // 填充底部数据
//            List<String> bottomTitle = Arrays.asList("合计总件数", "销售单号", "销售日期", "订单号", "共计款数", "共计类别数", "总重", "", "", "金总重", "银总重", "残品率");
//            List<String> bottomData = Arrays.asList(Integer.toString(data.size()),
//                    getFieldValue(data.get(1), "salesOrderNo").toString(),
//                    dateFormatToStr(getFieldValue(data.get(1), "salesDate")),
//                    getFieldValue(data.get(1), "orderNo").toString(),
//                    String.format("%d款", data.stream().map(t -> getFieldValue(t, "styleNo")).distinct().count()),
//                    String.format("%d类", data.stream().map(t -> getFieldValue(t, "styleCategory")).distinct().count()),
//                    String.format("=SUM(M%d:M%d)", 2, data.size() + 1),
//                    "",
//                    "",
//                    String.format("=SUM(L%d:L%d)", 2, data.size() + 1),
//                    String.format("=SUM(K%d:K%d)", 2, data.size() + 1),
//                    String.format("=COUNTIF(S%d:S%d, \"残次\")/COUNTA(S%d:S%d)", 2, data.size() + 1, 2, data.size() + 1));
//
//            Row bottomTitleRow = sheet.createRow(data.size() + 1);
//            Row bottomDataRow = sheet.createRow(data.size() + 2);
//            for (int c = 0; c < bottomTitle.size(); c++) {
//                Cell cellTile = bottomTitleRow.createCell(c);
//                cellTile.setCellValue(bottomTitle.get(c));
//
//                Cell cellData = bottomDataRow.createCell(c);
//                if (bottomData.contains("=")) {
//                    // 是公式数据
//                    cellData.setCellFormula(bottomData.get(c));
//                    if (bottomTitle.get(c).equals("残品率")) {   // 含有百分号
//                        CellStyle pctStyle = workbook.createCellStyle();
//                        pctStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
//                        cellData.setCellStyle(pctStyle);
//                    } else {
//                        cellData.setCellStyle(contentStyle);
//                    }
//                } else {
//                    cellData.setCellValue(bottomData.get(c));
//                    cellData.setCellStyle(contentStyle);
//                }
//            }
            // 写入输出流
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    private static String dateFormatToStr(Object date) {
        Date date1 = (Date) date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 保持 CST 时区
        return sdf.format(date1);
    }

    /**
     * 创建标题样式
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框
        setBorder(style);
        // 设置背景色
//        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        // 水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * 创建内容样式
     */
    private static CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框
        setBorder(style);
        return style;
    }

    /**
     * 设置单元格边框
     */
    private static void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    /**
     * 通过反射获取对象字段值
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("获取字段值失败: " + fieldName, e);
        }
    }

    /**
     * 设置单元格值，根据数据类型和格式进行处理
     */
    private static void setCellValue(Cell cell, Object value, ExcelColumn column, Workbook workbook) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        // 创建单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        setBorder(cellStyle);
        cellStyle.setAlignment(column.getAlignment());

        // 设置字体
        Font font = workbook.createFont();
        font.setBold(column.isBold());
        cellStyle.setFont(font);

        // 设置数据格式
        if (column.getDataFormat() != null) {
            DataFormat format = workbook.createDataFormat();
            cellStyle.setDataFormat(format.getFormat(column.getDataFormat()));
        }

        cell.setCellStyle(cellStyle);

        // 根据值类型设置单元格内容
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Date) {
            if (column.getDataFormat() != null) {
                cell.setCellValue((Date) value);
            } else {
                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value));
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}

