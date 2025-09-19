package org.jeecg.modules.jxcmanage.controller;

/*
 * ClassName: AppController
 * Package: org.jeecg.modules.jxcmanage.controller
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/19 - 16:08
 * @Version: v1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.jxcmanage.entity.TBQueryProduct;
import org.jeecg.modules.jxcmanage.service.ITBQueryProductService;
import org.jeecg.modules.jxcmanage.vo.ItemInfoVO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "进销存-H5")
@RestController
@RequestMapping("/jxcmanage/h5/api")
@Slf4j
public class AppController {

    @Autowired
    private ITBQueryProductService queryProductService;


    /**
     * 通过货号获取款号信息
     *
     * @param itemNumber 货号
     */
    @AutoLog(value = "进销存-H5-通过货号获取款号信息")
    @Operation(summary = "进销存-H5-通过货号获取款号信息")
    @GetMapping(value = "/getItemInfo")
    public Result<ItemInfoVO> getItemInfo(
            @RequestParam(name = "itemNumber") String itemNumber
    ) {
        if (StringUtils.isEmpty(itemNumber)) {
            return Result.error("参数有误！");
        }
        try {
            QueryWrapper<TBQueryProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("item_number", itemNumber);
            TBQueryProduct queryProduct = queryProductService.getBaseMapper().selectOne(queryWrapper);
            if (queryProduct == null) {
                return Result.error("参数有误！");
            }
            return Result.ok(ItemInfoVO.create(queryProduct));
        } catch (Exception e) {
            log.error("getItemInfo:出错了，原因:{}", e.getMessage());
            return Result.error("查询出错！请联系管理员！");
        }
    }


}
