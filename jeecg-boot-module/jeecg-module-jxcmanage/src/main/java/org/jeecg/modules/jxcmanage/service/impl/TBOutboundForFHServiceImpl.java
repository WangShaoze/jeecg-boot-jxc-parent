package org.jeecg.modules.jxcmanage.service.impl;

import org.jeecg.modules.jxcmanage.entity.TBOutboundForFH;
import org.jeecg.modules.jxcmanage.entity.TBGoodsForFH;
import org.jeecg.modules.jxcmanage.mapper.TBGoodsForFHMapper;
import org.jeecg.modules.jxcmanage.mapper.TBOutboundForFHMapper;
import org.jeecg.modules.jxcmanage.service.ITBOutboundForFHService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2025-09-14
 * @Version: V1.0
 */
@Service
public class TBOutboundForFHServiceImpl extends ServiceImpl<TBOutboundForFHMapper, TBOutboundForFH> implements ITBOutboundForFHService {

	@Autowired
	private TBOutboundForFHMapper tBOutboundForFHMapper;
	@Autowired
	private TBGoodsForFHMapper tBGoodsForFHMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(TBOutboundForFH tBOutboundForFH, List<TBGoodsForFH> tBGoodsForFHList) {
		tBOutboundForFHMapper.insert(tBOutboundForFH);
		if(tBGoodsForFHList!=null && tBGoodsForFHList.size()>0) {
			for(TBGoodsForFH entity:tBGoodsForFHList) {
				//外键设置
				entity.setOutboundId(tBOutboundForFH.getId());
				tBGoodsForFHMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(TBOutboundForFH tBOutboundForFH,List<TBGoodsForFH> tBGoodsForFHList) {
		tBOutboundForFHMapper.updateById(tBOutboundForFH);
		
		//1.先删除子表数据
		tBGoodsForFHMapper.deleteByMainId(tBOutboundForFH.getId());
		
		//2.子表数据重新插入
		if(tBGoodsForFHList!=null && tBGoodsForFHList.size()>0) {
			for(TBGoodsForFH entity:tBGoodsForFHList) {
				//外键设置
				entity.setOutboundId(tBOutboundForFH.getId());
				tBGoodsForFHMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		tBGoodsForFHMapper.deleteByMainId(id);
		tBOutboundForFHMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			tBGoodsForFHMapper.deleteByMainId(id.toString());
			tBOutboundForFHMapper.deleteById(id);
		}
	}
	
}
