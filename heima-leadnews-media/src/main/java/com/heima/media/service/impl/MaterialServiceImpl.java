package com.heima.media.service.impl;

import com.heima.common.fastdfs.FastDfsClient;
import com.heima.media.service.MaterialService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.wemedia.WmMaterialMapper;
import com.heima.model.mappers.wemedia.WmNewsMaterialMapper;
import com.heima.model.media.dtos.WmMaterialDto;
import com.heima.model.media.dtos.WmMaterialListDto;
import com.heima.model.media.pojos.WmMaterial;
import com.heima.model.media.pojos.WmUser;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings("all")
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private FastDfsClient fastDfsClient;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 获取当前登录用户
        WmUser user = WmThreadLocalUtils.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 验证参数
        if (multipartFile == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        // 获取文件后缀名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if(!extName.matches("(gif|png|jpg|jpeg)")){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);
        }

        // 上传图片获取文件id
        String fileId = null;
        try {
            fileId = fastDfsClient.uploadFile(multipartFile.getBytes(), extName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("user {} upload file {} to fastDFS error, error info:n", user.getId(), originalFilename, e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        // 上传成功保存资源到数据库
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setType((short) 0);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setUrl(fileId);
        wmMaterial.setUserId(user.getId());
        // 插入数据库时只用保存资源路径不用带服务器地址
        wmMaterialMapper.insert(wmMaterial);
        wmMaterial.setUrl(fileServerUrl + fileId);

        return ResponseResult.okResult(wmMaterial);
    }


    @Override
    public ResponseResult delPicture(WmMaterialDto dto) {
        WmUser user = WmThreadLocalUtils.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        if(dto ==null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 判断图片是否被引用
        int count = wmNewsMaterialMapper.countByMid(dto.getId());
        if(count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前图片被引用");
        }

        // 删除fastdfs上面的图片
        WmMaterial wmMaterial = wmMaterialMapper.selectByPrimaryKey(dto.getId());
        if (wmMaterial == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String fileId = wmMaterial.getUrl().replace(fileServerUrl, "");
        try {
            fastDfsClient.delFile(fileId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("user {} delete file {} from fastDFS error, error info:n", user.getId(), fileId, e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        // 删除数据库中的数据
        wmMaterialMapper.deleteByPrimaryKey(dto.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult findList(WmMaterialListDto dto) {
        // 验证参数
        dto.checkParam();
        // 获取用户id
        Long userId = WmThreadLocalUtils.getUser().getId();
        // 查询图片信息
        List<WmMaterial> datas = wmMaterialMapper.findListByUidAndStatus(dto, userId);
        datas = datas.stream().map((item) -> {
            item.setUrl(fileServerUrl + item.getUrl());
            return item;
        }).collect(Collectors.toList());

        int total = wmMaterialMapper.countListByUidAndStatus(dto, userId);
        Map<String, Object> resDatas = new HashMap<>();
        resDatas.put("curPage", dto.getPage());
        resDatas.put("size", dto.getSize());
        resDatas.put("list", datas);
        resDatas.put("total", total);

        return ResponseResult.okResult(resDatas);
    }

    @Override
    public ResponseResult changeUserMaterialStatus(WmMaterialDto dto, Short type) {
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmUser user = WmThreadLocalUtils.getUser();
        wmMaterialMapper.updateStatusByUidAndId(dto.getId(), user.getId(), type);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
