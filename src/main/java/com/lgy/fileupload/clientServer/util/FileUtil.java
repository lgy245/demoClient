package com.lgy.fileupload.clientServer.util;


import com.lgy.fileupload.clientServer.domain.FileProtocol;
import com.lgy.fileupload.util.PropertiesUntil;

import java.io.*;

/**
 * 文件读写工具
 * 虫洞栈：https://bugstack.cn
 * 公众号：bugstack虫洞栈  ｛获取学习源码｝
 * 虫洞群：5360692
 * Create by fuzhengwei on @2019
 */
public class FileUtil {

    /**
    * 接收分段文件的路径
    */
    private static final String RECEIVE_SPLIT_PATH = PropertiesUntil.SERVER_SPLIT_PATH;

    /**
    * byte[] 转为 文件
    * @author:zhengs
    * @Time: 21-6-30 下午7:30
    * @Copyright: ©  杭州凯立通信有限公司 版权所有
    * @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
    */
    public static void bytesToFile(byte[] bytes, String filePath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {

            file = new File(filePath);
            if (!file.getParentFile().exists()){
                //文件夹不存在 生成
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    * 文件 转为 byte[]
    * @author:zhengs
    * @Time: 21-6-30 下午7:30
    * @param file
    * @return
    */
    private static byte[] fileToBytes(File file) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
    * 读取文件
    * @author:zhengs
    * @Time: 21-6-30 下午7:32
    * @param fileProtocol
    * @return
    */
    public static FileProtocol readFile(FileProtocol fileProtocol) throws IOException {
        fileProtocol.setBytes(fileToBytes(new File(fileProtocol.getAbsolutePath())));
        return fileProtocol;
    }

    /**
    * 写入文件
    * @author:zhengs
    * @Time: 21-6-30 下午7:32
    * @param fileProtocol
    * @return
    */
    public static FileProtocol writeFile(FileProtocol fileProtocol) throws IOException {
        // 1.判断分片文件是否已存在
        if(!fileProtocol.checkSubFileIsExists()){
            // 2.保存文件
            bytesToFile(fileProtocol.getBytes(), RECEIVE_SPLIT_PATH + fileProtocol.getFileIndexName());

            // 3.设置当前文件分片装备为完成
            fileProtocol.setSubFileStatus();
        }

        return fileProtocol;
    }

}
