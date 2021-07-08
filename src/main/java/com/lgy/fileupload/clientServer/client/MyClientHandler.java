package com.lgy.fileupload.clientServer.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lgy.fileupload.clientServer.domain.FileProtocol;
import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
import com.lgy.fileupload.clientServer.domain.TransferType;
import com.lgy.fileupload.clientServer.util.FileUtil;
import com.lgy.fileupload.clientServer.util.MsgUtil;
import com.lgy.fileupload.clientServer.util.SerializationUtil;
import com.lgy.fileupload.model.FileModel;
import com.lgy.fileupload.util.MapUntil;
import com.lgy.fileupload.util.PropertiesUntil;
import com.lgy.fileupload.util.RememberFile;
import com.lgy.fileupload.webSocket.demo.WebSocketUploadServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
* socket客户端处理器
* @author:zhengs
* @Time: 21-6-30 下午7:24
* @Copyright: ©  杭州凯立通信有限公司 版权所有
* @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
*/
@Slf4j
public class MyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    private double persont;
    public static ChannelHandlerContext x;
    public static Object  m ;
    public static String path ="" ;
    public static String filename ="" ;
    public static FileTransferProtocol fileProtocolWed ;
    private ByteBuf inCache = Unpooled.buffer();
    private boolean ok = false;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
        if(ok){
            byte[] data = new byte[inCache.readableBytes()];
            inCache.readBytes(data);

            Object obj = SerializationUtil.deserialize(data, FileTransferProtocol.class);
            if(obj != null){
                this.handleMsg(ctx, obj);
                x=ctx;

            }
        }else{
            ctx.read();
        }
    }
    public void handleMsg(ChannelHandlerContext ctx, Object msg) throws Exception {

        //数据格式验证
        if (!(msg instanceof FileTransferProtocol)){
            return;
        }
        FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
        if(fileTransferProtocol.getIsSend() == null || fileTransferProtocol.getTransferType() == null ){
            return;
        }
        FileProtocol fileProtocol = (FileProtocol) fileTransferProtocol.getTransferObj();
        JSONObject json = new JSONObject();
        //if(fileTransferProtocol.getIsSend()==8) {
        if(fileTransferProtocol.getIsSend()==TransferType.CLIENT_SEND) {
            // 接受 文件请求
            switch (fileTransferProtocol.getTransferType()) {
                // [客户端]请求传输文件
                case TransferType.REQUEST:
                    // 待实现
                    // 将请求转发给web端
                    // 将文件名称转发到web端
                    if(MapUntil.getData(fileProtocol.getPreFileName(),fileProtocol.getPreFileName())==false){
                        json.put("file", MsgUtil.FileExitByte(fileProtocol));
                        json.put("fileName",fileProtocol.getFileName());
                        json.put("isAcept", "0");
                        json.put("isSend", "0");
                        json.put("documentProgress", "0");
                        WebSocketUploadServer.sendInfo(json.toJSONString(), "2");
                    }
                    break;
                // [客户端]传输文件过程中
                case TransferType.TRANSFER:
                    fileProtocol = FileUtil.writeFile(fileProtocol);
                    //待实现
                    // 将接收进度转发给web端
              /*  // 测试代码,假设第10个文件分片丢失
                if(fileProtocol.getFileIndex()==39){
                    fileProtocol.getStatusArray()[9] = '0';
                    fileProtocol.getStatusArray()[14] = '0';
                    fileProtocol.getStatusArray()[19] = '0';
                }*/
                    if(fileProtocol.isFINISH()){
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(MsgUtil.FileExitByte(fileProtocol), TransferType.FINISH,TransferType.CLIENT_SEND));
                        if(MapUntil.getData(fileProtocol.getFileIndexName(),fileProtocol.getFileIndexName())==false) {
                            path = new RememberFile().dowondPath(MsgUtil.FileExitByte(fileProtocol).getFileIndexName(), fileProtocol.getTotalFileIndex());
                            Double  persont2 = Double.valueOf(fileProtocol.getNextFileIndex()*1.0/fileProtocol.getTotalFileIndex()*1.0)*100;
                            json.put("file", MsgUtil.FileExitByte(fileProtocol));
                            json.put("fileName",fileProtocol.getFileName());
                            json.put("isAcept","0");
                            json.put("isSend","0");
                            json.put("documentProgress",String.valueOf(persont2));
                            json.put("path",path);
                            WebSocketUploadServer.sendInfo(json.toJSONString(),"2");
                        }

                    }else{
//                        if(MapUntil.getData(fileProtocol.getFileIndexName(),fileProtocol.getFileIndexName())==false) {
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(MsgUtil.FileExitByte(fileProtocol), TransferType.TRANSFER,TransferType.CLIENT_SEND));
                        Double persont3 = Double.valueOf(fileProtocol.getNextFileIndex() * 1.0 / fileProtocol.getTotalFileIndex() * 1.0) * 100;
                        json.put("file", MsgUtil.FileExitByte(fileProtocol));
                        json.put("fileName",fileProtocol.getFileName());
                        json.put("isAcept", "0");
                        json.put("isSend", "0");
                        json.put("documentProgress", String.valueOf(persont3));
                        WebSocketUploadServer.sendInfo(json.toJSONString(), "2");
//                        }
                    }
                    // 转发进度到web端
                    break;

                // [客户端]传输文件完成
                case TransferType.FINISH:
                    // 判断是否存在未丢失分片
                    if(fileProtocol.isFINISH()){
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(MsgUtil.FileExitByte(fileProtocol), TransferType.FINISH,TransferType.CLIENT_SEND));
                        System.err.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 传输完成。");
                    }else{
                        // 设置第一个未传分片文件的位置
                        fileProtocol.setFileIndex(MsgUtil.FileExitByte(fileProtocol).getFristFileIndex());
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(MsgUtil.FileExitByte(fileProtocol), TransferType.PACKET_LOSS,TransferType.CLIENT_SEND));
                    }
                case TransferType.HEART:
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(null, TransferType.HEART,TransferType.CLIENT_SEND));
                    break;
                default:
                    break;
            }
        }else if(fileTransferProtocol.getIsSend() == TransferType.CLIENT_DOWN){
            // 0:请求传输文件, 1:同意传输文件, 2:文件传输'数据'
            switch (fileTransferProtocol.getTransferType()) {
                // 1.服务器同意传输文件
                case TransferType.REFUSE:
                    // 待实现
                    // 将拒绝请求转发给web端
                    if(MapUntil.getData(fileProtocol.getPreFileName(),fileProtocol.getPreFileName())==false) {
                        json.put("file", MsgUtil.FileExitByte(fileProtocol));
                        json.put("fileName", fileProtocol.getFileName());
                        json.put("isAcept", "1");
                        RememberFile rememberFile1 = new RememberFile();
                        String content1 = rememberFile1.getContent(PropertiesUntil.STORY_FILE_PATH);//作为客户端的文件记录路径
                        List<FileModel> list1 = JSONArray.parseArray(content1, FileModel.class);
                        FileProtocol finalFileProtocol = fileProtocol;
                        list1.stream().filter(s -> s.getFileName().equals(finalFileProtocol.getFileName())).forEach(s -> s.setIsAcept(1));
                        String data1 = JSON.toJSONString(list1);
                        // 更新文本数据库
                        rememberFile1.updateFile(data1, PropertiesUntil.STORY_FILE_PATH);
                        WebSocketUploadServer.sendInfo(json.toJSONString(), "2");
                    }
                    break;

                // 1.服务器同意传输文件
                case TransferType.AGREE:
                    // 待实现
                    // 将同意请求转发到web端

                    // 传输第一个文件
                    fileProtocol.setFileIndex(1);
                    FileUtil.readFile(fileProtocol);
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.TRANSFER,TransferType.CLIENT_DOWN));
                    break;
                // 2.客户端继续传输文件
                case TransferType.TRANSFER:
                    //待实现
                    // 将当前进度转给web端
                    persont = Double.valueOf(fileProtocol.getNextFileIndex() * 1.0 / fileProtocol.getTotalFileIndex() * 1.0) * 100;
                    json.put("file",MsgUtil.FileExitByte(fileProtocol));
                    json.put("fileName",fileProtocol.getFileName());
                    json.put("isAcept", "0");
                    json.put("isSend", "1");
                    json.put("documentProgress", String.valueOf(Math.round(persont)));
                    WebSocketUploadServer.sendInfo(json.toJSONString(), "2");
                    // 如果传输完成,则继续传下一个分片文件
                    fileProtocol.setFileIndex(fileProtocol.getNextFileIndex());
                    FileUtil.readFile(fileProtocol);
                    if (fileProtocol.isFINISH() || !fileProtocol.isHasNextFileIndex()) {
                        // 如果传输完成
                        ctx.writeAndFlush(MsgUtil.createClientProtocol(MsgUtil.FileExitByte(fileProtocol), TransferType.FINISH,TransferType.CLIENT_DOWN));
                    } else {
                        ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.TRANSFER,TransferType.CLIENT_DOWN));
                    }
                    break;
                // 3.服务器通知文件传输完成
                case TransferType.FINISH:
                    // 待实现
                    // 通知web端传输完成
