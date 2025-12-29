package com.shiyi.strategy;

import com.qiniu.storage.model.FileListing;
import com.shiyi.common.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传策略
 */
public interface FileUploadStrategy {

    String fileUpload(MultipartFile file,String suffix);

    ResponseResult fileList(int pageNo, int pageSize);

    Boolean deleteFile(String ...fileName);
}
