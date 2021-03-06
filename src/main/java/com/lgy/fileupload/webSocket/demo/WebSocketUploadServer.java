package com.lgy.fileupload.webSocket.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lgy.fileupload.clientServer.client.MyClientHandler;
import com.lgy.fileupload.clientServer.domain.FileProtocol;
import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
import com.lgy.fileupload.clientServer.domain.TransferType;
import com.lgy.fileupload.clientServer.util.MsgUtil;
import com.lgy.fileupload.util.MapUntil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ClassName: WebSocketUploadServer <br/>
 * Description: <br/>
 * date: 2020/2/4 14:51<br/>
 *
 * @author ccsert<br />
 * @since JDK 1.8
 */
@ServerEndpoint("/upload/{sid}")
@Component
public class WebSocketUploadServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketUploadServer.class);

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    public  static CopyOnWriteArraySet<WebSocketUploadServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    public Session session;






    /**
     * 保证文件对象和文件路径的唯一性
     */
    private HashMap docUrl;

    /**
     * 结束标识判断
     */
    private String endupload = "over";

    /**
     * 连接建立成功时调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        //加入set中
        webSocketSet.add(this);
        //在线人数加1
        addOnlineCount();
        LOG.info(sid + "连接成功" + "----当前在线人数为：" + onlineCount);
    }

    /**
     * 连接关闭时调用的方法
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        //在线人数减1
        subOnlineCount();
        //从set中删除
        webSocketSet.remove(this);
        LOG.info(sid + "已关闭连接" + "----剩余在线人数为：" + onlineCount);
    }

    /**
     * 接收客户端发送的消息时调用的方法
     *
     * @param message 接收的字符串消息。该消息应当为json字符串
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) throws IOException, InterruptedException {
//        //前端传过来的消息都是一个json
        JSONObject jsonObject = JSON.parseObject(message);
//        //消息类型

        String type = jsonObject.getString("type");
        String data = jsonObject.getString("data");
        FileProtocol myclass = JSONObject.parseObject(data , FileProtocol.class);
        MapUntil.isHeart = false;
        if("1".equals(type)){
            FileTransferProtocol sendFileTransferProtocol = MsgUtil.createServerProtocol(MsgUtil.FileExitByte(myclass), TransferType.AGREE,TransferType.CLIENT_SEND);
            MyClientHandler.x.writeAndFlush(sendFileTransferProtocol);
        }else if ("2".equals(type)){
            FileTransferProtocol sendFileTransferProtocol = MsgUtil.createServerProtocol(MsgUtil.FileExitByte(myclass), TransferType.REFUSE,TransferType.CLIENT_SEND);

            MyClientHandler.x.writeAndFlush(sendFileTransferProtocol);
        }else{
            this.sendMessage("error");
        }



    }

    /**
     * 该方法用于接收字节流数组
     *
     * @param message 文件字节流数组
     * @param session 会话
     */
    @OnMessage
    public void onMessage(byte[] message, Session session) {
        //群发消息
        try {

            this.sendMessage("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器主动提推送消息
     *
     * @param message 消息内容
     * @throws IOException io异常抛出
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发消息功能
     *
     * @param message 消息内容
     * @param sid     房间号
     */
    public static void sendInfo(String message, @PathParam("sid") String sid) {
//        LOG.info("推送消息到窗口" + sid + "，推送内容:" + message);
        for (WebSocketUploadServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                item.sendMessage(message);
            } catch (IOException e) {
                LOG.error("消息发送失败" + e.getMessage(), e);
                return;
            }
        }
    }

    /**
     * 原子性的++操作
     */
    public static synchronized void addOnlineCount() {
        WebSocketUploadServer.onlineCount++;
    }

    /**
     * 原子性的--操作
     */
    public static synchronized void subOnlineCount() {
        WebSocketUploadServer.onlineCount--;
    }

}
