package com.lgy.fileupload.clientServer.codec;

import com.lgy.fileupload.clientServer.util.ByteUtil;
import com.lgy.fileupload.clientServer.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.boot.convert.Delimiter;

import java.util.List;

/**
* 解码
* @author:zhengs
* @Time: 21-6-30 下午7:24
* @Copyright: ©  杭州凯立通信有限公司 版权所有
* @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
*/
public class ObjDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public ObjDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws InterruptedException {
        System.out.println(in.getByte(0));
        System.out.println(in);
        // 标记字节流开始位置
        if(in.getByte(0)!=8&&in.getByte(0)!=7){
            return;
        }
        if (in.readableBytes() < 4) {
            return;
        }
        byte[] data = new byte[ in.readableBytes()];
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(data, genericClass));
    }

}
