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
package io.github.panxiaochao.dynamic.datasource.filter;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * <p>
 * 清空上文的DS 设置避免污染当前线程
 * </p>
 *
 * @author Lypxc
 * @since 2025-01-03
 * @version 1.0
 */
public class ClearDataSourceFilter extends GenericFilterBean implements Ordered {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        DynamicDataSourceContextHolder.clear();
        filterChain.doFilter(servletRequest, servletResponse);
        DynamicDataSourceContextHolder.clear();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
