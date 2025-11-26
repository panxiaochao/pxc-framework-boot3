package io.github.panxiaochao.util.test;

import io.github.panxiaochao.core.utils.RegexUtil;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

/**
 * <p>
 * </p>
 *
 * @author Lypxc
 * @since 2024-10-14
 * @version 1.0
 */
public class RegexUtilTest {

    @Test
    void getAllGroupNames() {
        final Pattern pattern = Pattern.compile("(?<year>\\d+)-(?<month>\\d+)-(?<day>\\d+)", Pattern.DOTALL);
        System.out.println(RegexUtil.getAllGroupNames(pattern, "2021-10-11"));
    }

}
