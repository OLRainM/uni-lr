package com.tibetan.medicine.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类（测试版本使用内存缓存，无需 Redis）
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager() {
        // 使用本地内存缓存，不依赖 Redis，适用于测试/展示环境
        return new ConcurrentMapCacheManager();
    }
}
