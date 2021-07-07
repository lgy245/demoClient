package com.lgy.fileupload.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lgy.fileupload.model.FileModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @Desc:记录文件数据库
 * @Author:lgy
 * @Time:2021/6/3010:55
 */
@Slf4j
public class RememberFile {

    public String getContent(String filePath){
        String thisLine = null;
        StringBuffer sb = new StringBuffer();
        File file = new File(filePath);
        if(file.exists()&&file.isFile()){
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                while((thisLine = br.readLine())!=null){
                    sb .append(thisLine+"\n");
                }
            }catch (IOException e){
                log.info("IOException", e);
            }

        }
        log.info("读取内容, ", sb);
        return sb.toString();
    }
    public boolean updateFile(String data,String filePath){
        boolean flag = false;
        File thisFile = new File(filePath);
        try{
            if(!thisFile.exists()){
                thisFile.createNewFile();
            }
            FileWriter fw = new FileWriter(filePath,false);
            fw.write(data+"\r\n");

            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return flag=true;
    }

  /*  public static void main(String[] args) {
        RememberFile a = new RememberFile();
        List<FileModel> list = new ArrayList<>();
        JSONObject listJson = new JSONObject();
        FileModel fileModel = new FileModel();
        fileModel.setId((UUID.randomUUID().toString()).replace("-", ""));// 唯一编码
        fileModel.setFileName("fileName");
        fileModel.setDocumentProgress("0");
        fileModel.setIsAcept(0);
        list.add(fileModel);
        listJson.put("list", list);
        String data = listJson.toString();
        //a.updateFile(data);
         String content = a.getContent();
        JSONObject jsonObject = JSONObject.parseObject(content);
        System.out.println(jsonObject.get("list").toString());

    }*/

    //合并文件，写入传输列表，返回下载链接
    public String dowondPath(String fileNames,int size) throws IOException {
        String fileName = new CutFileUtil().merageFile(PropertiesUntil.SERVER_SPLIT_PATH,fileNames,size);
        log.info("fileName"+fileName);
        // 写入传输列表
        RememberFile rememberFile = new RememberFile();
        String content = rememberFile .getContent(PropertiesUntil.SERVER_STORY_FILE_PATH);// 作为服务端接受文件  记录文件数据库的位置
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
        rememberFile.updateFile(data,PropertiesUntil.SERVER_STORY_FILE_PATH);
        //返回下载链接
        String local = "http://19.86.11.20:8082/downloadFile/";
        String path =  UriComponentsBuilder.newInstance().path(local)// 下载方法
                .path(fileName)
                .toUriString();
        return path ;


    }
    
    

}
