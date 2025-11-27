/*
 * Copyright © 2025-2026 Lypxc (545685602@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// /*
// * Copyright © 2022-2024 Lypxc (545685602@qq.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
// package io.github.panxiaochao.core.utils;
//
// import org.apache.hc.client5.http.config.ConnectionConfig;
// import org.apache.hc.client5.http.config.RequestConfig;
// import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
// import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
// import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
// import org.apache.hc.client5.http.impl.classic.HttpClients;
// import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
// import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
// import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
// import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
// import org.apache.hc.core5.http.URIScheme;
// import org.apache.hc.core5.http.config.Registry;
// import org.apache.hc.core5.http.config.RegistryBuilder;
// import org.apache.hc.core5.http.io.SocketConfig;
// import org.apache.hc.core5.ssl.SSLContexts;
// import org.apache.hc.core5.util.TimeValue;
// import org.apache.hc.core5.util.Timeout;
//
// import javax.net.ssl.SSLContext;
//
// /**
// * <p>
// * </p>
// *
// * @author Lypxc
// * @since 2024-07-30
// * @version 1.0
// */
// public class HttpClientUtil {
//
// private static final CloseableHttpClient HTTP_CLIENT;
// static {
// // 自定义 SSL 策略
// Registry<ConnectionSocketFactory> registry =
// RegistryBuilder.<ConnectionSocketFactory>create()
// .register(URIScheme.HTTP.getId(), PlainConnectionSocketFactory.getSocketFactory())
// .register(URIScheme.HTTPS.getId(), createSSLConnSocketFactory())
// .build();
//
// ConnectionConfig connectionConfig = ConnectionConfig.custom()
// // 连接超时
// .setConnectTimeout(Timeout.ofSeconds(10))
// // socket超时
// .setSocketTimeout(Timeout.ofSeconds(10))
// // 生存时间
// .setTimeToLive(TimeValue.ofSeconds(5))
// // 不活动生存时间
// .setValidateAfterInactivity(TimeValue.ofSeconds(10))
// .build();
//
// SocketConfig socketConfig =
// SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(10)).build();
//
// // 设置连接池
// PoolingHttpClientConnectionManager connManager = new
// PoolingHttpClientConnectionManager(registry);
// connManager.setMaxTotal(100);
// connManager.setDefaultMaxPerRoute(20);
// connManager.setDefaultConnectionConfig(connectionConfig);
// connManager.setDefaultSocketConfig(socketConfig);
//
// // 设置属性
// RequestConfig requestConfig = RequestConfig.custom()
// .setConnectionRequestTimeout(Timeout.ofSeconds(5))
// .setResponseTimeout(Timeout.ofSeconds(10))
// .setConnectionKeepAlive(Timeout.ofSeconds(5))
// .build();
//
// HTTP_CLIENT = HttpClients.custom()
// .setConnectionManager(connManager)
// .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
// .setRetryStrategy(new DefaultHttpRequestRetryStrategy())
// .setDefaultRequestConfig(requestConfig)
// .build();
// }
//
// private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
// SSLConnectionSocketFactory sslsf;
// try {
// SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) ->
// true).build();
// sslsf = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);
// }
// catch (Exception e) {
// throw new RuntimeException(e);
// }
// return sslsf;
// }
//
// }
