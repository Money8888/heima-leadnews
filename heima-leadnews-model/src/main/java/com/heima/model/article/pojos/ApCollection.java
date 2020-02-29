package com.heima.model.article.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heima.model.annotation.IdEncrypt;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
public class ApCollection {
    private Long id;
    @IdEncrypt
    private Integer behaviorEntryId;
    @IdEncrypt
    private Integer entryId;
    private Short type;
    private Date collectionTime;
    private Date publishedTime;
    @JsonIgnore
    private String burst;
    // 定义收藏内容类型的枚举
    @Alias("ApCollectionEnumType")
    public enum Type{
        ARTICLE((short)0),DYNAMIC((short)1);
        short code;
        Type(short code){
            this.code = code;
        }
        public short getCode(){
            return this.code;
        }
    }

    public boolean isCollectionArticle(){
        return (this.getType()!=null&&this.getType().equals(Type.ARTICLE));
    }
}