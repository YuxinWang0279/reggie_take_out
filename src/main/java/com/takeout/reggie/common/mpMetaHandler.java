package com.takeout.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元对象处理器
 */
@Component
@Slf4j
public class mpMetaHandler implements MetaObjectHandler {

    //operation when conduct insert method
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("Insert New Object {}",metaObject.toString());
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        metaObject.setValue("updateTime",LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("update object {}",metaObject.toString());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        metaObject.setValue("updateTime",LocalDateTime.now());
    }
}
