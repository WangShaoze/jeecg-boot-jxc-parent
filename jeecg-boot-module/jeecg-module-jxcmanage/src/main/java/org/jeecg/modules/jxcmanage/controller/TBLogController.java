package org.jeecg.modules.jxcmanage.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.jxcmanage.entity.TBLog;
import org.jeecg.modules.jxcmanage.service.ITBLogService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 业务日志管理
 * @Author: jeecg-boot
 * @Date: 2025-09-11
 * @Version: V1.0
 */
@Tag(name = "业务日志管理")
@RestController
@RequestMapping("/jxcmanage/tBLog")
@Slf4j
public class TBLogController extends JeecgController<TBLog, ITBLogService> {
    @Autowired
    private ITBLogService tBLogService;

    /**
     * 分页列表查询
     *
     * @param tBLog
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "业务日志管理-分页列表查询")
    @Operation(summary = "业务日志管理-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "jxcmanage/TBLogList")    // 自己只可以看自己的数据
    public Result<IPage<TBLog>> queryPageList(TBLog tBLog,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<TBLog> queryWrapper = QueryGenerator.initQueryWrapper(tBLog, req.getParameterMap());
        Page<TBLog> page = new Page<TBLog>(pageNo, pageSize);
        IPage<TBLog> pageList = tBLogService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBLog
     * @return
     */
    @AutoLog(value = "业务日志管理-添加")
    @Operation(summary = "业务日志管理-添加")
    @RequiresPermissions("jxcmanage:t_b_log:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBLog tBLog) {
        tBLogService.save(tBLog);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBLog
     * @return
     */
    @AutoLog(value = "业务日志管理-编辑")
    @Operation(summary = "业务日志管理-编辑")
    @RequiresPermissions("jxcmanage:t_b_log:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBLog tBLog) {
        tBLogService.updateById(tBLog);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "业务日志管理-通过id删除")
    @Operation(summary = "业务日志管理-通过id删除")
    @RequiresPermissions("jxcmanage:t_b_log:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBLogService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "业务日志管理-批量删除")
    @Operation(summary = "业务日志管理-批量删除")
    @RequiresPermissions("jxcmanage:t_b_log:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBLogService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "业务日志管理-通过id查询")
    @Operation(summary = "业务日志管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBLog> queryById(@RequestParam(name = "id", required = true) String id) {
        TBLog tBLog = tBLogService.getById(id);
        if (tBLog == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBLog);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBLog
     */
    @RequiresPermissions("jxcmanage:t_b_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBLog tBLog) {
        return super.exportXls(request, tBLog, TBLog.class, "业务日志管理");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("jxcmanage:t_b_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBLog.class);
    }

}
