package com.lgy.fileupload.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 文件分割+合并
 * @author:zhengs
 * @Time: 21-6-30 下午7:11
 * @Copyright: ©  杭州凯立通信有限公司 版权所有
 * @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
 */
public class CutFileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CutFileUtil.class);

    /**
     * 文件分割大小 4KB
     */
    private static final Long SPLIT_SIZE = 1024L;

    /**
     * 文件分割后存储路径
     */
    private static final String SPLIT_PATH = PropertiesUntil.SPLIT_PATH;

    /**
     * 文件合并
     * @author:zhengs
     * @Time: 21-6-29 下午4:39
     * @param cutFileName 任意一个分割文件的名称
     * @param chunks 分割文件总数
     * @return
     */
    public String merageFile(String cutFileName, int chunks) throws IOException {
        return merageFile(SPLIT_PATH, cutFileName, chunks);
    }

    /**
     * 分割文件
     * @author:zhengs
     * @Time: 21-6-29 下午4:34
     * @param filePath 待分割文件路径
     * @return
     */
    public List<String> splitFile(String filePath){
        List<String> fileNames = new ArrayList<>();
        File file = new File(filePath);
        //计算总共段数
        int count = (int) Math.ceil(file.length()/(double)SPLIT_SIZE);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,4,2,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(count * 2));
        //时间戳
        String timeStamp = String.valueOf(System.currentTimeMillis())+new Random().nextInt(1000);

        for (int i = 0; i < count; i++) {
            //分段文件名
            String fileName = timeStamp + "-" + (i+1)  + "-" +file.getName();
            threadPoolExecutor.execute(new SplitRunnable(SPLIT_SIZE.intValue(), fileName, file, i*SPLIT_SIZE, SPLIT_PATH));
            fileNames.add(fileName);
        }
        threadPoolExecutor.shutdown();
        while (true){
            if (threadPoolExecutor.isTerminated()){
                return fileNames;
            }
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private class SplitRunnable implements Runnable{
        int byteSize;
        String fileName;
        File originFile;
        Long startPos;
        String currentWorkDir;

        public SplitRunnable(int byteSize, String fileName, File originFile, Long startPos, String currentWorkDir) {
            this.byteSize = byteSize;
            this.fileName = fileName;
            this.originFile = originFile;
            this.startPos = startPos;
            this.currentWorkDir = currentWorkDir;
        }

        @Override
        public void run(){
            RandomAccessFile randomAccessFile = null;
            OutputStream outputStream = null;
            try {
                randomAccessFile = new RandomAccessFile(originFile, "r");
                byte[] b = new byte[byteSize];
                //移动指针到每“段”开头
                randomAccessFile.seek(startPos);
                int s = randomAccessFile.read(b);
                outputStream = new FileOutputStream(currentWorkDir+fileName);
                outputStream.write(b, 0 , s);
                outputStream.flush();
                b= null;
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if (outputStream !=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (randomAccessFile !=null){
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 文件合并
     * @author:zhengs
     * @Time: 21-6-29 下午4:39
     * @param cutFileName 任意一个分割文件的名称
     * @param chunks 分割文件总数
     * @return
     */
    public String merageFile(String rootPath, String cutFileName, int chunks) throws IOException {
        int indexOf = cutFileName.indexOf("-");
        String timeStream = cutFileName.substring(0, indexOf);
        //段数+文件名+后缀名
        String substring = cutFileName.substring(indexOf + 1, cutFileName.length());
        int indexOf1 = substring.indexOf("-");
        //文件名+后缀名
        String fileName = substring.substring(indexOf1+1, substring.length());
        File file = new File(rootPath+fileName);
        if (file.exists()){
            file.delete();
            LOGGER.info("覆盖已经存在的文件");
        }
        BufferedOutputStream destOutputStream = new BufferedOutputStream(new FileOutputStream(rootPath+fileName));
        for (int i = 1; i <= chunks ; i++) {
            //循环将每个分片的数据写入目标文件
            byte[] fileBuffer = new byte[1024];//文件读写缓存
            int readBytesLength = 0; //每次读取字节数
            File sourceFile = new File(rootPath+timeStream+"-"+i+"-"+fileName);
            BufferedInputStream sourceInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            LOGGER.info("开始合并分段文件："+timeStream+"-"+i+"-"+fileName);
            while ((readBytesLength = sourceInputStream.read(fileBuffer))!=-1){
                destOutputStream.write(fileBuffer, 0 , readBytesLength);
            }
            sourceInputStream.close();
            LOGGER.info("合并分段文件完成："+timeStream+"-"+i+"-"+fileName);
            //分片合并后删除
            boolean delete = sourceFile.delete();
            if (delete){
                LOGGER.info(timeStream+"-"+i+"-"+fileName+"删除完成");
            }
        }
        destOutputStream.flush();
        destOutputStream.close();
        return fileName;
    }

    public static void main(String[] args){
//        // 1.分割文件
//        String filePath = "/home/zs/code/websocketupload/data/1.png";
//        List<String> fileNames = new CutFileUtil().splitFile(filePath);
//
//        // 2.合并文件
//        try {
//            new CutFileUtil().merageFile(fileNames.stream().findFirst().orElse(null), fileNames.size());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            String rootPath = "E:\\data\\server\\server\\split\\";
            new CutFileUtil().merageFile(rootPath, "1625644696133344-1-sn明细.xlsx", 6);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}