package com.lgy.fileupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Desc:页面
 * @Author:lgy
 * @Time:2021/6/2911:30
 */
@Controller
@RequestMapping("to")
public class ViewController {
    @RequestMapping("/index")
    public String toIndex(){
        return "index";
    }
    @RequestMapping("/webSocketDemo")
    public String toWebSocketDemo(){
        return "webSocketDemo";
    }
    @RequestMapping("/test")
    public String test(){
        return "test";
    }

}
