package com.lgy.fileupload;

import com.lgy.fileupload.clientServer.client.NettyClient;
import com.lgy.fileupload.clientServer.server.NettyServer;
import com.lgy.fileupload.clientServer.util.LinkUtil;
import com.lgy.fileupload.conf.FileProperties;
import io.netty.channel.ChannelFuture;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        FileProperties.class
})@SpringBootApplication
public class FileUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
        new LinkUtil().con();
        //new NettyServer().bing(8083);
    }

}
