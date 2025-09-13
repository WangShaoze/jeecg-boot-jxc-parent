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
import org.jeecg.modules.jxcmanage.entity.TBKm;
import org.jeecg.modules.jxcmanage.service.ITBKmService;

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
 * @Description: 库位码管理
 * @Author: jeecg-boot
 * @Date:   2025-09-11
 * @Version: V1.0
 */
@Tag(name="库位码管理")
@RestController
@RequestMapping("/jxcmanage/tBKm")
@Slf4j
public class TBKmController extends JeecgController<TBKm, ITBKmService> {
	@Autowired
	private ITBKmService tBKmService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBKm
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "库位码管理-分页列表查询")
	@Operation(summary="库位码管理-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "jxcmanage/TBKmList")    // 自己只可以看自己的数据
	public Result<IPage<TBKm>> queryPageList(TBKm tBKm,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBKm> queryWrapper = QueryGenerator.initQueryWrapper(tBKm, req.getParameterMap());
		Page<TBKm> page = new Page<TBKm>(pageNo, pageSize);
		IPage<TBKm> pageList = tBKmService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBKm
	 * @return
	 */
	@AutoLog(value = "库位码管理-添加")
	@Operation(summary="库位码管理-添加")
	@RequiresPermissions("jxcmanage:t_b_km:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBKm tBKm) {
		tBKmService.save(tBKm);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBKm
	 * @return
	 */
	@AutoLog(value = "库位码管理-编辑")
	@Operation(summary="库位码管理-编辑")
	@RequiresPermissions("jxcmanage:t_b_km:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBKm tBKm) {
		tBKmService.updateById(tBKm);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "库位码管理-通过id删除")
	@Operation(summary="库位码管理-通过id删除")
	@RequiresPermissions("jxcmanage:t_b_km:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBKmService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "库位码管理-批量删除")
	@Operation(summary="库位码管理-批量删除")
	@RequiresPermissions("jxcmanage:t_b_km:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBKmService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "库位码管理-通过id查询")
	@Operation(summary="库位码管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBKm> queryById(@RequestParam(name="id",required=true) String id) {
		TBKm tBKm = tBKmService.getById(id);
		if(tBKm==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBKm);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBKm
    */
    @RequiresPermissions("jxcmanage:t_b_km:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBKm tBKm) {
        return super.exportXls(request, tBKm, TBKm.class, "库位码管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("jxcmanage:t_b_km:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBKm.class);
    }

}