//                    System.err.println("传输完成1");
                    RememberFile rememberFile = new RememberFile();
                    String content = rememberFile.getContent(PropertiesUntil.STORY_FILE_PATH);//作为客户端的文件记录路径
                    List<FileModel> list = JSONArray.parseArray(content, FileModel.class);
                    FileProtocol finalFileProtocol1 = fileProtocol;
                    list.stream().filter(s -> s.getFileName().equals(finalFileProtocol1.getFileName())).forEach(s -> s.setDocumentProgress("100"));
                    String data = JSON.toJSONString(list);
                    // 更新文本数据库
                    rememberFile.updateFile(data, PropertiesUntil.STORY_FILE_PATH);
                    //
                    //list.add(student);
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(null, TransferType.HEART,TransferType.CLIENT_DOWN));
                    break;
                case TransferType.PACKET_LOSS:
                    FileUtil.readFile(fileProtocol);
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.TRANSFER,TransferType.CLIENT_DOWN));
                    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 客户端传输文件信息。 FILE：" + fileProtocol.getAbsolutePath());
                    break;
                default:
                    break;
            }

        }

        /**模拟传输过程中断，场景测试可以注释掉
         */
       /* System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 客户端传输文件信息[主动断开链接，模拟断点续传]");
        ctx.flush();
        ctx.close();
        System.exit(-1);*/
    }

   // private boolean 1;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("链接报告开始");
        System.out.println("链接报告信息：本客户端链接到服务端。channelId：" + channel.id());
        System.out.println("链接报告IP:" + channel.localAddress().getHostString());
        System.out.println("链接报告Port:" + channel.localAddress().getPort());
        System.out.println("链接报告完毕");
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开链接" + ctx.channel().localAddress().toString());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg == null){
            return;
        }

        ByteBuf in = (ByteBuf)msg;
//        if(in.getByte(0)==99){
////            in.clear();
//            byte[] data = new byte[in.readableBytes()];
//            in.readBytes(data);
//            return;
//        }

        try{
            inCache.writeBytes(in);
        }finally {
            ReferenceCountUtil.release(msg);
        }
        ok = true;
    }

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       // ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }
}
