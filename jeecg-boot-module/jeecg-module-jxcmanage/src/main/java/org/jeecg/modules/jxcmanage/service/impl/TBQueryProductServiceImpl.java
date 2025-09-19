package org.jeecg.modules.jxcmanage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.jxcmanage.entity.TBQueryProduct;
import org.jeecg.modules.jxcmanage.mapper.TBQueryProductMapper;
import org.jeecg.modules.jxcmanage.service.ITBQueryProductService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 商品查询
 * @Author: jeecg-boot
 * @Date: 2025-09-19
 * @Version: V1.0
 */
@Slf4j
@Service
public class TBQueryProductServiceImpl extends ServiceImpl<TBQueryProductMapper, TBQueryProduct> implements ITBQueryProductService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<TBQueryProduct> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<TBQueryProduct> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
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
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }
}
