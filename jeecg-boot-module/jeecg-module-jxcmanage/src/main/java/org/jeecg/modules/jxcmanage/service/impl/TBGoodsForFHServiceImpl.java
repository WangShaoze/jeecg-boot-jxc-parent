package org.jeecg.modules.jxcmanage.service.impl;

import org.jeecg.modules.jxcmanage.entity.TBGoodsForFH;
import org.jeecg.modules.jxcmanage.mapper.TBGoodsForFHMapper;
import org.jeecg.modules.jxcmanage.service.ITBGoodsForFHService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 货品记录
 * @Author: jeecg-boot
 * @Date:   2025-09-14
 * @Version: V1.0
 */
@Service
public class TBGoodsForFHServiceImpl extends ServiceImpl<TBGoodsForFHMapper, TBGoodsForFH> implements ITBGoodsForFHService {
	
	@Autowired
	private TBGoodsForFHMapper tBGoodsForFHMapper;
	
	@Override
	public List<TBGoodsForFH> selectByMainId(String mainId) {
		return tBGoodsForFHMapper.selectByMainId(mainId);
	}
}
