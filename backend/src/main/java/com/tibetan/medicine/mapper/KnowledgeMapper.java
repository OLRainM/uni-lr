package com.tibetan.medicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tibetan.medicine.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;

/**
 * 藏药知识Mapper
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}
