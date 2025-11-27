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

import io.github.panxiaochao.boot3.core.enums.MimeType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 下载工具类, 采用 {@link ResponseEntity} 类封装返回数据.
 * </p>
 *
 * @author Lypxc
 * @since 2025-03-17
 * @version 1.0
 */
public class DownLoadUtil {

    /**
     * LOGGER DownLoadUtil.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DownLoadUtil.class);

    /**
     * 下载文件
     * @param bodyBytes 文件字节数组
     * @param fileName 文件名
     * @return ResponseEntity&lt;byte[]&gt;
     */
    public static ResponseEntity<byte[]> download(byte[] bodyBytes, String fileName) {
        try {
            final String contentDispositionValue = getContentDispositionValue(fileName);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionValue)
                .contentType(MimeType.findByFileName(fileName))
                .contentLength(bodyBytes.length)
                .body(bodyBytes);
        }
        catch (Exception e) {
            LOGGER.error("下载文件失败", e);
            return fail();
        }
    }

    /**
     * 下载失败
     * @return ResponseEntity&lt;byte[]&gt;
     */
    public static ResponseEntity<byte[]> fail() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }

    /**
     * 下载模板
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名, 例如: excel/temp.xlsx 重点:
     * 模板文件必须放置到启动类对应的 resource 目录下
     * @return ResponseEntity&lt;byte[]&gt;
     */
    public static ResponseEntity<byte[]> downloadTemplate(String templatePath) {
        try {
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            // 检查资源是否存在
            if (!templateResource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String fileName = extractFileNameFromPath(templatePath);
            byte[] bodyBytes = IOUtils.toByteArray(templateResource.getInputStream());

            final String contentDispositionValue = getContentDispositionValue(fileName);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionValue)
                .contentType(MimeType.findByFileName(fileName))
                .body(bodyBytes);
        }
        catch (Exception e) {
            LOGGER.error("下载文件失败", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    /**
     * 从路径中提取文件名
     * @param templatePath 模板路径
     * @return 文件名
     */
    private static String extractFileNameFromPath(String templatePath) {
        int lastSlashIndex = templatePath.lastIndexOf('/');
        return (lastSlashIndex != -1) ? templatePath.substring(lastSlashIndex + 1) : templatePath;
    }

    private static String getContentDispositionValue(String fileName) {
        String percentEncodedFileName = percentEncode(fileName);
        return ContentDisposition.attachment()
            .filename(percentEncodedFileName, StandardCharsets.UTF_8)
            .build()
            .toString();
    }

    private static String percentEncode(String s) {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return encode.replaceAll("\\+", "%20");
    }

}
