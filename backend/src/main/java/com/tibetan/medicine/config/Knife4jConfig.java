package com.tibetan.medicine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类 - API文档
 * 
 * 访问地址：http://localhost:8080/api/doc.html
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("藏药小程序API文档")
                        .version("1.0.0")
                        .description("藏药电商小程序后端接口文档，提供完整的RESTful API")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@tibetan-medicine.com")
                                .url("https://github.com/tibetan-medicine"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }

    /**
     * 用户模块API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("1-用户模块")
                .pathsToMatch("/user/**")
                .build();
    }

    /**
     * 商品模块API分组
     */
    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("2-商品模块")
                .pathsToMatch("/product/**")
                .build();
    }

    /**
     * 购物车模块API分组
     */
    @Bean
    public GroupedOpenApi cartApi() {
        return GroupedOpenApi.builder()
                .group("3-购物车模块")
                .pathsToMatch("/cart/**")
                .build();
    }

    /**
     * 订单模块API分组
     */
    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("4-订单模块")
                .pathsToMatch("/order/**")
                .build();
    }

    /**
     * 地址模块API分组
     */
    @Bean
    public GroupedOpenApi addressApi() {
        return GroupedOpenApi.builder()
                .group("5-地址模块")
                .pathsToMatch("/address/**")
                .build();
    }

    /**
     * 优惠券模块API分组
     */
    @Bean
    public GroupedOpenApi couponApi() {
        return GroupedOpenApi.builder()
                .group("6-优惠券模块")
                .pathsToMatch("/coupon/**")
                .build();
    }

    /**
     * 其他模块API分组
     */
    @Bean
    public GroupedOpenApi otherApi() {
        return GroupedOpenApi.builder()
                .group("9-其他模块")
                .pathsToMatch("/banner/**", "/knowledge/**", "/payment/**", "/upload/**")
                .build();
    }
}
