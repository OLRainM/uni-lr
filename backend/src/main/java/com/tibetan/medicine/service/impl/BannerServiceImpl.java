package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.Banner;
import com.tibetan.medicine.mapper.BannerMapper;
import com.tibetan.medicine.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
    
    @Override
    public List<Banner> getBannerList() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1);  // 只查询启用的轮播图
        wrapper.orderByAsc(Banner::getSort);  // 按排序字段升序
        return this.list(wrapper);
    }
}
