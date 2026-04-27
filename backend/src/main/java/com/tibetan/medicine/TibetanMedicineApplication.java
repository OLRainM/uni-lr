package com.tibetan.medicine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 藏药小程序后端启动类
 */
@SpringBootApplication
@MapperScan("com.tibetan.medicine.mapper")
public class TibetanMedicineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TibetanMedicineApplication.class, args);
        System.out.println("========================================");
        System.out.println("藏药小程序后端服务启动成功！");
        System.out.println("API地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}

