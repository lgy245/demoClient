//package com.lgy.fileupload;
//
//
//import com.lgy.fileupload.clientServer.client.NettyClient;
//import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
//import com.lgy.fileupload.clientServer.util.MsgUtil;
//import com.lgy.fileupload.util.CutFileUtil;
//import com.lgy.fileupload.util.PropertiesUntil;
//import io.netty.channel.ChannelFuture;
//
//import java.io.File;
//import java.util.List;
//
///**
//* socket客户端启动入口
//* @author:zhengs
//* @Time: 21-6-30 下午3:03
//* @Copyright: ©  杭州凯立通信有限公司 版权所有
//* @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
//*/
//public class NettyClientTest {
//
//    private static final String SPLIT_PATH = PropertiesUntil.SPLIT_PATH;
//
//    public static void main(String[] args) {
//        // 启动客户端
//        ChannelFuture channelFuture = new NettyClient().connect("127.0.0.1", 7397);
//
//        // 文件信息
//        String filePath = "E:\\data\\client\\data\\优化.txt";
//        File file = new File(filePath);
//        List<String> fileNames = new CutFileUtil().splitFile(filePath);
//        File splitFile = new File(SPLIT_PATH+fileNames.stream().findFirst().orElse(null));
//
//        char[] transfers = new char[40];
//        for(int i = 0;i<transfers.length;i++){
//            transfers[i] = '0';
//        }
//        FileTransferProtocol fileTransferProtocol = MsgUtil.createClientProtocol(SPLIT_PATH, splitFile.getName().split("-")[0], file.getName(), file.length(), 40,transfers);
//        // 请求传输文件
//        channelFuture.channel().writeAndFlush(fileTransferProtocol);
//    }
//
//}
