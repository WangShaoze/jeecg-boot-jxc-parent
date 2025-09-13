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
 * @Description: 业务日志管理
 * @Author: jeecg-boot
 * @Date:   2025-09-11
 * @Version: V1.0
 */
@Data
@TableName("t_b_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="业务日志管理")
public class TBLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private java.lang.String id;
	/**日志标识*/
	@Excel(name = "日志标识", width = 15)
    @Schema(description = "日志标识")
    private java.lang.String remarks;
	/**日志内容*/
	@Excel(name = "日志内容", width = 15)
    @Schema(description = "日志内容")
    private java.lang.String logContent;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @Schema(description = "文件路径")
    private java.lang.String fileUrl;
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
