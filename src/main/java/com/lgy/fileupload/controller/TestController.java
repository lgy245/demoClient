package com.lgy.fileupload.controller;

import com.lgy.fileupload.util.WebsocketUtil;
import com.lgy.fileupload.webSocket.demo.WebSocketUploadServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Desc:页面
 * @Author:lgy
 * @Time:2021/6/2911:30
 */
@RestController
@RequestMapping("test")
public class TestController {
    @RequestMapping("/test")
    public void toIndex() throws IOException {
        for (int i = 10;i>0;i--){

            WebSocketUploadServer.sendInfo(""+i,"1");
        }
    }

}
