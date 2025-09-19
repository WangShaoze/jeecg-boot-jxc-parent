package org.jeecg.modules.jxcmanage.mapper;

import java.util.List;
import org.jeecg.modules.jxcmanage.entity.TBGoodsForCK;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 货品记录
 * @Author: jeecg-boot
 * @Date:   2025-09-13
 * @Version: V1.0
 */
public interface TBGoodsForCKMapper extends BaseMapper<TBGoodsForCK> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<TBGoodsForCK>
   */
	public List<TBGoodsForCK> selectByMainId(@Param("mainId") String mainId);
}
