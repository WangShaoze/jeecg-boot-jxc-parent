package org.jeecg.modules.jxcmanage.service;

import org.jeecg.modules.jxcmanage.entity.TBGoodsForCK;
import org.jeecg.modules.jxcmanage.entity.TBStyleForCK;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 仓库
 * @Author: jeecg-boot
 * @Date:   2025-09-13
 * @Version: V1.0
 */
public interface ITBStyleForCKService extends IService<TBStyleForCK> {

	/**
	 * 添加一对多
	 *
	 * @param tBStyleForCK
	 * @param tBGoodsForCKList
	 */
	public void saveMain(TBStyleForCK tBStyleForCK,List<TBGoodsForCK> tBGoodsForCKList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param tBStyleForCK
	 * @param tBGoodsForCKList
	 */
	public void updateMain(TBStyleForCK tBStyleForCK,List<TBGoodsForCK> tBGoodsForCKList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
