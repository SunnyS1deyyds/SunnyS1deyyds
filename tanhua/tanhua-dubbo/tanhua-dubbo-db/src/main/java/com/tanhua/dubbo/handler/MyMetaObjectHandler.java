package com.tanhua.dubbo.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //有可能用户自己设置创建时间和更新时间，最好先判断一下

        //created
        Object created = getFieldValByName("created", metaObject);
        if (created == null) {
            //如果created为空，设置时间
            setFieldValByName("created", new Date(), metaObject);
        }

        //updated
        Object updated = getFieldValByName("updated", metaObject);
        if (updated == null) {
            //如果updated为空，设置时间
            setFieldValByName("updated", new Date(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //如果是更新操作，不做判断，直接设置最新的时间
        setFieldValByName("updated", new Date(), metaObject);
    }
}
