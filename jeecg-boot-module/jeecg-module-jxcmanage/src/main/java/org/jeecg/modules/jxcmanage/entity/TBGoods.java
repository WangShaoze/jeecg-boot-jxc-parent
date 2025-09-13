package org.jeecg.modules.jxcmanage.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 货品表
 * @Author: jeecg-boot
 * @Date: 2025-09-12
 * @Version: V1.0
 */
@Data
@TableName("t_b_goods")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "货品表")
public class TBGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键，32位不重复字符串
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键，32位不重复字符串")
    private java.lang.String id;

    /**
     * 总序号
     */
    @TableField(exist = false)
    @Excel(name = "总序号", width = 15)
    @Schema(description = "总序号")
    private java.lang.Integer index;


    /**
     * 销售单号
     */
    @Excel(name = "销售单号", width = 15)
    @Schema(description = "销售单号")
    private java.lang.String salesOrderNo;
    /**
     * 销售日期
     */
    @Excel(name = "销售日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "销售日期")
    private java.util.Date salesDate;
    /**
     * 订单号
     */
    @Excel(name = "订单号", width = 15)
    @Schema(description = "订单号")
    private java.lang.String orderNo;
    /**
     * 款号
     */
    @Excel(name = "款号", width = 15)
    @Schema(description = "款号")
    private java.lang.String styleNo;
    /**
     * 客户款号
     */
    @Excel(name = "客户款号", width = 15)
    @Schema(description = "客户款号")
    private java.lang.String newStyleNo;
    /**
     * 客胚名称
     */
    @Excel(name = "客胚名称", width = 15)
    @Schema(description = "客胚名称")
    private java.lang.String itemName;

    /**
     * 款式类别
     */
    @Excel(name = "款式类别", width = 15)
    @Schema(description = "款式类别")
    private java.lang.String styleCategory;
    /**
     * 货号
     */
    @Excel(name = "货号", width = 15)
    @Schema(description = "货号")
    private java.lang.String productNo;

    /**
     * 银重
     */
    @Excel(name = "银重", width = 15)
    @Schema(description = "银重")
    private java.math.BigDecimal silverWeight;
    /**
     * 金重
     */
    @Excel(name = "金重", width = 15)
    @Schema(description = "金重")
    private java.math.BigDecimal goldWeight;
    /**
     * 总重
     */
    @Excel(name = "总重", width = 15)
    @Schema(description = "总重")
    private java.math.BigDecimal totalWeight;
    /**
     * 证书编号
     */
    @Excel(name = "证书编号", width = 15)
    @Schema(description = "证书编号")
    private java.lang.String certificateNo;
    /**
     * 证书机构
     */
    @Excel(name = "证书机构", width = 15)
    @Schema(description = "证书机构")
    private java.lang.String certificateOrg;
    /**
     * 字印
     */
    @Excel(name = "字印", width = 15)
    @Schema(description = "字印")
    private java.lang.String seal;
    /**
     * 查询地址
     */
    @Excel(name = "查询地址", width = 15)
    @Schema(description = "查询地址")
    private java.lang.String queryUrl;
    /**
     * 序号
     */
    @Excel(name = "序号", width = 15)
    @Schema(description = "序号")
    private java.lang.Integer sequenceNo;
    /**
     * 原价
     */
    @Excel(name = "原价", width = 15)
    @Schema(description = "原价")
    private java.math.BigDecimal originalPrice;
    /**
     * 参考售价
     */
    @Excel(name = "参考售价", width = 15)
    @Schema(description = "参考售价")
    private java.math.BigDecimal referenceSellingPrice;
    /**
     * 状态（合/残）
     */
    @Excel(name = "状态（合/残）", width = 15, dicCode = "jxc_pandian_status")
    @Dict(dicCode = "jxc_pandian_status")
    @Schema(description = "状态（合/残）")
    private java.lang.String status;
    /**
     * 库存状态
     */
    @Excel(name = "库存状态", width = 15, dicCode = "jxc_inbound_status")
    @Dict(dicCode = "jxc_inbound_status")
    @Schema(description = "库存状态")
    private java.lang.String inboundStatus;
    /**
     * 销售状态
     */
    @Excel(name = "销售状态", width = 15, dicCode = "jxc_sales_status")
    @Dict(dicCode = "jxc_sales_status")
    @Schema(description = "销售状态")
    private java.lang.String salesStatus;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private java.lang.String remarks;
    /**
     * 库码ID
     */
    //@Excel(name = "库码ID", width = 15)
    @Schema(description = "库码ID")
    private java.lang.String kmId;
    /**
     * 库位号
     */
    @Excel(name = "库位号", width = 15)
    @Schema(description = "库位号")
    private java.lang.String kmValue;
    /**
     * 款式ID
     */
    //@Excel(name = "款式ID", width = 15)
    @Schema(description = "款式ID")
    private java.lang.String styleId;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private java.lang.String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
}
