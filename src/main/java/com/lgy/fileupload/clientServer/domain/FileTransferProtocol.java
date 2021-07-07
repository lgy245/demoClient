package com.lgy.fileupload.clientServer.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件传输协议
 * 虫洞栈：https://bugstack.cn
 * 公众号：bugstack虫洞栈  ｛获取学习源码｝
 * 虫洞群：5360692
 * Create by fuzhengwei on @2019
 */
@Data
public class FileTransferProtocol implements Serializable {
    /**
    * 0:请求传输文件
     * 1:同意传输文件
     * 2:拒绝传输文件
     * 3:文件传输数据
    */
    private Integer transferType;
    private Integer isSend;

    /**
    * 数据对象
    */
    private Object transferObj;
}
