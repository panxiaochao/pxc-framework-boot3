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
package io.github.panxiaochao.boot3.core.utils;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * <p>
 * OkHttp3 连接工具类.
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-18
 */
public class OkHttp3Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttp3Util.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");

    private static final MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

    private static final OkHttpClient CLIENT = SpringContextUtil.getBean(OkHttpClient.class);

    private OkHttp3Util() {
    }

    /**
     * get 请求
     * @param url 请求url地址
     * @return string
     */
    public static String doGet(String url) {
        return doGet(url, null, null);
    }

    /**
     * get 请求
     * @param url 请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public static String doGet(String url, Map<String, Object> params) {
        return doGet(url, params, null);
    }

    /**
     * get 请求
     * @param url 请求url地址
     * @param params 请求参数 map
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doGet(String url, Map<String, Object> params, Map<String, Object> headers) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                }
                else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }
        Request.Builder builder = new Request.Builder();
        // 添加请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        Request request = builder.url(sb.toString()).build();
        return execute(request);
    }

    /**
     * post 请求
     * @param url 请求url地址
     * @param params 请求参数 map
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doPost(String url, Map<String, String> params, Map<String, Object> headers) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                formBodyBuilder.add(key, params.get(key));
            }
        }
        Request.Builder builder = new Request.Builder();
        // 添加请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.header(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        Request request = builder.url(url).post(formBodyBuilder.build()).build();
        return execute(request);
    }

    /**
     * post 请求, 请求数据为 json 的字符串
     * @param url 请求url地址
     * @param json 请求数据, json 字符串
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doPostJson(String url, String json, Map<String, Object> headers) {
        return executePost(url, json, headers, JSON);
    }

    /**
     * post 请求, 请求数据为 xml 的字符串
     * @param url 请求url地址
     * @param xml 请求数据, xml 字符串
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doPostXml(String url, String xml, Map<String, Object> headers) {
        return executePost(url, xml, headers, XML);
    }

    private static String executePost(String url, String data, Map<String, Object> headers, MediaType contentType) {
        try {
            RequestBody requestBody = RequestBody.create(data, contentType);
            Request.Builder builder = new Request.Builder();
            // 添加请求头
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    builder.header(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            Request request = builder.url(url).post(requestBody).build();
            return execute(request);
        }
        catch (Exception e) {
            LOGGER.error("OkHttp POST 调用失败", e);
        }
        return null;
    }

    private static String execute(Request request) {
        Assert.notNull(CLIENT, "OkHttpClient is null");
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            }
        }
        catch (Exception e) {
            LOGGER.error("OkHttp 调用失败", e);
        }
        return null;
    }

}
