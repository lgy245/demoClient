package com.lgy.fileupload;

import com.lgy.fileupload.clientServer.util.LinkUtil;
import com.lgy.fileupload.service.ServerPortService;
import com.lgy.fileupload.util.PropertiesUntil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.InetAddress;

@SpringBootApplication
public class FileUploadApplication {

    public static void main(String[] args) throws IOException {
        PropertiesUntil.rootPath = System.getProperty("user.dir");
        PropertiesUntil.autoCreateDir();
        SpringApplication.run(FileUploadApplication.class, args);

        String host = null;
        Integer port = null;
        if(args.length>0){
            host = args[0];
        }
        if(args.length>1){
            port = Integer.valueOf(args[1]);
        }
        String ip = InetAddress.getLocalHost().getHostAddress();
        if(args.length>2){
            ip = args[2];
        }
        ServerPortService.ip = ip;

        new LinkUtil().con(host,port);

    }

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        ServerPortService.port = event.getWebServer().getPort();
    }
}
