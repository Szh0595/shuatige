package com.szh.shuatige.blackfilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

@Slf4j
public class BlackIpUtils {

    private static BitMapBloomFilter bloomFilter;

    // 判断 ip 是否在黑名单内
    public static boolean isBlackIp(String ip) {
        return bloomFilter != null && bloomFilter.contains(ip);
    }

    // 重建 ip 黑名单
    public static void rebuildBlackIp(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }
        try {
            // 解析 yaml 文件
            Yaml yaml = new Yaml();
            Map map = yaml.loadAs(configInfo, Map.class);
            // 获取 ip 黑名单
            List<String> blackIpList = (List<String>) map.get("blackIpList");
            // 加锁防止并发
            synchronized (BlackIpUtils.class) {
                if (CollectionUtil.isNotEmpty(blackIpList)) {
                    // 确保容量为正数，避免 NegativeArraySizeException
                    int capacity = Math.max(blackIpList.size() * 2, 1000);
                    // 注意构造参数的设置
                    BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(capacity);
                    for (String ip : blackIpList) {
                        if (StrUtil.isNotBlank(ip)) { // 添加IP非空校验
                            bitMapBloomFilter.add(ip);
                        }
                    }
                    bloomFilter = bitMapBloomFilter;
                } else {
                    // 使用合理的默认容量
                    bloomFilter = new BitMapBloomFilter(1000);
                }
            }
        } catch (Exception e) {
            log.error("解析黑名单配置失败，使用默认配置", e);
            synchronized (BlackIpUtils.class) {
                // 出现异常时使用默认配置
                bloomFilter = new BitMapBloomFilter(1000);
            }
        }
    }
}
