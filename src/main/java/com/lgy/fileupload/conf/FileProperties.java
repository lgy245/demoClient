package com.lgy.fileupload.conf;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    private String uploadDir;
    private String splitFile;

    public String getUploadDir() {
        return uploadDir;
    }
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
    public String getSplitFile() {
        return splitFile;
    }
    public void setSplitFile(String splitFile) {
        this.splitFile = splitFile;
    }
}