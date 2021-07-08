package com.lgy.fileupload;

import com.lgy.fileupload.clientServer.util.LinkUtil;
import com.lgy.fileupload.util.PropertiesUntil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

@SpringBootApplication
public class FileUploadApplication {

    public static void main(String[] args) throws IOException {
        PropertiesUntil.rootPath = System.getProperty("user.dir");
        PropertiesUntil.autoCreateDir();
        SpringApplication.run(FileUploadApplication.class, args);
        new LinkUtil().con();

    }

}
