package org.jeecg.modules.jxcmanage.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 仓库管理
 * @Author: jeecg-boot
 * @Date:   2025-09-15
 * @Version: V1.0
 */
@Data
@TableName("t_b_warehouse")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="仓库管理")
public class TBWarehouse implements Serializable {
    private static final long serialVersionUID = 1L;

	/**库码ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "库码ID")
    private java.lang.String id;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**开户时间*/
	@Excel(name = "开户时间", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "开户时间")
    private java.util.Date openAccountDatetime;
	/**激活码*/
	@Excel(name = "激活码", width = 15)
    @Schema(description = "激活码")
    private java.lang.String activateCode;
	/**仓库名*/
	@Excel(name = "仓库名", width = 15)
    @Schema(description = "仓库名")
    private java.lang.String warehouseName;
	/**注册手机号*/
	@Excel(name = "注册手机号", width = 15)
    @Schema(description = "注册手机号")
    private java.lang.String phone;
	/**店铺名*/
	@Excel(name = "店铺名", width = 15)
    @Schema(description = "店铺名")
    private java.lang.String merchantName;
	/**报警原因*/
	@Excel(name = "报警原因", width = 15)
    @Schema(description = "报警原因")
    private java.lang.String warningCause;
	/**角色*/
//	@Excel(name = "角色", width = 15)
    @Schema(description = "角色")
    private java.lang.String sysRole;
	/**角色ID*/
//	@Excel(name = "角色ID", width = 15)
    @Schema(description = "角色ID")
    private java.lang.String sysRoleId;
    /**用户ID*/
    @Schema(description = "用户ID")
    private java.lang.String sysUid;
}
