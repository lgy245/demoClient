package com.lgy.fileupload.clientServer.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lgy.fileupload.clientServer.domain.FileProtocol;
import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
import com.lgy.fileupload.clientServer.domain.TransferType;
import com.lgy.fileupload.clientServer.util.FileUtil;
import com.lgy.fileupload.clientServer.util.MsgUtil;
import com.lgy.fileupload.model.FileModel;
import com.lgy.fileupload.util.PropertiesUntil;
import com.lgy.fileupload.util.RememberFile;
import com.lgy.fileupload.webSocket.demo.WebSocketUploadServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
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
    private boolean isQuest=false;
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
        x=ctx;
        m=msg;
        //数据格式验证
        if (!(msg instanceof FileTransferProtocol)){
            return;
        }else{
            System.err.println(msg);
        }
        FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
        FileProtocol fileProtocol = (FileProtocol) fileTransferProtocol.getTransferObj();
        if(fileTransferProtocol.getIsSend()==8) {

            // 0:请求传输文件, 1:同意传输文件, 2:文件传输'数据'
            switch (fileTransferProtocol.getTransferType()) {
                // 1.服务器同意传输文件
                case TransferType.REFUSE:
                    // 待实现
                    // 将拒绝请求转发给web端
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fileName", fileProtocol.getFileName());
                    jsonObject.put("isAcept", "1");
                    RememberFile rememberFile1 = new RememberFile();
                    String content1 = rememberFile1.getContent(PropertiesUntil.STORY_FILE_PATH);//作为客户端的文件记录路径
                    List<FileModel> list1 = JSONArray.parseArray(content1, FileModel.class);
                    FileProtocol finalFileProtocol = fileProtocol;
                    list1.stream().filter(s -> s.getFileName().equals(finalFileProtocol.getFileName())).forEach(s -> s.setIsAcept(1));
                    String data1 = JSON.toJSONString(list1);
                    // 更新文本数据库
                    rememberFile1.updateFile(data1, PropertiesUntil.STORY_FILE_PATH);
                    WebSocketUploadServer.sendInfo(jsonObject.toJSONString(), "1");
                    break;

                // 1.服务器同意传输文件
                case TransferType.AGREE:
                    // 待实现
                    // 将同意请求转发到web端

                    // 传输第一个文件
                    fileProtocol.setFileIndex(1);
                    FileUtil.readFile(fileProtocol);
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.TRANSFER));
                    break;
                // 2.客户端继续传输文件
                case TransferType.TRANSFER:
                    JSONObject jsonTRANSFER = new JSONObject();
                    //待实现
                    // 将当前进度转给web端
//                    persont = Double.valueOf(fileProtocol.getNextFileIndex() * 1.0 / fileProtocol.getTotalFileIndex() * 1.0) * 100;
//                    jsonTRANSFER.put("fileName", fileProtocol.getFileName());
//                    jsonTRANSFER.put("isAcept", "0");
//                    jsonTRANSFER.put("isSend", "1");
//                    jsonTRANSFER.put("documentProgress", String.valueOf(Math.round(persont)));
//                    WebSocketUploadServer.sendInfo(jsonTRANSFER.toJSONString(), "1");

                    // 如果传输完成,则继续传下一个分片文件
                    fileProtocol.setFileIndex(fileProtocol.getNextFileIndex());
                    FileUtil.readFile(fileProtocol);

                    if (fileProtocol.isFINISH() || !fileProtocol.isHasNextFileIndex()) {
                        // 如果传输完成
                        ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.FINISH));
                    } else {
                        ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.TRANSFER));
                    }
                    break;

                // 3.服务器通知文件传输完成
                case TransferType.FINISH:
                    // 待实现
                    // 通知web端传输完成
//                    System.err.println("传输完成1");
//                    RememberFile rememberFile = new RememberFile();
//                    String content = rememberFile.getContent(PropertiesUntil.STORY_FILE_PATH);//作为客户端的文件记录路径
//                    List<FileModel> list = JSONArray.parseArray(content, FileModel.class);
//                    FileProtocol finalFileProtocol1 = fileProtocol;
//                    list.stream().filter(s -> s.getFileName().equals(finalFileProtocol1.getFileName())).forEach(s -> s.setDocumentProgress("100"));
//                    String data = JSON.toJSONString(list);
//                    // 更新文本数据库
//                    rememberFile.updateFile(data, PropertiesUntil.STORY_FILE_PATH);
                    //
                    //list.add(student);
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.HEART));
                    break;
                case TransferType.PACKET_LOSS:
                    FileUtil.readFile(fileProtocol);
                    ctx.writeAndFlush(MsgUtil.createClientProtocol(fileProtocol, TransferType.TRANSFER));
                    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 客户端传输文件信息。 FILE：" + fileProtocol.getAbsolutePath());
                    break;
                default:
                    break;
            }
        }else{
            switch (fileTransferProtocol.getTransferType()) {
                // [客户端]请求传输文件
                case TransferType.REQUEST:
                    // 待实现
                    // 将请求转发给web端
                    // 将文件名称转发到web端
                    if(!isQuest) {
                        JSONObject json = new JSONObject();
                        json.put("fileName", fileProtocol.getFileName());
                        json.put("isAcept", "0");
                        json.put("isSend", "0");
                        json.put("documentProgress", "0");
                        WebSocketUploadServer.sendInfo(json.toJSONString(), "2");
                        isQuest = true;
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
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(fileProtocol, TransferType.FINISH));
//                        path = new RememberFile().dowondPath(fileProtocol.getFileIndexName(),fileProtocol.getTotalFileIndex());
//                        Double  persont2 = Double.valueOf(fileProtocol.getNextFileIndex()*1.0/fileProtocol.getTotalFileIndex()*1.0)*100;
//                        JSONObject jsonT= new JSONObject();
//                        jsonT.put("fileName",fileProtocol.getFileName());
//                        jsonT.put("isAcept","0");
//                        jsonT.put("isSend","0");
//                        jsonT.put("documentProgress",String.valueOf(persont2));
//                        jsonT.put("path",path);
//                        WebSocketUploadServer.sendInfo(jsonT.toJSONString(),"2");
                    }else{

                        ctx.writeAndFlush(MsgUtil.createServerProtocol(fileProtocol, TransferType.TRANSFER));
//                        JSONObject json2 = new JSONObject();
//                        Double  persont3 = Double.valueOf(fileProtocol.getNextFileIndex()*1.0/fileProtocol.getTotalFileIndex()*1.0)*100;
//
//                        json2.put("fileName",fileProtocol.getFileName());
//                        json2.put("isAcept","0");
//                        json2.put("isSend","0");
//                        json2.put("documentProgress",String.valueOf(persont3));
//                        WebSocketUploadServer.sendInfo(json2.toJSONString(),"2");
                    }
                    // 转发进度到web端

                    break;

                // [客户端]传输文件完成
                case TransferType.FINISH:
                    // 判断是否存在未丢失分片
                    if(fileProtocol.isFINISH()){
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(fileProtocol, TransferType.FINISH));
                        System.err.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 传输完成。");
                    }else{
                        // 设置第一个未传分片文件的位置
                        fileProtocol.setFileIndex(fileProtocol.getFristFileIndex());
                        ctx.writeAndFlush(MsgUtil.createServerProtocol(fileProtocol, TransferType.PACKET_LOSS));
                    }
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

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       // ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }
}
