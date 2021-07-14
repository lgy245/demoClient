package com.lgy.fileupload.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lgy.fileupload.model.FileModel;
import com.lgy.fileupload.service.FileClient;
import com.lgy.fileupload.service.FileService;
import com.lgy.fileupload.util.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.util.ToStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
@Slf4j
@RestController
public class FileController {


    @Autowired
    private FileService fileService;
    @Autowired
    private FileClient fileClient;
    @PostMapping("/api/uploadFile")
    public ResultInfo uploadFile(@RequestParam("file") MultipartFile file)  {
        String fileName = fileService.storeFile(file);
        // 生成下载链接

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        ThreadClass threadClass = new ThreadClass();
        String fileId =  df.format(new Date());
        threadClass.StorageFile(fileName,fileId);
        // 线程2 进行文件分片传输
        threadClass.fileFragmentation(fileName,fileId);

        return  ResultInfo.ok();
    }


   /* @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files)  {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }*/
    //文件列表
    @GetMapping("/api/fileList")
    public ResultInfo getList(){
        //获取文件列表
        return  ResultInfo.ok(JSONArray.parseArray( new RememberFile() .getContent(PropertiesUntil.STORY_FILE_PATH),FileModel.class));
    }
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        // Load file as Resource
        Resource resource = fileClient.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        String filename = new String(resource.getFilename().getBytes("utf-8"),"ISO8859-1");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}