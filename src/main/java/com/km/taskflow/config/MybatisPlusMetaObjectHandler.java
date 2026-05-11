package com.km.taskflow.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 *
 * @author zzy
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final String DELETED = "deleted";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();

        // 新增时：如果没传创建时间，就填当前时间
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, now);

        // 新增时：updateTime 也填当前时间
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime.class, now);

        // 新增时：deleted 默认 0
        this.strictInsertFill(metaObject, DELETED, Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 修改时：不管原来有没有 updateTime，都强制覆盖为当前时间
        this.setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
    }
}