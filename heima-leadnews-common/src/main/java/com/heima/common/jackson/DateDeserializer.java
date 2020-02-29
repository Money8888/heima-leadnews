package com.heima.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Object> {

    JsonDeserializer<Object>  deserializer = null;

    public DateDeserializer(JsonDeserializer<Object> deserializer){
        this.deserializer = deserializer;
    }

    @Override
    public  Object deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException{
        try {
            String tmp = ""+p.getValueAsLong();
            if(tmp.length()==13) {
                return new Date(p.getValueAsLong());
            }else if(tmp.length()==10) {
                return new Date(p.getValueAsLong()*1000);
            }else
                return null;
        }catch (Exception e){
            if(deserializer!=null){
                return deserializer.deserialize(p,ctxt);
            }else {
                return p.getCurrentValue();
            }
        }
    }
}
