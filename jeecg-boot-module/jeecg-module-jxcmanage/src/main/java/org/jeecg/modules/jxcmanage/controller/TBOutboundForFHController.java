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
import org.jeecg.modules.jxcmanage.entity.TBGoodsForFH;
import org.jeecg.modules.jxcmanage.entity.TBOutboundForFH;
import org.jeecg.modules.jxcmanage.vo.TBOutboundForFHPage;
import org.jeecg.modules.jxcmanage.service.ITBOutboundForFHService;
import org.jeecg.modules.jxcmanage.service.ITBGoodsForFHService;
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
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2025-09-14
 * @Version: V1.0
 */
@Tag(name="出库表")
@RestController
@RequestMapping("/jxcmanage/tBOutboundForFH")
@Slf4j
public class TBOutboundForFHController {
	@Autowired
	private ITBOutboundForFHService tBOutboundForFHService;
	@Autowired
	private ITBGoodsForFHService tBGoodsForFHService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBOutboundForFH
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "出库表-分页列表查询")
	@Operation(summary="出库表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBOutboundForFH>> queryPageList(TBOutboundForFH tBOutboundForFH,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBOutboundForFH> queryWrapper = QueryGenerator.initQueryWrapper(tBOutboundForFH, req.getParameterMap());
		Page<TBOutboundForFH> page = new Page<TBOutboundForFH>(pageNo, pageSize);
		IPage<TBOutboundForFH> pageList = tBOutboundForFHService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBOutboundForFHPage
	 * @return
	 */
	@AutoLog(value = "出库表-添加")
	@Operation(summary="出库表-添加")
    @RequiresPermissions("jxcmanage:t_b_outbound:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBOutboundForFHPage tBOutboundForFHPage) {
		TBOutboundForFH tBOutboundForFH = new TBOutboundForFH();
		BeanUtils.copyProperties(tBOutboundForFHPage, tBOutboundForFH);
		tBOutboundForFHService.saveMain(tBOutboundForFH, tBOutboundForFHPage.getTBGoodsForFHList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBOutboundForFHPage
	 * @return
	 */
	@AutoLog(value = "出库表-编辑")
	@Operation(summary="出库表-编辑")
    @RequiresPermissions("jxcmanage:t_b_outbound:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBOutboundForFHPage tBOutboundForFHPage) {
		TBOutboundForFH tBOutboundForFH = new TBOutboundForFH();
		BeanUtils.copyProperties(tBOutboundForFHPage, tBOutboundForFH);
		TBOutboundForFH tBOutboundForFHEntity = tBOutboundForFHService.getById(tBOutboundForFH.getId());
		if(tBOutboundForFHEntity==null) {
			return Result.error("未找到对应数据");
		}
		tBOutboundForFHService.updateMain(tBOutboundForFH, tBOutboundForFHPage.getTBGoodsForFHList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "出库表-通过id删除")
	@Operation(summary="出库表-通过id删除")
    @RequiresPermissions("jxcmanage:t_b_outbound:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBOutboundForFHService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "出库表-批量删除")
	@Operation(summary="出库表-批量删除")
    @RequiresPermissions("jxcmanage:t_b_outbound:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBOutboundForFHService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出库表-通过id查询")
	@Operation(summary="出库表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBOutboundForFH> queryById(@RequestParam(name="id",required=true) String id) {
		TBOutboundForFH tBOutboundForFH = tBOutboundForFHService.getById(id);
		if(tBOutboundForFH==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBOutboundForFH);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "货品记录-通过主表ID查询")
	@Operation(summary="货品记录-通过主表ID查询")
	@GetMapping(value = "/queryTBGoodsForFHByMainId")
	public Result<IPage<TBGoodsForFH>> queryTBGoodsForFHListByMainId(@RequestParam(name="id",required=true) String id) {
		List<TBGoodsForFH> tBGoodsForFHList = tBGoodsForFHService.selectByMainId(id);
		IPage <TBGoodsForFH> page = new Page<>();
		page.setRecords(tBGoodsForFHList);
		page.setTotal(tBGoodsForFHList.size());
		return Result.OK(page);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBOutboundForFH
    */
    @RequiresPermissions("jxcmanage:t_b_outbound:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBOutboundForFH tBOutboundForFH) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<TBOutboundForFH> queryWrapper = QueryGenerator.initQueryWrapper(tBOutboundForFH, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

     //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
           List<String> selectionList = Arrays.asList(selections.split(","));
           queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<TBOutboundForFH>  tBOutboundForFHList = tBOutboundForFHService.list(queryWrapper);

      // Step.3 组装pageList
      List<TBOutboundForFHPage> pageList = new ArrayList<TBOutboundForFHPage>();
      for (TBOutboundForFH main : tBOutboundForFHList) {
          TBOutboundForFHPage vo = new TBOutboundForFHPage();
          BeanUtils.copyProperties(main, vo);
          List<TBGoodsForFH> tBGoodsForFHList = tBGoodsForFHService.selectByMainId(main.getId());
          vo.setTBGoodsForFHList(tBGoodsForFHList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "出库表列表");
      mv.addObject(NormalExcelConstants.CLASS, TBOutboundForFHPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出库表数据", "导出人:"+sysUser.getRealname(), "出库表"));
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
    @RequiresPermissions("jxcmanage:t_b_outbound:importExcel")
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
              List<TBOutboundForFHPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TBOutboundForFHPage.class, params);
              for (TBOutboundForFHPage page : list) {
                  TBOutboundForFH po = new TBOutboundForFH();
                  BeanUtils.copyProperties(page, po);
                  tBOutboundForFHService.saveMain(po, page.getTBGoodsForFHList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
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
