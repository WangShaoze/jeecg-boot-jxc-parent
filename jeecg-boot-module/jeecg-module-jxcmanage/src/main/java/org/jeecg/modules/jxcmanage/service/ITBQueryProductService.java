package org.jeecg.modules.jxcmanage.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.jxcmanage.entity.TBQueryProduct;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 商品查询
 * @Author: jeecg-boot
 * @Date:   2025-09-19
 * @Version: V1.0
 */
public interface ITBQueryProductService extends IService<TBQueryProduct> {
    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<TBQueryProduct> clazz);

}
