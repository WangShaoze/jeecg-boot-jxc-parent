package org.jeecg.modules.jxcmanage.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2025-09-14
 * @Version: V1.0
 */
@Schema(description="出库表")
@Data
@TableName("t_b_outbound")
public class TBOutboundForFH implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private java.lang.String id;
	/**款号*/
	@Excel(name = "款号", width = 15)
    @Schema(description = "款号")
    private java.lang.String styleNo;
	/**款式名称*/
	@Excel(name = "款式名称", width = 15)
    @Schema(description = "款式名称")
    private java.lang.String itemName;
	/**客户*/
	@Excel(name = "客户", width = 15)
    @Schema(description = "客户")
    private java.lang.String customAccount;
	/**金工费*/
	@Excel(name = "金工费", width = 15)
    @Schema(description = "金工费")
    private java.math.BigDecimal goldProcessFee;
	/**银工费*/
	@Excel(name = "银工费", width = 15)
    @Schema(description = "银工费")
    private java.math.BigDecimal sliverProcessFee;
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
}
