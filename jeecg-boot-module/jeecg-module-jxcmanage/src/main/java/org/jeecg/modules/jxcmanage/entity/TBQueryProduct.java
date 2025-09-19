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
 * @Description: 商品查询
 * @Author: jeecg-boot
 * @Date:   2025-09-19
 * @Version: V1.0
 */
@Data
@TableName("t_b_query_product")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="商品查询")
public class TBQueryProduct implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
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
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**货号*/
	@Excel(name = "货号", width = 15)
    @Schema(description = "货号")
    private java.lang.String itemNumber;
	/**证书编号*/
	@Excel(name = "证书编号", width = 15)
    @Schema(description = "证书编号")
    private java.lang.String certificateNumber;
	/**商品名称*/
	@Excel(name = "商品名称", width = 15)
    @Schema(description = "商品名称")
    private java.lang.String productName;
	/**形状*/
	@Excel(name = "形状", width = 15)
    @Schema(description = "形状")
    private java.lang.String shape;
	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private java.lang.String color;
	/**基材重*/
	@Excel(name = "基材重", width = 15)
    @Schema(description = "基材重")
    private java.lang.Double baseMaterialWeight;
	/**电金后重*/
	@Excel(name = "电金后重", width = 15)
    @Schema(description = "电金后重")
    private java.lang.Double weightAfterGoldPlating;
	/**总质量*/
	@Excel(name = "总质量", width = 15)
    @Schema(description = "总质量")
    private java.lang.Double totalWeight;
	/**金重*/
	@Excel(name = "金重", width = 15)
    @Schema(description = "金重")
    private java.lang.Double goldWeight;
}
