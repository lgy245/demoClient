package com.lgy.fileupload.model;

import lombok.Data;

/**
 * @Desc:
 * @Author:lgy
 * @Time:2021/6/3011:22
 */
@Data
public class FileModel {
    private String fileName;
    private Integer isAcept;
    private String documentProgress;
    private String id;
    private String path;

}
