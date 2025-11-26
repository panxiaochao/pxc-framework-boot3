/*
 * Copyright © 2025-2026 Lypxc (545685602@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.panxiaochao.core.utils;

import io.github.panxiaochao.core.utils.sysinfo.Cpu;
import io.github.panxiaochao.core.utils.sysinfo.DiskInfo;
import io.github.panxiaochao.core.utils.sysinfo.Jvm;
import io.github.panxiaochao.core.utils.sysinfo.Mem;
import io.github.panxiaochao.core.utils.sysinfo.ServerInfo;
import io.github.panxiaochao.core.utils.sysinfo.SysInfo;
import io.github.panxiaochao.core.utils.unit.DataOfSize;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.GlobalConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统服务器相关信息(CPU, 内存, JVM, 硬盘等) 工具类.
 * </p>
 *
 * @author Lypxc
 * @since 2023-07-07
 */
public class SystemServerUtil {

    /**
     * 硬件信息
     */
    private static final HardwareAbstractionLayer HAL;

    /**
     * 系统信息
     */
    private static final OperatingSystem OS;

    static {
        SystemInfo systemInfo = new SystemInfo();
        HAL = systemInfo.getHardware();
        OS = systemInfo.getOperatingSystem();
    }

    private SystemServerUtil() {
    }

