package org.jeecg.modules.jxcmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.jxcmanage.entity.TBStyleForCK;
import org.jeecg.modules.jxcmanage.entity.TBGoodsForCK;
import org.jeecg.modules.jxcmanage.mapper.TBGoodsForCKMapper;
import org.jeecg.modules.jxcmanage.mapper.TBStyleForCKMapper;
import org.jeecg.modules.jxcmanage.service.ITBStyleForCKService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;

/**
 * @Description: 仓库
 * @Author: jeecg-boot
 * @Date: 2025-09-13
 * @Version: V1.0
 */
@Service
public class TBStyleForCKServiceImpl extends ServiceImpl<TBStyleForCKMapper, TBStyleForCK> implements ITBStyleForCKService {

    @Autowired
    private TBStyleForCKMapper tBStyleForCKMapper;
    @Autowired
    private TBGoodsForCKMapper tBGoodsForCKMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(TBStyleForCK tBStyleForCK, List<TBGoodsForCK> tBGoodsForCKList) {
        tBStyleForCKMapper.insert(tBStyleForCK);
        if (tBGoodsForCKList != null && tBGoodsForCKList.size() > 0) {
            for (TBGoodsForCK entity : tBGoodsForCKList) {
                //外键设置
                entity.setStyleId(tBStyleForCK.getId());
                tBGoodsForCKMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMain(TBStyleForCK tBStyleForCK, List<TBGoodsForCK> tBGoodsForCKList) {
        tBStyleForCKMapper.updateById(tBStyleForCK);
        QueryWrapper<TBGoodsForCK> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inbound_status", "1");  // 在库
        queryWrapper.eq("style_id", tBStyleForCK.getId());
        List<TBGoodsForCK> tbGoodsForCKListDB = tBGoodsForCKMapper.selectList(queryWrapper);
        List<String> idsInDB = tbGoodsForCKListDB.stream().map(TBGoodsForCK::getId).toList();
        Map<String, TBGoodsForCK> tbGoodsForCKMap = new HashMap<>();
        for (TBGoodsForCK goods : tBGoodsForCKList) {
            tbGoodsForCKMap.put(goods.getId(), goods);
        }
        idsInDB.forEach(id -> {
            if (tbGoodsForCKMap.containsKey(id)) {
                tBGoodsForCKMapper.updateById(tbGoodsForCKMap.get(id));
            }else{
                tBGoodsForCKMapper.deleteById(id);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        tBGoodsForCKMapper.deleteByMainId(id);
        tBStyleForCKMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            tBGoodsForCKMapper.deleteByMainId(id.toString());
            tBStyleForCKMapper.deleteById(id);
        }
    }

}
