package com.z.framework.monitor.web.rest;

import com.z.framework.monitor.web.dto.Server;
import com.z.framework.monitor.web.vo.ServerVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ServerMetricsResource {

    @GetMapping("/server/metrics")
    public ResponseEntity<ServerVO> metrics() throws Exception {
        final Server server = new Server();
        server.init();
        // Server信息转换为ServerVO形式
        final ServerVO serverVO = server.convert();
        return ResponseEntity.ok().body(serverVO);
    }
}
