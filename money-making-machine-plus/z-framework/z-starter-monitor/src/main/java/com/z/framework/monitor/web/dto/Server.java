package com.z.framework.monitor.web.dto;

import com.z.framework.monitor.web.util.NumberUtil;
import com.z.framework.monitor.web.vo.FieldVO;
import com.z.framework.monitor.web.vo.ServerVO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: Server
 * @Package com/example/domain/Server.java
 * @Description: 服务器信息
 * @date 2022/6/30 下午10:20
 */
@Slf4j
public class Server {

    /**
     * CPU相关信息
     */
    private final Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private final Mem mem = new Mem();

    /**
     * JVM相关信息
     */
    private final Jvm jvm = new Jvm();

    /**
     * 服务器相关信息
     */
    private final Sys sys = new Sys();

    /**
     * 磁盘相关信息
     */
    private final List<SysFile> sysFiles = new LinkedList<>();

    public void init() throws Exception {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        setCpuInfo(hardwareAbstractionLayer.getProcessor());
        setMemInfo(hardwareAbstractionLayer.getMemory());
        setSysInfo();
        setJvmInfo();
        setSysFiles(systemInfo.getOperatingSystem());
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor) {
        // 常量声明提升可读性
        final int WAIT_SECONDS = 1; // 采样间隔时间

        try {
            // 第一次采样CPU时间片计数
            long[] prevTicks = processor.getSystemCpuLoadTicks();

            // 等待采样间隔（增加中断处理）
            TimeUnit.SECONDS.sleep(WAIT_SECONDS);

            // 第二次采样CPU时间片计数
            long[] ticks = processor.getSystemCpuLoadTicks();

            // 设置逻辑核心数
            cpu.setCpuNum(processor.getLogicalProcessorCount());
            long totalCpuTicks = 0;
            Map<TickType, Long> tickDeltas = new EnumMap<>(TickType.class);

            // 遍历所有CPU状态类型[1](@ref)
            for (TickType tickType : TickType.values()) {
                // 计算两次采样的时间片差值
                long delta = ticks[tickType.getIndex()] - prevTicks[tickType.getIndex()];
                tickDeltas.put(tickType, delta);
                totalCpuTicks += delta;
            }

            // 有效性校验（避免除零错误）
            if (totalCpuTicks <= 0) {
                throw new IllegalStateException("无效的CPU时间片数据");
            }

            // 计算各状态占比（统一格式化处理）
            Long finalTotalCpuTicks = totalCpuTicks;
            Function<Long, String> formatPercentage = delta ->
                    NumberUtil.round(NumberUtil.mul(NumberUtil.div(delta, finalTotalCpuTicks), 100), 2).toString();

            // 设置各状态百分比[2,8](@ref)
            cpu.setTotal(formatPercentage.apply(totalCpuTicks)); // 总利用率
            cpu.setSys(formatPercentage.apply(tickDeltas.get(TickType.SYSTEM)));
            cpu.setUsed(formatPercentage.apply(tickDeltas.get(TickType.USER)));
            cpu.setWait(formatPercentage.apply(tickDeltas.get(TickType.IOWAIT)));
            cpu.setFree(formatPercentage.apply(tickDeltas.get(TickType.IDLE)));
            cpu.setIrq(formatPercentage.apply(tickDeltas.get(TickType.IRQ)));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CPU信息采集被中断", e);
        } catch (Exception e) {
            log.error("获取CPU信息异常", e);
        }
    }


    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        mem.setTotal(convertSize(memory.getTotal()));
        mem.setUsed(convertSize(memory.getTotal() - memory.getAvailable()));
        mem.setFree(convertSize(memory.getAvailable()));
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo() {
        Properties props = System.getProperties();
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            sys.setComputerName(localHost.getHostName());
            sys.setComputerIp(localHost.getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置Java虚拟机
     */
    private void setJvmInfo() {
        Properties props = System.getProperties();
        jvm.setTotal(convertSize(Runtime.getRuntime().totalMemory()));
        jvm.setMax(convertSize(Runtime.getRuntime().maxMemory()));
        jvm.setFree(convertSize(Runtime.getRuntime().freeMemory()));
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            if(total == 0){
                continue;
            }
            long used = total - free;
            SysFile sysFile = new SysFile();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(convertSize(total));
            sysFile.setFree(convertSize(free));
            sysFile.setUsed(convertSize(used));
            sysFile.setUsage(NumberUtil.mul(NumberUtil.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
    }

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final int FORMAT_THRESHOLD = 100;
    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertSize(long size) {
        if (size < 0) throw new IllegalArgumentException("Size cannot be negative");

        if (size >= GB) {
            return String.format("%.1f GB", (double) size / GB);
        } else if (size >= MB) {
            double value = (double) size / MB;
            return String.format(value > FORMAT_THRESHOLD ? "%.0f MB ":"%.1f MB", value);
        } else if (size >= KB) {
            double value = (double) size / KB;
            return String.format(value > FORMAT_THRESHOLD ? "%.0f KB" : "%.1f KB", value);
        }else{
            return size + " B";
        }
    }

    public ServerVO convert() {
        final ServerVO serverVO = new ServerVO();
        serverVO.setCpu(Arrays.stream(cpu.getClass().getDeclaredFields()).map(field -> {
            try {
                field.setAccessible(true);
                return new FieldVO(field.getName(), "" + field.get(cpu));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        serverVO.setSys(Arrays.stream(sys.getClass().getDeclaredFields()).map(field -> {
            try {
                field.setAccessible(true);
                return new FieldVO(field.getName(), "" + field.get(sys));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        serverVO.setMem(Arrays.stream(mem.getClass().getDeclaredFields()).map(field -> {
            try {
                field.setAccessible(true);
                return new FieldVO(field.getName(), "" + field.get(mem));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        serverVO.setJvm(Arrays.stream(jvm.getClass().getDeclaredFields()).map(field -> {
            try {
                field.setAccessible(true);
                return new FieldVO(field.getName(), "" + field.get(jvm));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        serverVO.setSysFiles(sysFiles);
        return serverVO;
    }

    @Data
    static class Sys {

        /**
         * 服务器名称
         */
        private String computerName;

        /**
         * 服务器Ip
         */
        private String computerIp;

        /**
         * 项目路径
         */
        private String userDir;

        /**
         * 操作系统
         */
        private String osName;

        /**
         * 系统架构
         */
        private String osArch;
    }

    @Setter
    static class Mem {

        /**
         * 内存总量
         */
        private String total;

        /**
         * 已用内存
         */
        private String used;

        /**
         * 剩余内存
         */
        private String free;

    }

    @Setter
    static class Jvm {

        /**
         * 当前JVM占用的内存总数(M)
         */
        private String total;

        /**
         * JVM最大可用内存总数(M)
         */
        private String max;

        /**
         * JVM空闲内存(M)
         */
        private String free;

        /**
         * JDK版本
         */
        private String version;

        /**
         * JDK路径
         */
        private String home;

        /**
         * 获取JDK名称
         */
        public String getName() {
            return ManagementFactory.getRuntimeMXBean().getVmName();
        }

        /**
         * JDK启动时间
         */
        public String getStartTime() {
            long serverStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            ZoneId zone = ZoneId.systemDefault();
            final LocalDateTime serverStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(serverStartTime), zone);
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(serverStart);
        }

        /**
         * JDK运行时间
         */
        public String getRunTime() {
            long serverStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            // 获得两个时间的毫秒时间差异
            final LocalDateTime now = LocalDateTime.now();
            ZoneId zone = ZoneId.systemDefault();
            final LocalDateTime serverStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(serverStartTime), zone);
            Duration duration = Duration.between(serverStart, now);
            //相差的天数
            long days = duration.toDays();
            //相差的小时数
            long hours = duration.toHours();
            //相差的分钟数
            long minutes = duration.toMinutes();
            //相差毫秒数
            long millis = duration.toMillis();
            // 相差的纳秒数
            // long nanos = duration.toNanos();
            return days + "天" + hours + "小时" + minutes + "分钟";
        }

        /**
         * 运行参数
         */
        public String getInputArgs() {
            return ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        }
    }

    @Setter
    static class Cpu {

        /**
         * 核心数
         */
        private int cpuNum;

        /**
         * CPU总的使用率
         */
        private String total;

        /**
         * CPU系统使用率
         */
        private String sys;

        /**
         * CPU用户使用率
         */
        private String used;

        /**
         * CPU当前等待率
         */
        private String wait;

        /**
         * CPU当前空闲率
         */
        private String free;

        private String nice;

        private String irq;

    }

    public static class SysFile {

        /**
         * 盘符路径
         */
        private String dirName;

        /**
         * 盘符类型
         */
        private String sysTypeName;

        /**
         * 文件类型
         */
        private String typeName;

        /**
         * 总大小
         */
        private String total;

        /**
         * 剩余大小
         */
        private String free;

        /**
         * 已经使用量
         */
        private String used;

        /**
         * 资源的使用率
         */
        private double usage;

        public String getDirName() {
            return dirName;
        }

        public void setDirName(String dirName) {
            this.dirName = dirName;
        }

        public String getSysTypeName() {
            return sysTypeName;
        }

        public void setSysTypeName(String sysTypeName) {
            this.sysTypeName = sysTypeName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getUsed() {
            return used;
        }

        public void setUsed(String used) {
            this.used = used;
        }

        public double getUsage() {
            return usage;
        }

        public void setUsage(double usage) {
            this.usage = usage;
        }
    }
}
