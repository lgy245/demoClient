package com.lgy.fileupload.clientServer.codec;

import com.lgy.fileupload.clientServer.domain.Protocol;
import com.lgy.fileupload.clientServer.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

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
        // 标记字节流开始位置
        if (in.readableBytes() < 4) {
            return;
        }  if(in.getByte(0)==99){
//            in.clear();
            byte[] data = new byte[in.readableBytes()];
            in.readBytes(data);
            // 创建二维码成功
            out.add(new Protocol());
            return;
        }
        in.markReaderIndex();
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        Object obj = SerializationUtil.deserialize(data, genericClass);
        if(obj == null){
            in.resetReaderIndex();
            return;
        }
        out.add(obj);
    }

}
