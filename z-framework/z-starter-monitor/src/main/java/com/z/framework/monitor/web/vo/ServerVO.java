package com.z.framework.monitor.web.vo;

import com.z.framework.monitor.web.dto.Server;
import lombok.Data;

import java.util.List;

@Data
public class ServerVO {
    /**
     * CPU相关信息
     */
    private List<FieldVO> cpu;

    /**
     * 內存相关信息
     */
    private List<FieldVO> mem;

    /**
     * JVM相关信息
     */
    private List<FieldVO> jvm;

    /**
     * 服务器相关信息
     */
    private List<FieldVO> sys;

    /**
     * 磁盘相关信息
     */
    private List<Server.SysFile> sysFiles;

}
