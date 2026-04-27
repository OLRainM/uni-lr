package com.tibetan.medicine.controller;

import com.tibetan.medicine.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    
    /**
     * 上传图片（Mock版本）
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        // Mock: 模拟文件上传成功
        // 实际应用中需要上传到OSS、本地存储等
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        
        // 模拟生成文件URL
        String mockUrl = "https://picsum.photos/800/600?random=" + System.currentTimeMillis();
        
        Map<String, String> data = new HashMap<>();
        data.put("url", mockUrl);
        data.put("filename", originalFilename);
        data.put("size", String.valueOf(file.getSize()));
        
        return Result.success(data);
    }
    
    /**
     * 批量上传图片（Mock版本）
     */
    @PostMapping("/images")
    public Result<Map<String, Object>> uploadImages(
            @RequestHeader("Authorization") String token,
            @RequestParam("files") MultipartFile[] files) {
        
        if (files == null || files.length == 0) {
            return Result.error("文件不能为空");
        }
        
        // Mock: 模拟批量上传
        java.util.List<Map<String, String>> urls = new java.util.ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String mockUrl = "https://picsum.photos/800/600?random=" + System.currentTimeMillis();
                Map<String, String> fileInfo = new HashMap<>();
                fileInfo.put("url", mockUrl);
                fileInfo.put("filename", file.getOriginalFilename());
                urls.add(fileInfo);
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("count", urls.size());
        data.put("urls", urls);
        
        return Result.success(data);
    }
}
