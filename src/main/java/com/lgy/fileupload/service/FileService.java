package com.lgy.fileupload.service;

import com.lgy.fileupload.exection.FileException;
import com.lgy.fileupload.util.PropertiesUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileService {

    private final Path fileStorageLocation; // 文件在本地存储的地址

    @Autowired
    public FileService() {
        this.fileStorageLocation = Paths.get(PropertiesUntil.SERVER_STORY).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileException("无法创建文件的储存目录", ex);
        }
    }

    /**
     * 存储文件到系统
     *
     * @param file 文件
     * @return 文件名
     */
    public String storeFile(MultipartFile file) {
        // 获取文件名称
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            //
            if(fileName.contains("..")) {
                throw new FileException("文件名无效（存在..）" + fileName);
            }

            // 复制文件到下载路径

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            System.out.println(file.getInputStream());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileException("无法存储此文件 " + fileName + "请重试", ex);
        }
    }




}