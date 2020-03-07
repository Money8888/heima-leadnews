package com.heima.media.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmMaterialDto;
import com.heima.model.media.dtos.WmMaterialListDto;
import org.springframework.web.multipart.MultipartFile;

public interface MaterialManageControllerApi {
    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 删除图片
     * @param wmMaterial
     * @return
     */
    ResponseResult delPicture(WmMaterialDto wmMaterial);

    /**
     * 查询图片列表
     * @param dto
     * @return
     */
    ResponseResult list(WmMaterialListDto dto);

    ResponseResult collectionMaterial(WmMaterialDto dto);
    ResponseResult cancleCollectionMaterial(WmMaterialDto dto);
}
