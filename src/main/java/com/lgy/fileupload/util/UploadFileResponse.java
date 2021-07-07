package com.lgy.fileupload.util;


import lombok.Data;

import java.io.Serializable;

/**
 * @Desc:返回结果封装类
 * @Author:lgy
 * @Time:2021/6/1715:15
 */
@Data
public class UploadFileResponse  {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

}