    /**
     * 获取系统服务器信息，包括CPU, 内存, JVM, 硬盘等等信息
     * @return ServerInfo
     */
    public static ServerInfo getServerInfo() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setCpu(ofCpuInfo());
        serverInfo.setMem(ofMemInfo());
        serverInfo.setJvm(ofJvmInfo());
        serverInfo.setSys(ofSysInfo());
        serverInfo.setDiskInfo(ofDiskInfo());
        serverInfo.setDiskInfos(ofDiskInfos());
        return serverInfo;
    }

    /**
     * 获取CPU信息
     * @return Cpu
     */
    public static Cpu ofCpuInfo() {
        // Oshi 返回的值和Windows任务管理器显示的值一致
        GlobalConfig.set(GlobalConfig.OSHI_OS_WINDOWS_CPU_UTILITY, true);
        CentralProcessor processor = HAL.getProcessor();
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + cSys + idle + ioWait + irq + softIrq + steal;
        // 能效核心数
        Cpu cpu = new Cpu();
        cpu.setCpuName(processor.getProcessorIdentifier().getName());
        cpu.setPhysicalPackageCount(processor.getPhysicalPackageCount());
        cpu.setPhysicalProcessorCount(processor.getPhysicalProcessorCount());
        cpu.setLogicalProcessorCount(processor.getLogicalProcessorCount());
        cpu.setVendor(processor.getProcessorIdentifier().getVendor());
        cpu.setTotal(totalCpu);
        cpu.setSys(cSys);
        cpu.setUser(user);
        cpu.setWait(ioWait);
        cpu.setFree(idle);
        if (cpu.getCpuName().contains("Apple")) {
            try {
                // macOS 原生支持直接获取
                int pCores = runSysctlCommand("sysctl -n hw.perflevel0.physicalcpu");
                int eCores = runSysctlCommand("sysctl -n hw.perflevel1.physicalcpu");
                cpu.setEfficiencyCount(eCores);
                cpu.setPerformanceCount(pCores);
            }
            catch (Exception e) {
                cpu.setEfficiencyCount(-1);
                cpu.setPerformanceCount(-1);
            }
        }
        else {
            long efficiencyCount = processor.getPhysicalProcessors()
                .stream()
                .filter(s -> s.getEfficiency() == 1)
                .count();
            cpu.setEfficiencyCount((int) efficiencyCount);
            cpu.setPerformanceCount(cpu.getPhysicalProcessorCount() - cpu.getEfficiencyCount());
        }
        return cpu;
    }

    /**
     * 获取内存信息
     * @return Mem
     */
    public static Mem ofMemInfo() {
        GlobalMemory memory = HAL.getMemory();
        memory.getVirtualMemory();
        // 内存信息
        Mem mem = new Mem();
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
        return mem;
    }

    /**
     * 获取JVM信息
     * @return Jvm
     */
    public static Jvm ofJvmInfo() {
        Jvm jvm = new Jvm();
        Properties props = System.getProperties();
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
        jvm.setJavaVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
        jvm.setJvmVersion(props.getProperty("java.vm.version"));
        jvm.setVendor(props.getProperty("java.vendor"));
        return jvm;
    }

    /**
     * 获取系统信息
     * @return SysInfo
     */
    public static SysInfo ofSysInfo() {
        NetworkParams networkParams = OS.getNetworkParams();
        List<NetworkIF> networkIFs = HAL.getNetworkIFs();
        List<String> ipv4s = networkIFs.stream()
            .map(NetworkIF::getIPv4addr)
            .filter(ArrayUtil::isNotEmpty)
            .map(s -> String.join(StringPools.COMMA, s))
            .collect(Collectors.toList());
        SysInfo sys = new SysInfo();
        Properties props = System.getProperties();
        sys.setComputerName(networkParams.getHostName());
        sys.setComputerIp(String.join(StringPools.COMMA, ipv4s));
        sys.setDns(Arrays.toString(networkParams.getDnsServers()));
        sys.setIpv4Gateway(networkParams.getIpv4DefaultGateway());
        sys.setIpv6Gateway(networkParams.getIpv6DefaultGateway());
        sys.setOsName(props.getProperty("os.name") + " " + props.getProperty("os.version"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
        return sys;
    }

    /**
     * 获取磁盘文件存储信息
     * @return DiskInfo
     */
    public static List<DiskInfo> ofDiskInfos() {
        FileSystem fileSystem = OS.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        List<DiskInfo> diskInfos = new ArrayList<>();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setDirName(fs.getMount());
            diskInfo.setSysTypeName(fs.getType());
            diskInfo.setTypeName(fs.getName());
            diskInfo.setTotal(DataOfSize.ofBytes(total).toGigabytes());
            diskInfo.setFree(DataOfSize.ofBytes(free).toGigabytes());
            diskInfo.setUsed(DataOfSize.ofBytes(used).toGigabytes());
            diskInfo.setUsage(ArithmeticUtil.mul(ArithmeticUtil.div(used, total, 4), 100));
            diskInfos.add(diskInfo);
        }
        return diskInfos;
    }

    /**
     * 磁盘总体存储详情
     * @return DiskInfo
     */
    public static DiskInfo ofDiskInfo() {
        // 获取磁盘总体详情
        AtomicLong storageTotal = new AtomicLong(0);
        AtomicLong storageUsed = new AtomicLong(0);
        AtomicLong storageFree = new AtomicLong(0);
        File[] files = File.listRoots();
        for (File file : files) {
            long totalSpace = file.getTotalSpace();
            long usableSpace = file.getUsableSpace();
            long freeSpace = file.getFreeSpace();
            storageTotal.addAndGet(DataOfSize.ofBytes(totalSpace).toGigabytes());
            storageUsed.addAndGet(DataOfSize.ofBytes(usableSpace).toGigabytes());
            storageFree.addAndGet(DataOfSize.ofBytes(freeSpace).toGigabytes());
        }
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setTotal(storageTotal.get());
        diskInfo.setFree(storageFree.get());
        diskInfo.setUsed(storageUsed.get());
        diskInfo.setUsage(ArithmeticUtil.mul(ArithmeticUtil.div(diskInfo.getUsed(), diskInfo.getTotal(), 4), 100));
        return diskInfo;
    }

    /**
     * 获取网络上传下载 单位Kb/s
     * @return 上传速度和下载速度
     */
    public static Map<String, Object> ofNetworkInterfaces() {
        Map<String, Object> networkInterfaces = new HashMap<>();
        List<NetworkIF> networkIFs = HAL.getNetworkIFs();
        for (NetworkIF networkIF : networkIFs) {
            if (ArrayUtil.isNotEmpty(networkIF.getIPv4addr())) {
                long rxBytes = networkIF.getBytesRecv();
                long txBytes = networkIF.getBytesSent();
                try {
                    Thread.sleep(3000);
                }
                catch (Exception e) {
                    // Ignore
                }
                networkIF.updateAttributes();
                long rxBytes1 = networkIF.getBytesRecv();
                long txBytes1 = networkIF.getBytesSent();
                long rxSpeed = DataOfSize.ofBytes(rxBytes1 - rxBytes).toKilobytes();
                long txSpeed = DataOfSize.ofBytes(txBytes1 - txBytes).toKilobytes();

                networkInterfaces.put("displayName", networkIF.getDisplayName());
                networkInterfaces.put("download", rxSpeed);
                networkInterfaces.put("up", txSpeed);
            }
        }
        return networkInterfaces;
    }

    private static int runSysctlCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String result = reader.readLine().trim();
            return Integer.parseInt(result);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        // System.out.println(SystemServerUtil.INSTANCE().ofSysInfo());
        // Properties props = System.getProperties();
        // 遍历所有的属性
        // for (String key : props.stringPropertyNames()) {
        // // 输出对应的键和值
        // System.out.println(key + " = " + props.getProperty(key));
        // }

        // long memoryUsed = 0;
        // for (MemoryPoolMXBean memoryPoolBean :
        // ManagementFactory.getPlatformMXBeans(MemoryPoolMXBean.class)) {
        // memoryUsed += memoryPoolBean.getUsage().getUsed();
        // }
        // System.out.println(DataOfSize.ofBytes(memoryUsed).toMegabytes());
        //
        // System.out.println("打印Java内存系统信息-----------");
        // MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        // System.out.println("测试是否启用了内存系统的详细输出:" + memoryMXBean.isVerbose());
        // System.out.println("返回正在等待完成的对象的大致数量:" +
        // memoryMXBean.getObjectPendingFinalizationCount());
        // System.out.println("返回用于对象分配的堆的当前内存使用情况:" + memoryMXBean.getHeapMemoryUsage());
        // System.out.println("返回Java虚拟机使用的非堆内存的当前内存使用情况:" +
        // memoryMXBean.getNonHeapMemoryUsage());
    }

}
