package com.z.framework.monitor.web.dto;

import com.z.framework.monitor.web.util.NumberUtil;
import com.z.framework.monitor.web.vo.FieldVO;
import com.z.framework.monitor.web.vo.ServerVO;
import lombok.Data;
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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: Server
 * @Package com/example/domain/Server.java
 * @Description: 服务器信息
 * @date 2022/6/30 下午10:20
 */
public class Server {

    private static final int OSHI_WAIT_SECOND = 1000;

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

    public Cpu getCpu() {
        return cpu;
    }

    public Mem getMem() {
        return mem;
    }

    public Jvm getJvm() {
        return jvm;
    }

    public Sys getSys() {
        return sys;
    }

    public List<SysFile> getSysFiles() {
        return sysFiles;
    }

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
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(totalCpu);
        cpu.setSys(cSys);
        cpu.setUsed(user);
        cpu.setWait(iowait);
        cpu.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
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
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
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
            long used = total - free;
            SysFile sysFile = new SysFile();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(convertFileSize(total));
            sysFile.setFree(convertFileSize(free));
            sysFile.setUsed(convertFileSize(used));
            sysFile.setUsage(NumberUtil.mul(NumberUtil.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
    }

    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
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

    static class Mem {

        /**
         * 内存总量
         */
        private double total;

        /**
         * 已用内存
         */
        private double used;

        /**
         * 剩余内存
         */
        private double free;

        public double getTotal() {
            return NumberUtil.div(total, (1024 * 1024 * 1024), 2);
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public double getUsed() {
            return NumberUtil.div(used, (1024 * 1024 * 1024), 2);
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public double getFree() {
            return NumberUtil.div(free, (1024 * 1024 * 1024), 2);
        }

        public void setFree(long free) {
            this.free = free;
        }

        public double getUsage() {
            return NumberUtil.mul(NumberUtil.div(used, total, 4), 100);
        }
    }

    static class Jvm {

        /**
         * 当前JVM占用的内存总数(M)
         */
        private double total;

        /**
         * JVM最大可用内存总数(M)
         */
        private double max;

        /**
         * JVM空闲内存(M)
         */
        private double free;

        /**
         * JDK版本
         */
        private String version;

        /**
         * JDK路径
         */
        private String home;

        public double getTotal() {
            return NumberUtil.div(total, (1024 * 1024), 2);
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getMax() {
            return NumberUtil.div(max, (1024 * 1024), 2);
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getFree() {
            return NumberUtil.div(free, (1024 * 1024), 2);
        }

        public void setFree(double free) {
            this.free = free;
        }

        public double getUsed() {
            return NumberUtil.div(total - free, (1024 * 1024), 2);
        }

        public double getUsage() {
            return NumberUtil.mul(NumberUtil.div(total - free, total, 4), 100);
        }

        /**
         * 获取JDK名称
         */
        public String getName() {
            return ManagementFactory.getRuntimeMXBean().getVmName();
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
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

    static class Cpu {

        /**
         * 核心数
         */
        private int cpuNum;

        /**
         * CPU总的使用率
         */
        private double total;

        /**
         * CPU系统使用率
         */
        private double sys;

        /**
         * CPU用户使用率
         */
        private double used;

        /**
         * CPU当前等待率
         */
        private double wait;

        /**
         * CPU当前空闲率
         */
        private double free;

        public int getCpuNum() {
            return cpuNum;
        }

        public void setCpuNum(int cpuNum) {
            this.cpuNum = cpuNum;
        }

        public BigDecimal getTotal() {
            return NumberUtil.round(NumberUtil.mul(total, 100), 2);
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public BigDecimal getSys() {
            return NumberUtil.round(NumberUtil.mul(sys / total, 100), 2);
        }

        public void setSys(double sys) {
            this.sys = sys;
        }

        public BigDecimal getUsed() {
            return NumberUtil.round(NumberUtil.mul(used / total, 100), 2);
        }

        public void setUsed(double used) {
            this.used = used;
        }

        public BigDecimal getWait() {
            return NumberUtil.round(NumberUtil.mul(wait / total, 100), 2);
        }

        public void setWait(double wait) {
            this.wait = wait;
        }

        public BigDecimal getFree() {
            return NumberUtil.round(NumberUtil.mul(free / total, 100), 2);
        }

        public void setFree(double free) {
            this.free = free;
        }
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
