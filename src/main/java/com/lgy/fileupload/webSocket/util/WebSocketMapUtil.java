package com.lgy.fileupload.webSocket.util;

import com.lgy.fileupload.webSocket.demo.WebSocketUploadServer;

import java.util.Collection;
import java.util.HashMap;

public class WebSocketMapUtil {
    public static HashMap<String, WebSocketUploadServer> webSocketMap = new HashMap<>();
    public static void put(String key, WebSocketUploadServer myWebSocket){
        webSocketMap.put(key, myWebSocket);
    }

    public static WebSocketUploadServer get(String key){
        return webSocketMap.get(key);
    }

    public static void remove(String key){
        webSocketMap.remove(key);
    }

    public static Collection<WebSocketUploadServer> getValues(){
        return webSocketMap.values();
    }
}