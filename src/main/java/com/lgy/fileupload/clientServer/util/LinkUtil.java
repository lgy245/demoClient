package com.lgy.fileupload.clientServer.util;

import com.lgy.fileupload.clientServer.client.NettyClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Desc:
 * @Author:lgy
 * @Time:2021/7/517:02
 */
public class LinkUtil {
    public static   ChannelFuture channelFuture=null;
    public void con(String host,Integer port){
        if(host==null&&host.length()==0){
            host = "19.86.11.85";
        }
        if(null == port){
            port = 5230;
        }
        this.channelFuture = new NettyClient().connect(host, port);

    }
}
