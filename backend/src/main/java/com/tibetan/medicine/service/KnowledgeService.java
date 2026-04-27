package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.entity.Knowledge;
import java.util.List;

/**
 * 藏药知识服务接口
 */
public interface KnowledgeService extends IService<Knowledge> {
    
    /**
     * 获取知识列表
     */
    List<Knowledge> getKnowledgeList();
    
    /**
     * 获取知识详情
     */
    Knowledge getKnowledgeDetail(Long id);
}
