package org.jeecg.modules.jxcmanage.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.jxcmanage.entity.TBGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 货品表
 * @Author: jeecg-boot
 * @Date: 2025-09-12
 * @Version: V1.0
 */
public interface ITBGoodsService extends IService<TBGoods> {
    ResponseEntity<byte[]> exportPandDianXls(HttpServletRequest request, String selectRowArr, String title);

    Result<?> importPandianExcel(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
