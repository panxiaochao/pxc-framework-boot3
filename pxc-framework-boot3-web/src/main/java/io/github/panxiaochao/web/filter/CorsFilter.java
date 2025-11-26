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
package io.github.panxiaochao.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * CorsFilter过滤器
 * </p>
 *
 * @author Lypxc
 * @since 2023-06-26
 */
public class CorsFilter implements Filter {

    /**
     * 当前跨域请求最大有效时长，同一个域名不会再进行检查，默认3600
     */
    private static final String MAX_AGE = "3600";

    /**
     * 允许请求的方法
     */
    private static final List<String> ALLOWED_METHODS = Arrays.asList("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE",
            "PATCH");

    /**
     * 允许被客户端访问的请求头
     */
    private static final List<String> EXPOSE_HEADERS = Arrays.asList("Content-Disposition", "Content-Length",
            "Content-Type", "Cache-Control", "Expires", "Content-Language", "Last-Modified", "Pragma", "Authorization");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 解决跨域的问题
        cors(request, response);
        // 预请求，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        // 放行
        filterChain.doFilter(request, response);
    }

    private void cors(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", String.join(",", ALLOWED_METHODS));
        // 以下是允许前端向后端自定义的 HTTP 请求头, “你可以发送这些自定义的请求头部。”
        response.setHeader("Access-Control-Allow-Headers", "*");
        // 以下是允许后端向前段放出，哪些可以被前段访问的 HTTP 请求头, “你可以读取这些自定义的响应头部。”
        response.setHeader("Access-Control-Expose-Headers", String.join(",", EXPOSE_HEADERS));
        response.setHeader("Access-Control-Max-Age", MAX_AGE);
    }

}
