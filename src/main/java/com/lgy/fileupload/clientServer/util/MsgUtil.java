package com.lgy.fileupload.clientServer.util;


import com.lgy.fileupload.clientServer.domain.FileProtocol;
import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
import com.lgy.fileupload.clientServer.domain.TransferType;

/**
* 消息构建工具
* @author:zhengs
* @Time: 21-6-30 下午7:26
* @Copyright: ©  杭州凯立通信有限公司 版权所有
* @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
*/
public class MsgUtil {

    /**
     * 构建对象；请求传输文件(客户端)
     *
     * @param fileUrl  客户端文件地址
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @return 传输协议
     */
    public static FileTransferProtocol createClientProtocol(Integer isSend,String fileUrl, String preFileName, String fileName, Long fileSize, Integer totalFileIndex, char[] statusArray) {
        FileProtocol fileProtocol = new FileProtocol();
        fileProtocol.setFilePath(fileUrl);
        fileProtocol.setPreFileName(preFileName);
        fileProtocol.setFileName(fileName);
        fileProtocol.setFileSize(fileSize);
        fileProtocol.setTotalFileIndex(totalFileIndex);
        fileProtocol.setStatusArray(statusArray);

        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(TransferType.REQUEST);
        fileTransferProtocol.setTransferObj(fileProtocol);
        //fileTransferProtocol.setIsSend(TransferType.CLIENT_SEND);
        fileTransferProtocol.setIsSend(isSend);


        return fileTransferProtocol;

    }

    /**
    * [客户端] 构建文件传输协议
    * @author:zhengs
    * @Time: 21-6-30 下午7:36
    * @param fileProtocol
    * @return
    */
    public static FileTransferProtocol createClientProtocol(FileProtocol fileProtocol, Integer transferType,Integer isSend) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(transferType);
        fileTransferProtocol.setTransferObj(fileProtocol);
        fileTransferProtocol.setIsSend(isSend);
        return fileTransferProtocol;
    }

    /**
    * [服务端]构建文件传输协议
    * @author:zhengs
    * @Time: 21-6-30 下午7:37
    * @param fileProtocol
    * @param transferType
    * @return
    */
    public static FileTransferProtocol createServerProtocol(FileProtocol fileProtocol,Integer transferType,Integer isSend) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(transferType);
        fileProtocol.setBytes(null);
        fileTransferProtocol.setTransferObj(fileProtocol);
       // fileTransferProtocol.setIsSend(TransferType.CLIENT_DOWN);
        fileTransferProtocol.setIsSend(isSend);

        return fileTransferProtocol;
    }
    /**
     * 将不存在byte 的对象转发到web端
     */
    public static FileProtocol FileExitByte(FileProtocol  fileProtocol){
        fileProtocol.setBytes(null);
        return fileProtocol;
    }
}
