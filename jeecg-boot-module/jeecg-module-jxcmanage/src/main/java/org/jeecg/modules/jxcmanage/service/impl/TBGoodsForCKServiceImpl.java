package org.jeecg.modules.jxcmanage.service.impl;

import org.jeecg.modules.jxcmanage.entity.TBGoodsForCK;
import org.jeecg.modules.jxcmanage.mapper.TBGoodsForCKMapper;
import org.jeecg.modules.jxcmanage.service.ITBGoodsForCKService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 货品记录
 * @Author: jeecg-boot
 * @Date:   2025-09-13
 * @Version: V1.0
 */
@Service
public class TBGoodsForCKServiceImpl extends ServiceImpl<TBGoodsForCKMapper, TBGoodsForCK> implements ITBGoodsForCKService {
	
	@Autowired
	private TBGoodsForCKMapper tBGoodsForCKMapper;
	
	@Override
	public List<TBGoodsForCK> selectByMainId(String mainId) {
		return tBGoodsForCKMapper.selectByMainId(mainId);
	}
}
