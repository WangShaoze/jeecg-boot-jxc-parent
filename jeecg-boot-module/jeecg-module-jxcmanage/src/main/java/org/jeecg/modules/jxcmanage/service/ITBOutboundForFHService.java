package org.jeecg.modules.jxcmanage.service;

import org.jeecg.modules.jxcmanage.entity.TBGoodsForFH;
import org.jeecg.modules.jxcmanage.entity.TBOutboundForFH;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2025-09-14
 * @Version: V1.0
 */
public interface ITBOutboundForFHService extends IService<TBOutboundForFH> {

	/**
	 * 添加一对多
	 *
	 * @param tBOutboundForFH
	 * @param tBGoodsForFHList
	 */
	public void saveMain(TBOutboundForFH tBOutboundForFH,List<TBGoodsForFH> tBGoodsForFHList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param tBOutboundForFH
	 * @param tBGoodsForFHList
	 */
	public void updateMain(TBOutboundForFH tBOutboundForFH,List<TBGoodsForFH> tBGoodsForFHList);
	
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
