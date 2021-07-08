package com.lgy.fileupload.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lgy.fileupload.clientServer.client.NettyClient;
import com.lgy.fileupload.clientServer.domain.FileTransferProtocol;
import com.lgy.fileupload.clientServer.domain.TransferType;
import com.lgy.fileupload.clientServer.util.LinkUtil;
import com.lgy.fileupload.clientServer.util.MsgUtil;
import com.lgy.fileupload.model.FileModel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc:线程类
 * @Author:lgy
 * @Time:2021/6/3016:14
 */
@Slf4j
public class ThreadClass {
    //具体产生何种线程池看个人需求，Executors提供多种线程池供选择
    //不推荐使用Executor创建线程池（可能会引发oom），此处只是演示,推荐根据实际场景用原生ThreadPoolExecutor创建。
    ExecutorService executor = Executors.newCachedThreadPool();
    ExecutorService executor2 = Executors.newCachedThreadPool();
    public  void StorageFile(String fileName){
        executor.submit(()->{
            System.out.println("正在存储数据，请稍等");
            try {
                RememberFile rememberFile = new RememberFile();
                String content = rememberFile .getContent(PropertiesUntil.STORY_FILE_PATH);// 作为客户端 记录文本数据库的位置
                List<FileModel> list = null;
                if (!content.isEmpty()){
                    try {
                        list =  JSONArray.parseArray(content,FileModel.class);
                    }catch (Exception e){
                        log.info("读取文件转换list 失败, : {}", e);
                    }
                }else {
                    list = new ArrayList<>();
                }
                //转换成list<fileModel>
                FileModel fileModel = new FileModel();
                fileModel.setId((UUID.randomUUID().toString()).replace("-", ""));// 唯一编码
                fileModel.setFileName(fileName);
                fileModel.setDocumentProgress("0");
                fileModel.setIsAcept(0);
                list.add(fileModel);
                String data = JSON.toJSONString(list);
                // 更新文本数据库
                rememberFile.updateFile(data,PropertiesUntil.STORY_FILE_PATH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("业务执行完成1");
        });
    }
    public  void fileFragmentation(String fileName){
        executor2.submit(()->{
            System.out.println("正在进行分片传输，请稍等");
            try {

                // 拼接地址
                String path = PropertiesUntil.SERVER_STORY+fileName;//作为客户端存储文件路径
                System.out.println(path+"分片储存的拼接地址");
                // 文件分片
                List<String> fileNames = new CutFileUtil().splitFile(path);
                System.out.println(fileNames.size());
                // 启动客户端
                ChannelFuture channelFuture = new LinkUtil().channelFuture;
                FileTransferProtocol fileTransferProtocol = null;
                // 文件信息
                File file = new File(path);
                File splitFile =new File(PropertiesUntil.SPLIT_PATH+fileNames.stream().findFirst().orElse(null));
                char[] transfers = new char[fileNames.size()];
                for(int i = 0;i<transfers.length;i++){
                    transfers[i] = '0';
                }
                fileTransferProtocol = MsgUtil.createClientProtocol(TransferType.CLIENT_DOWN,PropertiesUntil.SPLIT_PATH, splitFile.getName().split("-")[0], file.getName(), file.length(), fileNames.size(),transfers);
                channelFuture.channel().writeAndFlush(fileTransferProtocol);

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("业务执行完成2");
        });
    }


}
