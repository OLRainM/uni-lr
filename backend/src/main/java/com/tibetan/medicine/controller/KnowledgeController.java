package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Knowledge;
import com.tibetan.medicine.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 藏药知识控制器
 */
@SkipAuth
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
    
    @Autowired
    private KnowledgeService knowledgeService;
    
    /**
     * 知识列表
     */
    @GetMapping("/list")
    public Result<List<Knowledge>> getKnowledgeList() {
        List<Knowledge> list = knowledgeService.getKnowledgeList();
        return Result.success(list);
    }
    
    /**
     * 知识详情
     */
    @GetMapping("/detail/{id}")
    public Result<Knowledge> getKnowledgeDetail(@PathVariable Long id) {
        Knowledge knowledge = knowledgeService.getKnowledgeDetail(id);
        return Result.success(knowledge);
    }
}
