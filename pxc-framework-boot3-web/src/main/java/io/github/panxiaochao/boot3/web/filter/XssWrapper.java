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
package io.github.panxiaochao.boot3.web.filter;

import io.github.panxiaochao.boot3.core.utils.ArrayUtil;
import io.github.panxiaochao.boot3.core.utils.StrUtil;
import io.github.panxiaochao.boot3.core.utils.XssUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * XSS Request Wrapper
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-03
 * @version 1.0
 */
public class XssWrapper extends HttpServletRequestWrapper {

    public XssWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 对参数中特殊字符进行过滤
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return filterXss(value);
    }

    /**
     * 对参数数组进行特殊字符过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] parameters = super.getParameterValues(name);
        if (!ArrayUtil.isEmpty(parameters)) {
            String[] encodedValues = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                encodedValues[i] = filterXss(parameters[i]);
            }
            return encodedValues;
        }
        return parameters;
    }

    /**
     * 获取attribute,特殊字符过滤
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String && StrUtil.isNotBlank(value.toString())) {
            return filterXss(value.toString());
        }
        return value;
    }

    /**
     * 对参数map进行特殊字符过滤
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = filterXss(values[i]);
                }
                map.put(key, values);
            }
        }
        return map;
    }

    /**
     * 对请求头部进行特殊字符过滤
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return filterXss(value);
    }

    /**
     * 对请求内容进行Xss过滤
     * @param content 请求内容
     * @return 过滤后的内容
     */
    private String filterXss(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        return XssUtil.filter(content);
    }

}
