package com.heima.model.behavior.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heima.model.annotation.IdEncrypt;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
public class ApLikesBehavior {
    private Long id;
    @IdEncrypt
    private Integer behaviorEntryId;
    @IdEncrypt
    private Integer entryId;
    private Short type;
    private Short operation;
    private Date createdTime;
    @JsonIgnore
    private String burst;
    // 定义点赞内容的类型
    @Alias("ApLikesBehaviorEnumType")
    public enum Type{
        ARTICLE((short)0),DYNAMIC((short)1),COMMENT((short)2);
        short code;
        Type(short code){
            this.code = code;
        }
        public short getCode(){
            return this.code;
        }
    }
    //定义点赞操作的方式，点赞还是取消点赞
    @Alias("ApLikesBehaviorEnumOperation")
    public enum Operation{
        LIKE((short)0),CANCEL((short)1);
        short code;
        Operation(short code){
            this.code = code;
        }
        public short getCode(){
            return this.code;
        }
    }

}