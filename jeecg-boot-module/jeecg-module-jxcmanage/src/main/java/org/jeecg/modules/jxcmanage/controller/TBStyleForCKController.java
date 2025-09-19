package org.jeecg.modules.jxcmanage.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.jxcmanage.entity.TBGoodsForCK;
import org.jeecg.modules.jxcmanage.entity.TBStyleForCK;
import org.jeecg.modules.jxcmanage.vo.TBStyleForCKPage;
import org.jeecg.modules.jxcmanage.service.ITBStyleForCKService;
import org.jeecg.modules.jxcmanage.service.ITBGoodsForCKService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 仓库
 * @Author: jeecg-boot
 * @Date: 2025-09-13
 * @Version: V1.0
 */
@Tag(name = "仓库")
@RestController
@RequestMapping("/jxcmanage/tBStyleForCK")
@Slf4j
public class TBStyleForCKController {
    @Autowired
    private ITBStyleForCKService tBStyleForCKService;
    @Autowired
    private ITBGoodsForCKService tBGoodsForCKService;

    /**
     * 分页列表查询
     *
     * @param tBStyleForCK
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "仓库-分页列表查询")
    @Operation(summary = "仓库-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBStyleForCK>> queryPageList(TBStyleForCK tBStyleForCK,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<TBStyleForCK> queryWrapper = QueryGenerator.initQueryWrapper(tBStyleForCK, req.getParameterMap());
        Page<TBStyleForCK> page = new Page<TBStyleForCK>(pageNo, pageSize);
        IPage<TBStyleForCK> pageList = tBStyleForCKService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBStyleForCKPage
     * @return
     */
    @AutoLog(value = "仓库-添加")
    @Operation(summary = "仓库-添加")
    @RequiresPermissions("jxcmanage:t_b_style:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBStyleForCKPage tBStyleForCKPage) {
        TBStyleForCK tBStyleForCK = new TBStyleForCK();
        BeanUtils.copyProperties(tBStyleForCKPage, tBStyleForCK);
        tBStyleForCKService.saveMain(tBStyleForCK, tBStyleForCKPage.getGoodsList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBStyleForCKPage
     * @return
     */
    @AutoLog(value = "仓库-编辑")
    @Operation(summary = "仓库-编辑")
    @RequiresPermissions("jxcmanage:t_b_style:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBStyleForCKPage tBStyleForCKPage) {
        TBStyleForCK tBStyleForCK = new TBStyleForCK();
        BeanUtils.copyProperties(tBStyleForCKPage, tBStyleForCK);
        TBStyleForCK tBStyleForCKEntity = tBStyleForCKService.getById(tBStyleForCK.getId());
        if (tBStyleForCKEntity == null) {
            return Result.error("未找到对应数据");
        }
		tBStyleForCKService.updateMain(tBStyleForCK, tBStyleForCKPage.getGoodsList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "仓库-通过id删除")
    @Operation(summary = "仓库-通过id删除")
    @RequiresPermissions("jxcmanage:t_b_style:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBStyleForCKService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "仓库-批量删除")
    @Operation(summary = "仓库-批量删除")
    @RequiresPermissions("jxcmanage:t_b_style:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBStyleForCKService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "仓库-通过id查询")
    @Operation(summary = "仓库-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBStyleForCK> queryById(@RequestParam(name = "id", required = true) String id) {
        TBStyleForCK tBStyleForCK = tBStyleForCKService.getById(id);
        if (tBStyleForCK == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBStyleForCK);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "货品记录-通过主表ID查询")
    @Operation(summary = "货品记录-通过主表ID查询")
    @GetMapping(value = "/queryTBGoodsForCKByMainId")
    public Result<IPage<TBGoodsForCK>> queryTBGoodsForCKListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<TBGoodsForCK> tBGoodsForCKList = tBGoodsForCKService.selectByMainId(id);
        IPage<TBGoodsForCK> page = new Page<>();
        page.setRecords(tBGoodsForCKList);
        page.setTotal(tBGoodsForCKList.size());
        return Result.OK(page);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBStyleForCK
     */
    @RequiresPermissions("jxcmanage:t_b_style:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBStyleForCK tBStyleForCK) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<TBStyleForCK> queryWrapper = QueryGenerator.initQueryWrapper(tBStyleForCK, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //Step.2 获取导出数据
        List<TBStyleForCK> tBStyleForCKList = tBStyleForCKService.list(queryWrapper);

        // Step.3 组装pageList
        List<TBStyleForCKPage> pageList = new ArrayList<TBStyleForCKPage>();
        for (TBStyleForCK main : tBStyleForCKList) {
            TBStyleForCKPage vo = new TBStyleForCKPage();
            BeanUtils.copyProperties(main, vo);
            List<TBGoodsForCK> tBGoodsForCKList = tBGoodsForCKService.selectByMainId(main.getId());
            vo.setGoodsList(tBGoodsForCKList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "仓库列表");
        mv.addObject(NormalExcelConstants.CLASS, TBStyleForCKPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("仓库数据", "导出人:" + sysUser.getRealname(), "仓库"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("jxcmanage:t_b_style:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<TBStyleForCKPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TBStyleForCKPage.class, params);
                for (TBStyleForCKPage page : list) {
                    TBStyleForCK po = new TBStyleForCK();
                    BeanUtils.copyProperties(page, po);
                    tBStyleForCKService.saveMain(po, page.getGoodsList());
                }
                return Result.OK("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.OK("文件导入失败！");
    }

}
