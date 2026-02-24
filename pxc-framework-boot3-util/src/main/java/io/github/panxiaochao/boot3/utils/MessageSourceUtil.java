package io.github.panxiaochao.boot3.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * <p>
 * 获取国际化语言 i18n 文件
 * </p>
 *
 * @author lypxc
 * @since 2026-02-24
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageSourceUtil {

    private static final MessageSource MESSAGE_SOURCE = SpringContextUtil.getBean(MessageSource.class);

    /**
     * 根据消息键和参数获取翻译值
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        try {
            if (ObjectUtil.isNotEmpty(MESSAGE_SOURCE)) {
                return MESSAGE_SOURCE.getMessage(code, args, LocaleContextHolder.getLocale());
            }
            return StrUtil.EMPTY;
        }
        catch (NoSuchMessageException e) {
            return code;
        }
    }

}
