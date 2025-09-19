package org.jeecg.modules.jxcmanage.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.jxcmanage.entity.TBLog;
import org.jeecg.modules.jxcmanage.entity.TBWarehouse;
import org.jeecg.modules.jxcmanage.mapper.TBWarehouseMapper;
import org.jeecg.modules.jxcmanage.service.ITBLogService;
import org.jeecg.modules.jxcmanage.service.ITBWarehouseService;
import org.jeecg.modules.jxcmanage.utils.FileNameValidator;
import org.jeecg.modules.jxcmanage.utils.RandomUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 仓库管理
 * @Author: jeecg-boot
 * @Date: 2025-09-15
 * @Version: V1.0
 */
@Service
@Slf4j
public class TBWarehouseServiceImpl extends ServiceImpl<TBWarehouseMapper, TBWarehouse> implements ITBWarehouseService {
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ITBLogService logService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        log.info("当前用户信息:\n{}", JSON.toJSONString(sysUser));

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();

            String fileName = file.getOriginalFilename();
            // 精确到十分秒（100 ms）的当前时间字符串
            String tenthSecTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis() / 100 * 100),
                    ZoneId.systemDefault()
            ).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
            String newFileName;
            if (!StringUtils.isEmpty(fileName)) {
                if (FileNameValidator.isValidFileName(fileName)) {
                    newFileName = "导入" + FileNameValidator.getBaseName(fileName) + "_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";
                } else {
                    newFileName = "导入" + FileNameValidator.sanitizeFileName(fileName) + "_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";
                }
            } else {
                newFileName = "导入盘点货品表_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".xlsx";
            }
            // 需要把文件上传到minio中，返回下载链接
            String fileUrl = MinioUtil.upload(file, newFileName, null, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


            // 需要把日志输出到相关的文件中去
            TBLog tbLog = new TBLog();
            tbLog.setLogContent(newFileName);
            tbLog.setRemarks("盘点-导入");
            tbLog.setFileUrl(fileUrl);
            logService.save(tbLog);


            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<TBWarehouse> list = ExcelImportUtil.importExcel(file.getInputStream(), TBWarehouse.class, params);

                // 先创建系统用户：：角色默认是: 子仓库管理员 1966334003791331329
                List<TBWarehouse> warehouseList = registerToSysUser(list, sysUser);
                long start = System.currentTimeMillis();
                saveBatch(warehouseList);
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


    @Transactional(rollbackFor = Exception.class)
    public List<TBWarehouse> registerToSysUser(List<TBWarehouse> list, LoginUser sysUser) {
        for (TBWarehouse tbWarehouse : list) {
            try {
                SysUser user = new SysUser();
                String uid = RandomUtil.generate19DigitNumber();
                String username = tbWarehouse.getPhone();
                user.setId(uid);
                user.setCreateTime(new Date());// 设置创建时间
                user.setCreateBy(sysUser.getUsername());
                String salt = oConvertUtils.randomGen(8);
                String passwordEncode = PasswordUtil.encrypt(username, CommonConstant.DEFAULT_PASSWORD, salt);
                user.setSalt(salt);
                user.setUsername(username);
                user.setRealname(tbWarehouse.getWarehouseName());
                user.setPassword(passwordEncode);
                user.setEmail(username + CommonConstant.EMAIL_DEFAULT_SUFFIX);
                user.setPhone(username);
                user.setWorkNo(RandomUtil.randomAlphanumeric6() + RandomUtil.randomAlphanumeric6());
                user.setStatus(CommonConstant.USER_UNFREEZE);
                user.setDelFlag(CommonConstant.DEL_FLAG_0);
                user.setActivitiSync(CommonConstant.ACT_SYNC_1);
                user.setUserIdentity(CommonConstant.USER_IDENTITY_1);
                sysUserService.addUserWithRole(user, "1966334003791331329");//默认临时角色 test

                tbWarehouse.setSysUid(uid);
                tbWarehouse.setSysRole("子仓库管理员");
                tbWarehouse.setSysRoleId("1966334003791331329");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("registerToSysUser:{}", e.getMessage());
            }
        }
        return list;
    }
}
