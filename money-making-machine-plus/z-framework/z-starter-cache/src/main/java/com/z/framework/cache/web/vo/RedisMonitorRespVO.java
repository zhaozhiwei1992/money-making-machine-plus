package com.z.framework.cache.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Properties;

@Data
@Builder
@AllArgsConstructor
public class RedisMonitorRespVO {

    private Properties info;

    private Long dbSize;

    private List<CommandStat> commandStats;

    @Data
    @Builder
    @AllArgsConstructor
    public static class CommandStat {

        private String command;

        private Long calls;

        private Long usec;

    }

}
