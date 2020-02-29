package com.heima.model.media.dtos;

import com.heima.model.annotation.IdEncrypt;
import lombok.Data;

@Data
public class CommentReplytDto {

    @IdEncrypt
    private Integer commentId;
    private String content;

}
