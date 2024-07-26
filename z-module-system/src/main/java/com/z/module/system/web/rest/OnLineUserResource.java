package com.z.module.system.web.rest;

import com.z.module.system.service.OnLineUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Title: OnLineUserResource
 * @Package com/example/web/rest/OnLineUserResource.java
 * @Description:
 * 获取当前在线用户信息
 * 监听security认证
 * @author zhaozhiwei
 * @date 2022/5/30 下午10:52
 * @version V1.0
 */
@RestController
public class OnLineUserResource {

    private OnLineUserService onLineUserService;

    public OnLineUserResource(OnLineUserService onLineUserService) {
        this.onLineUserService = onLineUserService;
    }

    @GetMapping("/online/users")
    public List<Map<String, Object>> onLineUsers() {
        return onLineUserService.findAll();
    }

    @DeleteMapping("/online/users/{loginName}")
    public ResponseEntity<Object> onLineUserDelete(@PathVariable String loginName) {
        onLineUserService.delete(loginName);
        //        todo 强制用户下线, token失效
        //        获取用户对应token
        //        缓存保留失效token, 认证时直接失败即可
        return ResponseEntity.noContent().build();
    }
}
