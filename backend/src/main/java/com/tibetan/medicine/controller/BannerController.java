package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Banner;
import com.tibetan.medicine.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器
 */
@SkipAuth
@RestController
@RequestMapping("/banner")
public class BannerController {
    
    @Autowired
    private BannerService bannerService;
    
    /**
     * 获取轮播图列表
     */
    @GetMapping("/list")
    public Result<List<Banner>> getBannerList() {
        List<Banner> banners = bannerService.getBannerList();
        return Result.success(banners);
    }
}
