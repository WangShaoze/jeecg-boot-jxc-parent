package org.jeecg.modules.jxcmanage.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.jxcmanage.entity.TBGoods;
import org.jeecg.modules.jxcmanage.entity.TBKm;
import org.jeecg.modules.jxcmanage.service.ITBGoodsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.jxcmanage.service.ITBKmService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 货品表
 * @Author: jeecg-boot
 * @Date: 2025-09-12
 * @Version: V1.0
 */
@Tag(name = "货品表")
@RestController
@RequestMapping("/jxcmanage/tBGoods")
@Slf4j
public class TBGoodsController extends JeecgController<TBGoods, ITBGoodsService> {
    @Autowired
    private ITBGoodsService tBGoodsService;

    @Autowired
    private ITBKmService kmService;


    /**
     * selectKm
     *
     * @param id
     * @param kmId
     */
    @AutoLog(value = "货品表-盘点-选库未")
    @Operation(summary = "货品表-盘点-选库未")
    @GetMapping(value = "/selectKm")
    public Result<String> selectKm(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "kmId") String kmId
    ) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(kmId)) {
            return Result.error("请准正确传入参数！");
        }
        TBKm tbKm = kmService.getById(kmId);
        if (tbKm == null) {
            return Result.error("未找到库玛");
        }
        TBGoods tbGoods = tBGoodsService.getById(id);
        if (tbGoods == null) {
            return Result.error("未找到该记录！");
        }
        tbGoods.setKmId(kmId);
        tbGoods.setKmValue(tbKm.getKm());
        try {
            boolean success = tBGoodsService.saveOrUpdate(tbGoods);
            if (!success) {
                return Result.error("保存或者更新失败！请重试！");
            }
            log.info("selectKm: 库位选择成功！");
            return Result.ok("库位选择成功！");
        } catch (Exception e) {
            log.error("selectKm:库位选择失败! 错误原因: {}", e.getMessage());
            return Result.error("库位选择失败!请联系管理员！");
        }
    }


    /**
     * batchSelectKm
     *
     * @param ids
     * @param kmId
     */
    @AutoLog(value = "货品表-盘点-批量选库未")
    @Operation(summary = "货品表-盘点-批量选库未")
    @GetMapping(value = "/batchSelectKm")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchSelectKm(
            @RequestParam(name = "ids") String ids,
            @RequestParam(name = "kmId") String kmId
    ) {
        if (StringUtils.isEmpty(ids) || StringUtils.isEmpty(kmId)) {
            return Result.error("请准正确传入参数！");
        }
        String[] idArray = ids.split(",");
        List<String> idList = Arrays.asList(idArray);
        if (idList.isEmpty()) {
            return Result.error("请选择记录后重试！");
        }
        TBKm tbKm = kmService.getById(kmId);
        if (tbKm == null) {
            return Result.error("未找到库玛");
        }
        QueryWrapper<TBGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        List<TBGoods> tbGoodsList = tBGoodsService.getBaseMapper().selectList(queryWrapper);
        if (tbGoodsList.isEmpty() || tbGoodsList.size() != idList.size()) {
            return Result.error("数据不齐！数据中存在部分数据不在数据库中！");
        }

        tbGoodsList.forEach(tbGoods -> {
            tbGoods.setKmId(kmId);
            tbGoods.setKmValue(tbKm.getKm());
        });
        try {
            boolean success = tBGoodsService.saveOrUpdateBatch(tbGoodsList);
            if (!success) {
                return Result.error("保存或者更新失败！请重试！");
            }
            log.info("batchSelectKm: 库位批量选择成功！");
            return Result.ok("库位批量选择成功！");
        } catch (Exception e) {
            log.error("batchSelectKm:库位批量选择失败！错误原因:{} ", e.getMessage());
            throw new RuntimeException("库位批量选择失败！请联系管理员！");
        }

    }


    /**
     * inbound
     *
     * @param id
     */
    @AutoLog(value = "货品表-盘点-入库")
    @Operation(summary = "货品表-盘点-入库")
    @GetMapping(value = "/inbound")
    public Result<String> inbound(
            @RequestParam(name = "id") String id
    ) {
        if (StringUtils.isEmpty(id)) {
            return Result.error("请正确传入参数！");
        }
        TBGoods tbGoods = tBGoodsService.getById(id);
        if (tbGoods == null) {
            return Result.error("该记录不存在！");
        }
        if (StringUtils.isEmpty(tbGoods.getKmId()) || StringUtils.isEmpty(tbGoods.getKmValue())) {
            return Result.error("该记录没有库位！");
        }
        tbGoods.setInboundStatus("1");  // 在库
        // TODO 更新查询地址
        try {
            boolean success = tBGoodsService.saveOrUpdate(tbGoods);
            if (!success) {
                return Result.error("入库失败！请重试！");
            }
            log.info("inbound: 入库成功！");
            return Result.ok("入库成功！");
        } catch (Exception e) {
            log.error("inbound: 入库失败！错误原因: {}", e.getMessage());
            return Result.error("入库失败！请联系管理员！");
        }


    }


    /**
     * batchInbound
     *
     * @param ids
     */
    @AutoLog(value = "货品表-盘点-批量入库")
    @Operation(summary = "货品表-盘点-批量入库")
    @GetMapping(value = "/batchInbound")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchInbound(
            @RequestParam(name = "ids") String ids
    ) {
        if (StringUtils.isEmpty(ids)) {
            return Result.error("请正确传入参数！");
        }
        String[] idArray = ids.split(",");
        List<String> idList = Arrays.asList(idArray);
        QueryWrapper<TBGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        List<TBGoods> tbGoodsList = tBGoodsService.getBaseMapper().selectList(queryWrapper);
        if (tbGoodsList.isEmpty() || tbGoodsList.size() != idList.size()) {
            return Result.error("数据不齐！数据中存在部分数据不在数据库中！");
        }
        for (TBGoods tbGoods : tbGoodsList) {
            if (StringUtils.isEmpty(tbGoods.getKmId()) || StringUtils.isEmpty(tbGoods.getKmValue())) {
                return Result.error("部分记录没有选择库未！请检查！");
            }
            tbGoods.setInboundStatus("1");  // 在库
            // TODO 更新查询地址
        }
        try {
            boolean success = tBGoodsService.saveOrUpdateBatch(tbGoodsList);
            if (!success) {
                return Result.error("批量入库失败！请重试！");
            }
            log.info("batchInbound: 批量入库成功！");
            return Result.ok("批量入库成功！");
        } catch (Exception e) {
            log.error("batchInbound :批量入库失败！错误原因:{}", e.getMessage());
            throw new RuntimeException("批量入库失败！请联系管理员！");
        }
    }


    /**
     * 分页列表查询
     *
     * @param tBGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "货品表-分页列表查询")
    @Operation(summary = "货品表-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "jxcmanage/TBGoodsList")
    public Result<IPage<TBGoods>> queryPageList(TBGoods tBGoods,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("inboundStatus", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("salesStatus", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<TBGoods> queryWrapper = QueryGenerator.initQueryWrapper(tBGoods, req.getParameterMap(), customeRuleMap);
        Page<TBGoods> page = new Page<TBGoods>(pageNo, pageSize);
        IPage<TBGoods> pageList = tBGoodsService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBGoods
     * @return
     */
    @AutoLog(value = "货品表-添加")
    @Operation(summary = "货品表-添加")
    @RequiresPermissions("jxcmanage:t_b_goods:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBGoods tBGoods) {
        tBGoodsService.save(tBGoods);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBGoods
     * @return
     */
    @AutoLog(value = "货品表-编辑")
    @Operation(summary = "货品表-编辑")
    @RequiresPermissions("jxcmanage:t_b_goods:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBGoods tBGoods) {
        tBGoodsService.updateById(tBGoods);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "货品表-通过id删除")
    @Operation(summary = "货品表-通过id删除")
    @RequiresPermissions("jxcmanage:t_b_goods:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBGoodsService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "货品表-批量删除")
    @Operation(summary = "货品表-批量删除")
    @RequiresPermissions("jxcmanage:t_b_goods:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBGoodsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "货品表-通过id查询")
    @Operation(summary = "货品表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBGoods> queryById(@RequestParam(name = "id", required = true) String id) {
        TBGoods tBGoods = tBGoodsService.getById(id);
        if (tBGoods == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBGoods);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBGoods
     */
    @RequiresPermissions("jxcmanage:t_b_goods:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBGoods tBGoods) {
        return super.exportXls(request, tBGoods, TBGoods.class, "货品表");
    }

    /**
     * 自定义导出excel
     *
     * @param request
     * @param selectRowArr
     */
    @RequiresPermissions("jxcmanage:t_b_goods:exportPandDianXls")
    @RequestMapping(value = "/exportPandDianXls")
    public ResponseEntity<byte[]> exportPandDianXls(HttpServletRequest request, @RequestParam(name = "selectRowArr") String selectRowArr) {
        return tBGoodsService.exportPandDianXls(request, selectRowArr, "货品表");
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("jxcmanage:t_b_goods:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBGoods.class);
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("jxcmanage:t_b_goods:importPandianExcel")
    @RequestMapping(value = "/importPandianExcel", method = RequestMethod.POST)
    public Result<?> importPandianExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return tBGoodsService.importPandianExcel(request, response);
    }

}
