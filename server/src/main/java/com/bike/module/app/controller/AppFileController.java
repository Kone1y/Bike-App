package com.bike.module.app.controller;

import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/app/file")
public class AppFileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID() + extension;
        File dest = new File(uploadPath, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
        Map<String, String> result = new HashMap<>();
        result.put("url", "/uploads/" + filename);
        return Result.success(result);
    }
}
