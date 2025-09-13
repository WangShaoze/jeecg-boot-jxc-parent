package org.jeecg.modules.jxcmanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.jxcmanage.entity.ExcelColumn;
import org.jeecg.modules.jxcmanage.entity.TBGoods;
import org.jeecg.modules.jxcmanage.entity.TBLog;
import org.jeecg.modules.jxcmanage.entity.TBStyle;
import org.jeecg.modules.jxcmanage.mapper.TBGoodsMapper;
import org.jeecg.modules.jxcmanage.service.ITBGoodsService;
import org.jeecg.modules.jxcmanage.service.ITBLogService;
import org.jeecg.modules.jxcmanage.service.ITBStyleService;
import org.jeecg.modules.jxcmanage.utils.ExcelExportUtil;
import org.jeecg.modules.jxcmanage.utils.FileNameValidator;
import org.jeecg.modules.jxcmanage.utils.RandomUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description: 货品表
 * @Author: jeecg-boot
 * @Date: 2025-09-12
 * @Version: V1.0
 */
@Service
@Slf4j
public class TBGoodsServiceImpl extends ServiceImpl<TBGoodsMapper, TBGoods> implements ITBGoodsService {
    @Lazy
    @Autowired
    private ITBStyleService styleService;

    @Lazy
    @Autowired
    private ITBLogService logService;


    @Override
    public ResponseEntity<byte[]> exportPandDianXls(HttpServletRequest request, String selectRowArr, String title) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        log.info("当前用户信息:\n{}", JSON.toJSONString(sysUser));

        // Step.1 组装查询条件
        QueryWrapper<TBGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", sysUser.getUsername());
        queryWrapper.eq("inbound_status", "0");  // 库存为带入库的数据

        // 过滤选中数据
        if (oConvertUtils.isNotEmpty(selectRowArr)) {
            List<String> selectionList = Arrays.asList(selectRowArr.split(","));
            if (!selectionList.isEmpty()) {
                queryWrapper.in("id", selectionList);
            }
        }
        // Step.2 获取导出数据
        List<TBGoods> exportList = list(queryWrapper);

