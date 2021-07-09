package com.lgy.fileupload.service;

import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @Desc:
 * @Author:lgy
 * @Time:2021/7/912:28
 */
@Service
public class ServerPortService {
    private int port;
    public static String ip;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
    }
}
