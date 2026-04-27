package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.entity.Banner;
import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<Banner> {
    
    /**
     * 获取轮播图列表
     */
    List<Banner> getBannerList();
}
