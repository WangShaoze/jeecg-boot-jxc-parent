package org.jeecg.modules.jxcmanage.service;

import org.jeecg.modules.jxcmanage.entity.TBGoodsForFH;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 货品记录
 * @Author: jeecg-boot
 * @Date:   2025-09-14
 * @Version: V1.0
 */
public interface ITBGoodsForFHService extends IService<TBGoodsForFH> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<TBGoodsForFH>
	 */
	public List<TBGoodsForFH> selectByMainId(String mainId);
}
