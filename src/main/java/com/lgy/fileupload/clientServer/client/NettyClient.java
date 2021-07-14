package com.lgy.fileupload.clientServer.client;

import com.lgy.fileupload.clientServer.util.LinkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    //配置服务端NIO线程组
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture connect(String inetHost, int inetPort) throws InterruptedException {
        ChannelFuture channelFuture = null;
        LinkUtil.host = inetHost;
        LinkUtil.port = inetPort;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new MyChannelInitializer());
            channelFuture = b.connect(inetHost, inetPort).syncUninterruptibly();
            this.channel = channelFuture.channel();
            LinkUtil.channelFuture = channelFuture;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                System.out.println("filetransfer-netty client start sucess.");
                LinkUtil.start = true;
            } else {
                System.out.println("filetransfer-netty client start error.");
                LinkUtil.start = false;
                while(!LinkUtil.start){
                    Thread.sleep(3000);
                    System.err.println("重启中");
                    this.connect(LinkUtil.host,LinkUtil.port);
                }
            }
        }
        return channelFuture;
    }

    public void destroy() {
        if (null == channel) return;
        channel.close();
        workerGroup.shutdownGracefully();
    }

}
