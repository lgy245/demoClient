package com.lgy.fileupload.clientServer.domain;

import lombok.Data;
import org.apache.commons.lang.ArrayUtils;

import java.io.Serializable;

/**
* 文件传输协议
* @author:zhengs
* @Time: 21-6-30 下午6:50
* @Copyright: ©  杭州凯立通信有限公司 版权所有
* @Warning: 本内容仅限于公司内部传阅,禁止外泄或用于其它商业目的
*/
@Data
public class FileProtocol implements Serializable {
    /**
    * 文件路径
    */
    private String filePath;

    /**
     * 文件名前缀
     */
    private String preFileName;

    /**
     * 分片文件的第几片
     */
    private Integer fileIndex;

    /**
     * 分片文件总数
     */
    private Integer totalFileIndex;

    /**
    * 文件名
    */
    private String fileName;

    /**
    * 文件大小
    */
    private Long fileSize;

    /**
     * 所有分片文件传输状态
     */
    private char[] statusArray;

    /**
    * 文件内容
    */
    private byte[] bytes;

    /**
    * 获取分片文件的绝对路径
    * @author:zhengs
    * @Time: 21-6-30 下午6:41
    *
    * @return
    */
    public String getAbsolutePath(){
        return this.getFilePath()+this.getPreFileName()+"-"+this.getFileIndex()+"-"+this.getFileName();
    }

    /**
     * 获取分片文件的名称
     * @author:zhengs
     * @Time: 21-6-30 下午6:41
     *
     * @return
     */
    public String getFileIndexName(){
        return this.getPreFileName()+"-"+this.getFileIndex()+"-"+this.getFileName();
    }

    /**
    * 判断文件是否还有下一分片
    * @author:zhengs
    * @Time: 21-6-30 下午6:57
    *
    * @return
    */
    public boolean isHasNextFileIndex(){
        if(this.fileIndex == null){
            this.fileIndex = 0;
        }
        if(this.fileIndex < this.totalFileIndex){
            return true;
        }

        return false;
    }

    /**
    * 获取第一个未传文件的下标
    * @author:zhengs
    * @Time: 21-7-1 下午3:27
    *
    * @return
    */
    public Integer getFristFileIndex(){
        for(int i = 0 ; i< this.statusArray.length ;i++){
            if(this.statusArray[i] == '0'){
                return i+1;
            }
        }

        return this.getTotalFileIndex();
    }

    /**
    * 返回下一个未接收的分片序号
    * @author:zhengs
    * @Time: 21-6-30 下午6:59
    *
    * @return
    */
    public Integer getNextFileIndex(){
        if(this.fileIndex < this.totalFileIndex){
            if(this.statusArray[this.fileIndex] == '0'){
                return this.fileIndex+1;
            }else{
                this.fileIndex++;
                return this.getNextFileIndex();
            }
        }

        return this.totalFileIndex;
    }

    /**
    * 判断是否是最后一个分片
    * @author:zhengs
    * @Time: 21-7-1 上午9:25
    *
    * @return
    */
    public boolean isLastFileIndex(){
        return this.fileIndex >= this.totalFileIndex;
    }

    /**
    *  设置当前文件费分片传输进度为完成
    * @author:zhengs
    * @Time: 21-7-1 上午9:44
    *
    * @return
    */
    public void setSubFileStatus(){
        if(this.fileIndex <= this.totalFileIndex){
            this.statusArray[this.fileIndex -1] = '1';
        }
    }

    /**
    * 检查该分片文件是否已存在
    * @author:zhengs
    * @Time: 21-7-1 上午9:54
    *
    * @return
    */
    public boolean checkSubFileIsExists(){
        if(this.fileIndex <= this.totalFileIndex && this.statusArray[this.fileIndex -1] == '1'){
            return true;
        }

        return false;
    }

    /**
    * 判断所有分片是否传输完成
    * @author:zhengs
    * @Time: 21-7-1 上午10:07
    *
    * @return
    */
    public boolean isFINISH(){
        return !ArrayUtils.contains(this.statusArray, '0');
    }
}