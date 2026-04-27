package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.Knowledge;
import com.tibetan.medicine.mapper.KnowledgeMapper;
import com.tibetan.medicine.service.KnowledgeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 藏药知识服务实现类
 */
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService {
    
    @Override
    public List<Knowledge> getKnowledgeList() {
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Knowledge::getStatus, 1);
        wrapper.orderByDesc(Knowledge::getCreateTime);
        return this.list(wrapper);
    }
    
    @Override
    public Knowledge getKnowledgeDetail(Long id) {
        Knowledge knowledge = this.getById(id);
        if (knowledge != null) {
            // 阅读量+1（防止历史数据views为null）
            Integer currentViews = knowledge.getViews() == null ? 0 : knowledge.getViews();
            knowledge.setViews(currentViews + 1);
            this.updateById(knowledge);
        }
        return knowledge;
    }
}
