package org.jeecg.modules.jeecgmodulejxcmanage;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.modules.jxcmanage.entity.TBPandian;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration
class JeecgModuleJxcManageApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(JeecgModuleJxcManageApplicationTests.class);


    @Test
    public void test3() throws IOException {
        String fileName = "D:\\zshare\\pandian.xlsx";
        InputStream inp = new FileInputStream(fileName);
        excelFilePreProcess(inp, fileName);
    }

    /**
     * 文件预处理
     */
    private static void excelFilePreProcess(InputStream inp, String fileName) throws IOException {
        Workbook workbook = WorkbookFactory.create(inp);
        Sheet sheet = workbook.getSheetAt(0);

        List<Integer> toDelete = new ArrayList<>();

        for (Row row : sheet) {
            int r = row.getRowNum();
            for (Cell cell : row) {
                if (r <= 2) {
                    if (r == 0) {
                        toDelete.add(r);
                    }
                    if (r == 1 && cell.getCellType() == CellType.STRING && cell.getStringCellValue().contains("合/残")) {
                        cell.setCellValue("状态（合/残）");
                    }
                    continue;
                }
                if (cell.getCellType() == CellType.STRING && (cell.getStringCellValue().contains("残次(M") || cell.getStringCellValue().contains("总序号"))) {
                    toDelete.add(r);
                    break;
                }
            }
        }

        for (Integer r : toDelete) {
            Row row = sheet.getRow(r);
            if (row != null) sheet.removeRow(row);
        }
        removeBlankRows(sheet);
        splitMergedCellsByColumnByName(sheet, "新款号");


        FileOutputStream os = new FileOutputStream(fileName);
        workbook.write(os);
        os.close();
        workbook.close();
    }


    /**
     * 删除空白行（整行所有单元格都为空或空字符串时删除）
     */
    public static void removeBlankRows(Sheet sheet) {
        int first = sheet.getFirstRowNum();
        int last = sheet.getLastRowNum();

        // 1) 收集所有要删除的行号（升序）
        List<Integer> toDelete = new ArrayList<>();
        toDelete.add(0);
        for (int i = first; i <= last; i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) {
                toDelete.add(i);
            }
        }
        if (toDelete.isEmpty()) return;

        // 2) 先移除所有会与这些删除行冲突的 merged regions
        //    从后向前遍历 mergedRegions 并删除与 toDelete 交叠的 region
        for (int j = sheet.getNumMergedRegions() - 1; j >= 0; j--) {
            CellRangeAddress range = sheet.getMergedRegion(j);
            boolean overlap = false;
            for (int r : toDelete) {
                if (range.getFirstRow() <= r && range.getLastRow() >= r) {
                    overlap = true;
                    break;
                }
            }
            if (overlap) {
                sheet.removeMergedRegion(j);
            }
        }

        // 3) 按降序删除行并 shiftRows（这样 shift 不会影响尚未处理的行索引）
        for (int k = toDelete.size() - 1; k >= 0; k--) {
            int r = toDelete.get(k);
            Row row = sheet.getRow(r);
            if (row != null) {
                sheet.removeRow(row);
            }
            if (r < sheet.getLastRowNum()) {
                sheet.shiftRows(r + 1, sheet.getLastRowNum(), -1);
            }
        }
    }


    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                if (cell.getCellType() == CellType.STRING &&
                        !cell.getStringCellValue().trim().isEmpty()) {
                    return false;
                } else if (cell.getCellType() != CellType.STRING) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 根据列名拆分合并单元格并填充值
     */
    private static void splitMergedCellsByColumnByName(Sheet sheet, String columnName) {
        // 假设表头在第 0 行或第 1 行，你可以根据实际情况调整
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return;

        int targetColumnIndex = -1;
        for (Cell cell : headerRow) {
            if (cell.getCellType() == CellType.STRING && columnName.equals(cell.getStringCellValue().trim())) {
                targetColumnIndex = cell.getColumnIndex();
                break;
            }
        }

        if (targetColumnIndex == -1) {
            // 没找到列名
            return;
        }

        // 遍历所有合并区域，从后往前
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.getFirstColumn() == targetColumnIndex) {
                String value = sheet.getRow(range.getFirstRow()).getCell(targetColumnIndex).getStringCellValue();

                // 填充拆分后的每一行
                for (int r = range.getFirstRow(); r <= range.getLastRow(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) row = sheet.createRow(r);
                    Cell cell = row.getCell(targetColumnIndex);
                    if (cell == null) cell = row.createCell(targetColumnIndex, CellType.STRING);
                    cell.setCellValue(value);
                }

                // 移除合并单元格
                sheet.removeMergedRegion(i);
            }
        }
    }


}
