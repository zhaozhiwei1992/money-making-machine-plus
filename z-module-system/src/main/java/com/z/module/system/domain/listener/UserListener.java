package com.z.module.system.domain.listener;

import com.z.module.system.domain.User;
import jakarta.persistence.PreRemove;
import org.springframework.stereotype.Component;

/**
 * @Title: null.java
 * @Package: com.z.module.system.domain.listener
 * @Description: 对用户删除做权限控制
 * @author: zhaozhiwei
 * @date: 2024/10/13 16:23
 * @version: V1.0
 */
@Component
public class UserListener {

    @PreRemove
    public void beforeDelete(User user) {
        if(user.getCreatedBy().equals("system")){
            throw new RuntimeException("不允许删除系统创建条目");
        }
    }
}
