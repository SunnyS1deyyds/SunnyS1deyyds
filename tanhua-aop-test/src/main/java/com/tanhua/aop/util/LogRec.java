package com.tanhua.aop.util;

import java.lang.annotation.*;

//注解放置的目标位置,METHOD是可注解在方法级别上
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Inherited
@Documented
public @interface LogRec {

    Type type() default Type.LOGIN;  // 操作类型

    String key() default "";  // 路由key

    String note() default "";  // 操作说明

    String abc() default "";//第四个属性

}