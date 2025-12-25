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
package io.github.panxiaochao.boot3.web.config;

import io.github.panxiaochao.boot3.web.config.properties.WebProperties;
import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * OkHttp3 自动配置类
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-18
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WebProperties.class)
public class OkHttpAutoConfiguration {

    private final WebProperties properties;

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(properties.getOkHttp().getMaxIdleConnections(),
                properties.getOkHttp().getKeepAliveDuration(), TimeUnit.SECONDS);
    }

    @Bean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory(), x509TrustManager());
        // 是否开启缓存
        builder.setRetryOnConnectionFailure$okhttp(false);
        builder.connectTimeout(properties.getOkHttp().getConnectTimeout(), TimeUnit.SECONDS);
        builder.readTimeout(properties.getOkHttp().getReadTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(properties.getOkHttp().getWriteTimeout(), TimeUnit.SECONDS);
        builder.connectionPool(connectionPool);
        builder.followRedirects(true);
        builder.followSslRedirects(true);
        // 设置默认主机验证规则
        builder.setHostnameVerifier$okhttp((hostname, session) -> true);
        return builder.build();
    }

    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public SSLSocketFactory sslSocketFactory() {
        try {
            // 信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { x509TrustManager() }, new SecureRandom());
            return sslContext.getSocketFactory();
        }
        catch (Exception e) {
            // skip
        }
        return null;
    }

}
