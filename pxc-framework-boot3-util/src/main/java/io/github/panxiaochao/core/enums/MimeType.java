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
package io.github.panxiaochao.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * MimeType 枚举常量类.
 * </p>
 * <p>
 * 类型参考DOC: <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA
 * Media Types</a>
 * </p>
 *
 * @author Lypxc
 * @since 2025-03-17
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum MimeType {

    // ================ 字体类型 ================//
    FONT_TTF("ttf", new MediaType("font", "ttf")),

    FONT_OTF("otf", new MediaType("font", "otf")),

    FONT_WOFF("woff", new MediaType("font", "woff")),

    FONT_WOFF2("woff2", new MediaType("font", "woff2")),

    FONT_EOT("eot", new MediaType("application", "vnd.ms-fontobject")),

    FONT_SFNT("sfnt", new MediaType("font", "sfnt")),

    FONT_OTF2("otf2", new MediaType("font", "otf2")),

    // ================ 图片类型 ================//
    IMAGE_JPG("jpg", MediaType.IMAGE_JPEG),

    IMAGE_JPEG("jpeg", MediaType.IMAGE_JPEG),

    IMAGE_PNG("png", MediaType.IMAGE_PNG),

    IMAGE_GIF("gif", MediaType.IMAGE_GIF),

    IMAGE_WEBP("webp", new MediaType("image", "webp")),

    IMAGE_BMP("bmp", new MediaType("image", "bmp")),

    IMAGE_HEIC("heic", new MediaType("image", "heic")),

    IMAGE_HEIF("heif", new MediaType("image", "heif")),

    IMAGE_TIFF("tiff", new MediaType("image", "tiff")),

    IMAGE_ICO("ico", new MediaType("image", "x-icon")),

    IMAGE_PSD("psd", new MediaType("image", "vnd.adobe.photoshop")),

    IMAGE_DNG("dng", new MediaType("image", "x-adobe-dng")),

    // ================ 矢量图形 ================//
    VECTOR_SVG("svg", new MediaType("image", "svg+xml")),

    VECTOR_AI("ai", new MediaType("application", "postscript")),

    VECTOR_EPS("eps", new MediaType("application", "postscript")),

    // ================ 应用文档 ================//
    OFFICE_DOC("doc", new MediaType("application", "msword")),

    OFFICE_DOCX("docx", new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document")),

    OFFICE_XLS("xls", new MediaType("application", "vnd.ms-excel")),

    OFFICE_XLSX("xlsx", new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet")),

    OFFICE_PPT("ppt", new MediaType("application", "vnd.ms-powerpoint")),

    OFFICE_PPTX("pptx", new MediaType("application", "vnd.openxmlformats-officedocument.presentationml.presentation")),

    OFFICE_PDF("pdf", MediaType.APPLICATION_PDF),

    OFFICE_XPS("xps", new MediaType("application", "vnd.ms-xpsdocument")),

    OFFICE_ODT("odt", new MediaType("application", "vnd.oasis.opendocument.text")),

    OFFICE_ODS("ods", new MediaType("application", "vnd.oasis.opendocument.spreadsheet")),

    OFFICE_ODP("odp", new MediaType("application", "vnd.oasis.opendocument.presentation")),

    // ================ 视频类型 ================//
    VIDEO_MP4("mp4", new MediaType("video", "mp4")),

    VIDEO_AVI("avi", new MediaType("video", "x-msvideo")),

    VIDEO_MOV("mov", new MediaType("video", "quicktime")),

    VIDEO_WMV("wmv", new MediaType("video", "x-ms-wmv")),

    VIDEO_FLV("flv", new MediaType("video", "x-flv")),

    VIDEO_MKV("mkv", new MediaType("video", "x-matroska")),

    VIDEO_WEBM("webm", new MediaType("video", "webm")),

    VIDEO_MPEG("mpeg", new MediaType("video", "mpeg")),

    VIDEO_MP3("mp3", new MediaType("audio", "mpeg")),

    VIDEO_3GP("3gp", new MediaType("video", "3gpp")),

    VIDEO_OGG("ogg", new MediaType("video", "ogg")),

    VIDEO_M4V("m4v", new MediaType("video", "x-m4v")),

    VIDEO_MPG("mpg", new MediaType("video", "mpeg")),

    VIDEO_MPG4("mpg4", new MediaType("video", "mpeg4")),

    VIDEO_WAV("wav", new MediaType("audio", "x-wav")),

    VIDEO_AAC("aac", new MediaType("audio", "aac")),

    VIDEO_MID("mid", new MediaType("audio", "midi")),

    VIDEO_MIDI("midi", new MediaType("audio", "midi")),

    VIDEO_AMR("amr", new MediaType("audio", "amr")),

    VIDEO_M4A("m4a", new MediaType("audio", "x-m4a")),

    VIDEO_FLAC("flac", new MediaType("audio", "flac")),

    VIDEO_RM("rm", new MediaType("video", "rm")),

    VIDEO_RMVB("rmvb", new MediaType("video", "rmvb")),

    VIDEO_OGV("ogv", new MediaType("video", "ogg")),

    VIDEO_3GPP("3gpp", new MediaType("video", "3gpp")),

    // ================ 文本类型 ================//
    TEXT_TXT("txt", MediaType.TEXT_PLAIN),

    TEXT_HTML("html", MediaType.TEXT_HTML),

    TEXT_XML("xml", MediaType.TEXT_XML),

    TEXT_CSS("css", new MediaType("text", "css")),

    TEXT_CSV("csv", new MediaType("text", "csv")),

    TEXT_JS("js", new MediaType("application", "javascript")),

    TEXT_JSON("json", MediaType.APPLICATION_JSON),

    TEXT_YML("yml", new MediaType("text", "yaml")),

    TEXT_YAML("yaml", new MediaType("text", "yaml")),

    TEXT_PROPERTIES("properties", MediaType.TEXT_PLAIN),

    TEXT_MARKDOWN("md", new MediaType("text", "markdown")),

    // ================ 电子书 ================//
    BOOK_RTF("rtf", new MediaType("text", "rtf")),

    BOOK_EPUB("epub", new MediaType("application", "epub+zip")),

    BOOK_MOBI("mobi", new MediaType("application", "x-mobipocket-ebook")),

    BOOK_CHM("chm", new MediaType("application", "x-chm")),

    BOOK_AZW("azw", new MediaType("application", "vnd.amazon.ebook")),

    // ================ 软件文件 ================//
    SOFTWARE_EXE("exe", new MediaType("application", "octet-stream")),

    SOFTWARE_MSI("msi", new MediaType("application", "x-msi")),

    SOFTWARE_DMG("dmg", new MediaType("application", "octet-stream")),

    SOFTWARE_PKG("pkg", new MediaType("application", "x-newton-compatible-pkg")),

    SOFTWARE_DEB("deb", new MediaType("application", "x-debian-package")),

    SOFTWARE_RPM("rpm", new MediaType("application", "x-redhat-package-manager")),

    SOFTWARE_SNAP("snap", new MediaType("application", "vnd.snap")),

    SOFTWARE_APK("apk", new MediaType("application", "vnd.android.package-archive")),

    SOFTWARE_IPA("ipa", new MediaType("application", "octet-stream")),

    // ================ 压缩文件 ================//
    COMPRESS_ZIP("zip", new MediaType("application", "zip")),

    COMPRESS_RAR("rar", new MediaType("application", "x-rar-compressed")),

    COMPRESS_7Z("7z", new MediaType("application", "x-7z-compressed")),

    COMPRESS_TAR("tar", new MediaType("application", "x-tar")),

    COMPRESS_GZ("gz", new MediaType("application", "gzip")),

    COMPRESS_XZ("xz", new MediaType("application", "x-xz")),

    COMPRESS_TGZ("tgz", new MediaType("application", "x-compressed")),

    // ================ 编程相关 ================//
    PROGRAMMING_JAVA("java", new MediaType("text", "x-java-source")),

    PROGRAMMING_CLASS("class", new MediaType("application", "java-vm")),

    PROGRAMMING_JAR("jar", new MediaType("application", "java-archive")),

    PROGRAMMING_PY("py", new MediaType("text", "x-python")),

    PROGRAMMING_PHP("php", new MediaType("text", "x-php")),

    PROGRAMMING_RB("rb", new MediaType("text", "x-ruby")),

    PROGRAMMING_C("c", new MediaType("text", "x-c")),

    PROGRAMMING_CPP("cpp", new MediaType("text", "x-c++src")),

    PROGRAMMING_H("h", new MediaType("text", "x-c++hdr")),

    PROGRAMMING_CSH("csh", new MediaType("text", "x-csh")),

    PROGRAMMING_SH("sh", new MediaType("text", "x-sh")),

    PROGRAMMING_CS("cs", new MediaType("text", "x-csharp")),

    PROGRAMMING_SQL("sql", new MediaType("text", "sql")),;

    private final String extensions;

    private final MediaType mediaType;

    /**
     * 缓存所有MimeType
     */
    private static final Map<String, MediaType> VALUES_MAP = Arrays.stream(values())
        .collect(Collectors.toConcurrentMap(MimeType::getExtensions, MimeType::getMediaType,
                (existing, replacement) -> existing));

    /**
     * 根据文件扩展名查找对应的MediaType
     * @param extensions 文件扩展名（可带点）
     * @return 匹配的MediaType，找不到时返回默认类型
     */
    public static MediaType findByExtension(String extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return defaultType();
        }
        String cleanExt = cleanExtension(extensions);
        return VALUES_MAP.getOrDefault(cleanExt, defaultType());
    }

    /**
     * 根据文件名自动识别MediaType
     * @param fileName 完整文件名
     * @return 匹配的MediaType，找不到时返回默认类型
     */
    public static MediaType findByFileName(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return defaultType();
        }
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return findByExtension(extension);
    }

    /**
     * 清理 文件扩展名
     */
    private static String cleanExtension(String ext) {
        return ext.replaceFirst("^\\.", "").toLowerCase();
    }

    /**
     * 获取默认类型
     */
    private static MediaType defaultType() {
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    /**
     * 获取所有扩展名
     */
    public Set<String> getAllExtensions() {
        return Collections.unmodifiableSet(new HashSet<>(VALUES_MAP.keySet()));
    }

}
