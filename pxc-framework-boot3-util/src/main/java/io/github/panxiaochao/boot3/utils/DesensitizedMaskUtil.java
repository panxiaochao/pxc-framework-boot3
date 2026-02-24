package io.github.panxiaochao.boot3.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 脱敏掩码工具类
 * </p>
 *
 * @author lypxc
 * @since 2026-02-24
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DesensitizedMaskUtil {

    /**
     * 生成指定长度的掩码字符串
     * @param length 掩码长度
     * @return 掩码字符串
     */
    private static String generateMask(int length) {
        return StrUtil.repeat(CharPools.ASTERISK, Math.max(0, length));
    }

    /**
     * 灵活脱敏方法
     * @param value 原始字符串
     * @param prefixVisible 前面可见长度（非负整数）
     * @param suffixVisible 后面可见长度（非负整数）
     * @param maskLength 中间掩码长度（非负整数，固定显示多少 *，如果总长度不足则自动缩减）
     * @return 脱敏后字符串
     */
    public static String flexibleMask(String value, int prefixVisible, int suffixVisible, int maskLength) {
        // 参数校验
        prefixVisible = Math.max(0, prefixVisible);
        suffixVisible = Math.max(0, suffixVisible);
        maskLength = Math.max(0, maskLength);

        if (StrUtil.isBlank(value)) {
            return value;
        }

        int len = value.length();
        int prefixMaskLimit = prefixVisible + maskLength;
        int fullLimit = prefixMaskLimit + suffixVisible;

        // 规则 1：长度 <= 中间掩码长度 → 全掩码
        if (len <= maskLength) {
            return generateMask(len);
        }

        // 生成中间掩码
        String mask = generateMask(maskLength);

        // 规则 2：长度 <= 前缀 + 中间掩码
        if (len <= prefixMaskLimit) {
            return value.substring(0, len - maskLength) + mask;
        }

        // 确保前缀长度不超过字符串长度
        int safePrefixVisible = Math.min(prefixVisible, len);
        String prefix = value.substring(0, safePrefixVisible);

        // 规则 3：长度 <= 前缀 + 中间掩码 + 后缀
        if (len <= fullLimit) {
            int suffixLen = len - prefixMaskLimit;
            return prefix + mask + value.substring(len - suffixLen);
        }

        // 确保后缀长度不超过字符串长度
        int safeSuffixVisible = Math.min(suffixVisible, len - prefixMaskLimit);

        // 规则 4：标准形态
        return prefix + mask + value.substring(len - safeSuffixVisible);
    }

    /**
     * 高安全级别脱敏方法（Token / 私钥）
     * @param value 原始字符串
     * @param prefixVisible 前面可见长度（推荐0~4，非负整数）
     * @param suffixVisible 后面可见长度（推荐0~4，非负整数）
     * @return 脱敏后字符串
     */
    public static String highSecurityMask(String value, int prefixVisible, int suffixVisible) {
        // 参数校验
        prefixVisible = Math.max(0, prefixVisible);
        suffixVisible = Math.max(0, suffixVisible);

        if (StrUtil.isBlank(value)) {
            return value;
        }

        int len = value.length();

        // 规则1：长度 <= 前缀可见长度 → 全部掩码
        if (len <= prefixVisible) {
            return generateMask(len);
        }

        // 规则2：长度 <= 前缀 + 后缀可见长度 → 优先掩码后面
        if (len <= prefixVisible + suffixVisible) {
            return value.substring(0, len - prefixVisible) + generateMask(prefixVisible);
        }

        // 确保前缀长度不超过字符串长度
        int safePrefixVisible = Math.min(prefixVisible, len);
        // 确保后缀长度不超过字符串长度
        int safeSuffixVisible = Math.min(suffixVisible, len - safePrefixVisible);

        // 规则3：标准形态 → 前后可见，中间全部掩码
        return value.substring(0, safePrefixVisible) + generateMask(len - safePrefixVisible - safeSuffixVisible)
                + value.substring(len - safeSuffixVisible);
    }

}
