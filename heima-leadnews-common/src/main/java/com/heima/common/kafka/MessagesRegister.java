package com.heima.common.kafka;

import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

/**
 * 扫描所有的kafkamessage类
 */
@Log4j2
@Component
public class MessagesRegister implements InitializingBean {

    Map<String,Class> messages = Maps.newConcurrentMap();

    @Override
    public void afterPropertiesSet() throws Exception {
        Reflections reflections = new Reflections("com.heima");
        Set<Class<? extends KafkaMessage>> ms = reflections.getSubTypesOf(KafkaMessage.class);
        if(ms!=null){
            ms.forEach(cla->{
                try {
                    Constructor<?>[] cs = cla.getConstructors();
                    KafkaMessage mess = null;
                    if (cs != null && cs.length > 0) {
                        Class[] temp = cs[0].getParameterTypes();
                        Object[] parms = new Object[temp.length];
                        for (int i = 0; i < temp.length; i++) {
                            if(temp[i].isPrimitive()){
                                if(temp[i].getName().contains("boolean")){
                                    parms[i]=false;
                                }else {
                                    parms[i] = 0;
                                }
                            }else{
                                parms[i]=null;
                            }
                        }
                        mess = (KafkaMessage) cs[0].newInstance(parms);
                    } else {
                        mess = (KafkaMessage) cla.newInstance();
                    }
                    String type = mess.getType();
                    messages.put(type,cla);
                }catch (Exception e){
                    System.out.println(cla+"====================:"+cla.getConstructors()[0].getParameterCount());
                    e.printStackTrace();
                }
            });
        }
        log.info("=================================================");
        log.info("scan kafka message resultt[{}]",messages);
        log.info("=================================================");
    }

    /**
     * 通过消息的类型名称，查找对应的class定义
     * @param type
     * @return
     */
    public Class<? extends KafkaMessage> findClassByType(String type){
        return this.messages.get(type);
    }

}
