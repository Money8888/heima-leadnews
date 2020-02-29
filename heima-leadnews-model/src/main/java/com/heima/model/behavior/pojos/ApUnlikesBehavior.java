package com.heima.model.behavior.pojos;

import com.heima.model.annotation.IdEncrypt;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
public class ApUnlikesBehavior {
    private Long id;
    @IdEncrypt
    private Integer entryId;
    @IdEncrypt
    private Integer articleId;
    private Short type;
    private Date createdTime;
    // 定义不喜欢操作的类型
    @Alias("ApUnlikesBehaviorEnumType")
    public enum Type{
        UNLIKE((short)0),CANCEL((short)1);
        short code;
        Type(short code){
            this.code = code;
        }
        public short getCode(){
            return this.code;
        }
    }
}