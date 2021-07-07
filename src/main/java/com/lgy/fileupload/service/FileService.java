package com.lgy.fileupload.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lgy.fileupload.conf.FileProperties;
import com.lgy.fileupload.exection.FileException;
import com.lgy.fileupload.model.FileModel;
import com.lgy.fileupload.util.CutFileUtil;
import com.lgy.fileupload.util.PropertiesUntil;
import com.lgy.fileupload.util.RememberFile;
import com.lgy.fileupload.util.ThreadClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class FileService {

    private final Path fileStorageLocation; // 文件在本地存储的地址

    @Autowired
    public FileService(FileProperties fileProperties) {
        this.fileStorageLocation = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();
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