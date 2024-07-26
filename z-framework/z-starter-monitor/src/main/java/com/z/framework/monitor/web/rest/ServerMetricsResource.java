package com.z.framework.monitor.web.rest;

import com.z.framework.monitor.web.vo.Server;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: ServerMetricsResource
 * @Package com/example/web/rest/ServerMetricsResource.java
 * @Description: 服务监控信息
 * @date 2022/6/30 下午10:18
 */
@RestController
@RequestMapping("/api")
public class ServerMetricsResource {

    @GetMapping("/server/metrics")
    public ResponseEntity<Server> metrics() throws Exception {
        final Server server = new Server();
        server.init();
        return ResponseEntity.ok().body(server);
    }
}
