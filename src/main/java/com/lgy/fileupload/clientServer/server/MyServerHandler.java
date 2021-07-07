package com.lgy.fileupload.clientServer.server;

import com.alibaba.fastjson.JSONObject;
import com.lgy.fileupload.clientServer.domain.FileProtocol;
import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
import com.lgy.fileupload.clientServer.domain.TransferType;
import com.lgy.fileupload.clientServer.util.ByteUtil;
import com.lgy.fileupload.clientServer.util.FileUtil;
import com.lgy.fileupload.clientServer.util.MsgUtil;

import com.lgy.fileupload.service.FileService;
import com.lgy.fileupload.util.RememberFile;
import com.lgy.fileupload.webSocket.demo.WebSocketUploadServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
* socket服务端处理器
* @author:zhengs
* @Time: 21-6-30 下午7:25
* @Copyright: ©  杭州凯立通信有限公司 版权所有
* @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
*/
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    public static ChannelHandlerContext x;
    public static Object  m ;
    public static String path ="" ;

    @Autowired
    FileService fileService;
    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("链接报告开始");
        System.out.println("链接报告信息：有一客户端链接到本服务端。channelId：" + channel.id());
        System.out.println("链接报告IP:" + channel.localAddress().getHostString());
        System.out.println("链接报告Port:" + channel.localAddress().getPort());
        System.out.println("链接报告完毕");
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开链接" + ctx.channel().localAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        x=ctx;
        m=msg;
        //数据格式验证
        if (!(msg instanceof FileTransferProtocol)){
            System.err.println("格式错误");
            return;
        }

        FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
        FileProtocol fileProtocol = (FileProtocol) fileTransferProtocol.getTransferObj();
        //0:请求传输文件,、1文件传输'指令'、2文件传输'数据'
        switch (fileTransferProtocol.getTransferType()) {
            // [客户端]请求传输文件
            case TransferType.REQUEST:
                // 待实现
                // 将请求转发给web端
                // 将文件名称转发到web端
                JSONObject json = new JSONObject();
                json.put("fileName",fileProtocol.getFileName());
                json.put("isAcept","0");
                json.put("isSend","0");
                json.put("documentProgress","0");
                WebSocketUploadServer.sendInfo(json.toJSONString(),"2");
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
                    path = new RememberFile().dowondPath(fileProtocol.getFileIndexName(),fileProtocol.getTotalFileIndex());
                    Double  persont2 = Double.valueOf(fileProtocol.getNextFileIndex()*1.0/fileProtocol.getTotalFileIndex()*1.0)*100;
                    JSONObject jsonT= new JSONObject();
                    jsonT.put("fileName",fileProtocol.getFileName());
                    jsonT.put("isAcept","0");
                    jsonT.put("isSend","0");
                    jsonT.put("documentProgress",String.valueOf(persont2));
                    jsonT.put("path",path);
                    WebSocketUploadServer.sendInfo(jsonT.toJSONString(),"2");
                }else{

                    ctx.writeAndFlush(MsgUtil.createServerProtocol(fileProtocol, TransferType.TRANSFER));
                    JSONObject json2 = new JSONObject();
                    Double  persont3 = Double.valueOf(fileProtocol.getNextFileIndex()*1.0/fileProtocol.getTotalFileIndex()*1.0)*100;

                    json2.put("fileName",fileProtocol.getFileName());
                    json2.put("isAcept","0");
                    json2.put("isSend","0");
                    json2.put("documentProgress",String.valueOf(persont3));
                    WebSocketUploadServer.sendInfo(json2.toJSONString(),"2");
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

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }
}
