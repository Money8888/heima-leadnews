package com.heima.model.websocket.dtos;

import com.heima.model.annotation.IdEncrypt;
import lombok.Data;

@Data
public class WebSocketDto {
    // 设备ID
    @IdEncrypt
    Integer equipmentId;
    // 文章ID
    @IdEncrypt
    String token;
}
