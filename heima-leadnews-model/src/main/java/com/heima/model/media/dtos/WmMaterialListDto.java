package com.heima.model.media.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class WmMaterialListDto extends PageRequestDto {
    Short isCollected; //1 查询收藏的
}