        try {
            byte[] excelBytes = outputExcelData(exportList);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            // 精确到十分秒（100 ms）的当前时间字符串
            String tenthSecTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis() / 100 * 100),
                    ZoneId.systemDefault()
            ).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
            String fileName = "盘点货品导出表_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";


            // 需要把文件上传到minio中，返回下载链接
            String fileUrl = MinioUtil.upload(fileName, excelBytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            // 需要把日志输出到相关的文件中去
            TBLog tbLog = new TBLog();
            tbLog.setLogContent(fileName);
            tbLog.setRemarks("盘点 导出");
            tbLog.setFileUrl(fileUrl);
            logService.save(tbLog);

            // 添加下载头信息，让浏览器自动下载
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("exportPandDianXls: 导出失败！错误原因:", e);
            throw new RuntimeException("导出失败！请联系管理员！");
        }
    }

    private byte[] outputExcelData(List<TBGoods> exportList) {
        // 编写excel导出逻辑
        List<ExcelColumn> columns = new ArrayList<>();
        ExcelColumn noColumn = new ExcelColumn("总序号", "index");
        noColumn.setWidth(10);
        noColumn.setAlignment(HorizontalAlignment.CENTER);
        columns.add(noColumn);

        columns.add(new ExcelColumn("销售单号", "salesOrderNo"));

        // 对日期列进行特殊格式设置
        ExcelColumn dateColumn = new ExcelColumn("销售日期", "salesDate");
        dateColumn.setDataFormat("yyyy-MM-dd HH:mm:ss");
        dateColumn.setAlignment(HorizontalAlignment.CENTER);
        dateColumn.setWidth(20);
        columns.add(dateColumn);

        columns.add(new ExcelColumn("订单号", "orderNo"));
        columns.add(new ExcelColumn("款号", "styleNo"));
        columns.add(new ExcelColumn("款式类别", "styleCategory"));
        columns.add(new ExcelColumn("货号", "productNo"));
        columns.add(new ExcelColumn("客户款号", "newStyleNo"));
        columns.add(new ExcelColumn("客胚名称", "itemName"));


        ExcelColumn silverWeightCol = new ExcelColumn("银重", "silverWeight");
        silverWeightCol.setAlignment(HorizontalAlignment.CENTER);
        silverWeightCol.setWidth(10);
        silverWeightCol.setDataFormat("0.00");

        ExcelColumn goldWeightCol = new ExcelColumn("金重", "goldWeight");
        goldWeightCol.setAlignment(HorizontalAlignment.CENTER);
        goldWeightCol.setWidth(10);
        goldWeightCol.setDataFormat("0.00");

        ExcelColumn totalWeightCol = new ExcelColumn("总重", "totalWeight");
        totalWeightCol.setAlignment(HorizontalAlignment.CENTER);
        totalWeightCol.setWidth(10);
        totalWeightCol.setDataFormat("0.00");

        columns.add(silverWeightCol);
        columns.add(goldWeightCol);
        columns.add(totalWeightCol);

        columns.add(new ExcelColumn("证书编号", "certificateNo"));
        columns.add(new ExcelColumn("证书机构", "certificateOrg"));
        columns.add(new ExcelColumn("字印", "seal"));
        columns.add(new ExcelColumn("查询地址", "queryUrl"));
        columns.add(new ExcelColumn("序号", "sequenceNo"));
        columns.add(new ExcelColumn("状态", "status"));
        columns.add(new ExcelColumn("备注", "remarks"));

        return ExcelExportUtil.exportExcel("盘点货品", columns, exportList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importPandianExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String fileName = file.getOriginalFilename();

            // 精确到十分秒（100 ms）的当前时间字符串
            String tenthSecTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis() / 100 * 100),
                    ZoneId.systemDefault()
            ).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
            String newFileName;
            if (!StringUtils.isEmpty(fileName)) {
                if (FileNameValidator.isValidFileName(fileName)) {
                    newFileName = "导入" + FileNameValidator.getBaseName(fileName) + "_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";
                } else {
                    newFileName = "导入" + FileNameValidator.sanitizeFileName(fileName) + "_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";
                }
            } else {
                newFileName = "导入盘点货品表_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";
            }
            // 需要把文件上传到minio中，返回下载链接
            String fileUrl = MinioUtil.upload(file, newFileName, null, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


            // 需要把日志输出到相关的文件中去
            TBLog tbLog = new TBLog();
            tbLog.setLogContent(newFileName);
            tbLog.setRemarks("盘点-导入");
            tbLog.setFileUrl(fileUrl);
            logService.save(tbLog);


            // 缓存
            List<TBStyle> styleList = styleService.getBaseMapper().selectList(new QueryWrapper<>());
            Map<String, TBStyle> styleMap = new HashMap<>();
            for (TBStyle style : styleList) {
                styleMap.put(style.getStyleNo(), style);
            }


            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setSheetName("Sheet1");
            try {
                InputStream inp = file.getInputStream();
                // 文件预处理
                // 重新生成流用于后续业务
                InputStream forLaterUse = new ByteArrayInputStream(excelFilePreProcess(inp));
                List<TBGoods> list = ExcelImportUtil.importExcel(forLaterUse, TBGoods.class, params);

                list.forEach(i -> {
                    i.setStyleId(styleMap.get(i.getNewStyleNo()).getId());  // 获取该获取的款式id
                    i.setInboundStatus("0");  // 状态是待入库
                });

                //List<TBGoods> list = ExcelImportUtil.importExcel(file.getInputStream(), TBGoods.class, params);
                long start = System.currentTimeMillis();
                saveBatch(list);
                log.info("消耗时间{}毫秒", System.currentTimeMillis() - start);
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if (msg != null && msg.contains("Duplicate entry")) {
                    return Result.error("文件导入失败:有重复数据！");
                } else {
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error("导入盘点数据时文件流关闭失败！", e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 文件预处理
     */
    private static byte[] excelFilePreProcess(InputStream inp) throws IOException {
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
        // 将处理后的 Excel 写入内存流，而不是文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray(); // 后续可以重新生成 InputStream
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

}
