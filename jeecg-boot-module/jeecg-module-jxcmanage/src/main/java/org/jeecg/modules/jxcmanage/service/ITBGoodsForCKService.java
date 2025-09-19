package org.jeecg.modules.jxcmanage.service;

import org.jeecg.modules.jxcmanage.entity.TBGoodsForCK;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 货品记录
 * @Author: jeecg-boot
 * @Date:   2025-09-13
 * @Version: V1.0
 */
public interface ITBGoodsForCKService extends IService<TBGoodsForCK> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<TBGoodsForCK>
	 */
	public List<TBGoodsForCK> selectByMainId(String mainId);
}
