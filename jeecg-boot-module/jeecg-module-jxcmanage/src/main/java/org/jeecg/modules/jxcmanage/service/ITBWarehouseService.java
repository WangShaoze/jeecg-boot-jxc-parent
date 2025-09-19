package org.jeecg.modules.jxcmanage.service;

import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.jxcmanage.entity.TBWarehouse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 仓库管理
 * @Author: jeecg-boot
 * @Date:   2025-09-15
 * @Version: V1.0
 */
public interface ITBWarehouseService extends IService<TBWarehouse> {
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception;


}
