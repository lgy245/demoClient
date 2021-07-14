package com.lgy.fileupload.clientServer.domain;

import com.lgy.fileupload.util.MapUntil;
import com.lgy.fileupload.util.PropertiesUntil;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;

import java.io.Serializable;
import java.util.List;

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
     * 文件名前缀
     */
    private String preFileName;
    /**
     * 文件id
     */
    private String fileId;

    /**
     * 分片文件的第几片
     */
    private int fileIndex;

    /**
     * 分片文件总数
     */
    private int totalFileIndex;

    /**
    * 文件名
    */
    private String fileName;

  //  private char[] lossPackage;
    private List<Integer> lossPackage;

    /**
     * 所有分片文件传输状态
     */
//    private char[] statusArray;

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
        return PropertiesUntil.SPLIT_PATH+this.getPreFileName()+"-"+this.getFileIndex()+"-"+this.getFileName();
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
        char[] statusArray = MapUntil.getStatusArray(this.fileId,this.totalFileIndex);
        if(statusArray == null){
            return 1;
        }
        for(int i = 0 ; i< statusArray.length ;i++){
            if(statusArray[i] == '0'){
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
            return this.fileIndex+1;
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
            MapUntil.setSubFileStatus(this.fileId, this.fileIndex-1, this.totalFileIndex);
//            this.statusArray[this.fileIndex -1] = '1';
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
        char[] statusArray = MapUntil.getStatusArray(this.fileId,this.totalFileIndex);
        if(this.fileIndex <= this.totalFileIndex && statusArray[this.fileIndex -1] == '1'){
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
        char[] statusArray = MapUntil.getStatusArray(this.fileId,this.totalFileIndex);
        return !ArrayUtils.contains(statusArray, '0');
    }

    /**
     * 获取分片文件缓存状态中，状态为1 的数量（传输完成的数量）
     * @param
     * @return
     */
    public int  getFileStatusNum(){
      char [] arrays = MapUntil.getStatusArray(this.fileId,this.totalFileIndex);
        int count = 0;
        if (arrays == null || arrays.length == 0) {
            return count;
        }
        for (int i = 0; i < arrays.length; i++) {
            if(arrays[i] == '1'){
                count++;
            }
        }
        return count;
    }
}