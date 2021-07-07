package com.lgy.fileupload.service;

import com.lgy.fileupload.conf.FileProperties;
import com.lgy.fileupload.exection.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileClient {

    private final Path fileStorageLocation; // 文件在本地存储的地址

    @Autowired
    public FileClient(FileProperties fileProperties) {
        this.fileStorageLocation = Paths.get(fileProperties.getSplitFile()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileException("无法创建文件的储存目录", ex);
        }
    }


    /**
     * 加载文件
     * @param fileName 文件名
     * @return 文件
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileException("未找到文件 " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileException("未找到文件 " + fileName, ex);
        }
    }


}