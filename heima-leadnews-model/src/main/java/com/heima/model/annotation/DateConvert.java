package com.heima.model.annotation;

import java.lang.annotation.*;

/**
 * 日期类型转换注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DateConvert {
    String value() default "";
}
