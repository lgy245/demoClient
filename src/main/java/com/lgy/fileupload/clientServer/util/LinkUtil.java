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
    public void con(){
        this.channelFuture = new NettyClient().connect("19.86.11.85", 5230);

    }
}
