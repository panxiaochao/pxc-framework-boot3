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
package io.github.panxiaochao.web.config;

import io.github.panxiaochao.web.config.properties.WebProperties;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.HttpsSupport;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * <p>
 * RestTemplate 自动配置类
 * </p>
 *
 * @author Lypxc
 * @since 2025-02-07
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WebProperties.class)
@ConditionalOnProperty(name = "spring.pxc-framework-boot3.restTemplate.enabled", havingValue = "true")
public class RestTemplateAutoConfiguration {

    private final WebProperties webProperties;

    @Bean
    public HttpClientConnectionManager poolingHttpClientConnectionManager() {
        // 注册https请求, http请求默认就支持
        PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create();
        // @formatter:off
		builder.setTlsSocketStrategy(getTlsSocketStrategy())
				.setDefaultSocketConfig(getSocketConfig())
				.setDefaultConnectionConfig(getConnectionConfig())
				.setMaxConnTotal(webProperties.getRestTemplate().getMaxConnTotal())
				.setMaxConnPerRoute(webProperties.getRestTemplate().getMaxConnPerRoute());
		// @formatter:on
        return builder.build();
    }

    @Bean
    public HttpClient httpClient(HttpClientConnectionManager poolingHttpClientConnectionManager) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // 设置http连接管理器
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
        // 设置重试次数
        httpClientBuilder.setRetryStrategy(getRetryStrategy());
        // 常规配置
        httpClientBuilder.setDefaultRequestConfig(getRequestConfig());
        return httpClientBuilder.build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        // httpClient创建器
        clientHttpRequestFactory.setHttpClient(httpClient);
        // 连接超时时间/毫秒（连接上服务器(握手成功)的时间，超出抛出connect timeout）
        clientHttpRequestFactory
            .setConnectTimeout(Duration.ofMillis(webProperties.getRestTemplate().getConnectTimeout() * 1000));
        // 数据读取超时时间(socketTimeout)/毫秒（务器返回数据(response)的时间，超过抛出read timeout）
        clientHttpRequestFactory
            .setReadTimeout(Duration.ofMillis(webProperties.getRestTemplate().getReadTimeout() * 1000));
        // 连接池获取请求连接的超时时间，不宜过长，必须设置/毫秒（超时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException:
        // Timeout waiting for connection from pool）
        clientHttpRequestFactory
            .setConnectionRequestTimeout(Duration.ofMillis(webProperties.getRestTemplate().getConnectTimeout() * 1000));
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        // 配置请求工厂
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter messageConverter) {
                // 解决乱码问题
                messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
        return restTemplate;
    }

    /**
     * Gets a configured {@code SocketConfig}
     * @return {@link SocketConfig}
     */
    protected SocketConfig getSocketConfig() {
        return SocketConfig.custom()
            .setSoTimeout(Timeout.ofSeconds(webProperties.getRestTemplate().getConnectTimeout()))
            .build();
    }

    /**
     * Gets a configured {@code ConnectionConfig}
     * @return {@link ConnectionConfig}
     */
    protected ConnectionConfig getConnectionConfig() {
        return ConnectionConfig.custom()
            .setSocketTimeout(Timeout.ofSeconds(webProperties.getRestTemplate().getConnectTimeout()))
            .setConnectTimeout(Timeout.ofSeconds(webProperties.getRestTemplate().getConnectTimeout()))
            .setTimeToLive(Timeout.ofSeconds(webProperties.getRestTemplate().getTimeToLive()))
            .build();
    }

    /**
     * Gets a configured {@code TlsSocketStrategy}
     * @return {@link TlsSocketStrategy}
     */
    protected TlsSocketStrategy getTlsSocketStrategy() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();
            return new DefaultClientTlsStrategy(sslContext,
                    new String[] { TLS.V_1_3.id, TLS.V_1_2.id, TLS.V_1_1.id, TLS.V_1_0.id }, null, SSLBufferMode.STATIC,
                    HttpsSupport.getDefaultHostnameVerifier());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to configure the TLSSocketStrategy", e);
        }
    }

    /**
     * Gets a configured {@code RequestConfig}
     * @return {@link RequestConfig}
     */
    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(webProperties.getRestTemplate().getConnectTimeout()))
            .setResponseTimeout(Timeout.ofSeconds(webProperties.getRestTemplate().getConnectTimeout()))
            .setConnectionKeepAlive(Timeout.ofSeconds(webProperties.getRestTemplate().getTimeToLive()))
            .setRedirectsEnabled(false)
            .build();
    }

    /**
     * Gets a configured {@code HttpRequestRetryStrategy}
     * @return {@link HttpRequestRetryStrategy}
     */
    protected HttpRequestRetryStrategy getRetryStrategy() {
        return new DefaultHttpRequestRetryStrategy(webProperties.getRestTemplate().getMaxRetries(),
                TimeValue.ofSeconds(webProperties.getRestTemplate().getRetryInterval()));
    }

}
