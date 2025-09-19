package org.jeecg.modules.jxcmanage.vo;

import java.util.List;
import org.jeecg.modules.jxcmanage.entity.TBGoodsForFH;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 分货
 * @Author: jeecg-boot
 * @Date:   2025-09-13
 * @Version: V1.0
 */
@Data
@Schema(description="分货")
public class TBStyleForFHPage {

	/**ID*/
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
	
	@ExcelCollection(name="货品记录")
	@Schema(description = "货品记录")
	private List<TBGoodsForFH> tBGoodsForFHList;
	
}
