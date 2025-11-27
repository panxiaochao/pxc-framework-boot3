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
package io.github.panxiaochao.boot3.captcha.utils;

import io.github.panxiaochao.boot3.core.utils.RandomUtil;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * </p>
 *
 * @author Lypxc
 * @since 2024-08-12
 * @version 1.0
 */
public class Graphics2DUtil {

    /**
     * RGB颜色范围上限
     */
    private static final int RGB_COLOR_BOUND = 256;

    /**
     * 创建{@link Graphics2D}
     * @param image {@link BufferedImage}
     * @param color {@link Color}背景颜色以及当前画笔颜色，{@code null}表示不设置背景色
     */
    public static Graphics2D createGraphics(BufferedImage image, Color color) {
        final Graphics2D graphics = image.createGraphics();
        if (null != color) {
            // 填充背景
            graphics.setColor(color);
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        }
        return graphics;
    }

    /**
     * 绘制字符串，使用随机颜色，默认抗锯齿
     * @param g {@link Graphics}画笔
     * @param str 字符串
     * @param font 字体
     * @param width 字符串总宽度
     * @param height 字符串背景高度
     * @return 画笔对象
     */
    public static Graphics drawString(Graphics g, String str, Font font, int width, int height) {
        return drawString(g, str, font, null, width, height);
    }

    /**
     * 绘制字符串，默认抗锯齿
     * @param g {@link Graphics}画笔
     * @param str 字符串
     * @param font 字体
     * @param color 字体颜色，{@code null} 表示使用随机颜色（每个字符单独随机）
     * @param width 字符串背景的宽度
     * @param height 字符串背景的高度
     * @return 画笔对象
     */
    public static Graphics drawString(Graphics g, String str, Font font, Color color, int width, int height) {
        // 抗锯齿
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        // 创建字体
        g.setFont(font);
        if (null != color) {
            g.setColor(color);
        }
        // 计算文字长度，计算居中的x点坐标，即字符串左边位置
        FontMetrics metrics = g.getFontMetrics(font);
        // 文字高度居中
        int y = (height - (metrics.getAscent() + metrics.getDescent())) / 2 + metrics.getAscent();
        // 根据字体算出字符串的占用宽度
        int textWidth = metrics.stringWidth(str);
        int len = str.length();
        // 文字的起始位置
        int x = (width - textWidth) / (len + 1);
        // 每个字符串的平均宽度
        int charWidth = textWidth / len;
        for (int i = 0; i < len; i++) {
            if (null == color) {
                // 产生随机的颜色值，让输出的每个字符的颜色值都将不同
                g.setColor(randomColor());
            }
            g.drawString(String.valueOf(str.charAt(i)), x + (x + charWidth) * i, y);
        }
        return g;
    }

    /**
     * 生成随机颜色
     */
    public static Color randomColor() {
        final ThreadLocalRandom random = RandomUtil.threadLocalRandom();
        return new Color(random.nextInt(RGB_COLOR_BOUND), random.nextInt(RGB_COLOR_BOUND),
                random.nextInt(RGB_COLOR_BOUND));
    }

    /**
     * 通过{@link ImageWriter}写出图片到输出流
     * @param image 图片
     * @param out 输出的Image流{@link ImageOutputStream}
     * @param quality 质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     */
    public static void write(BufferedImage image, OutputStream out, String imageType, float quality) {
        ImageWriter writer = ImageIO.getImageWritersByFormatName(imageType).next();
        // 设置质量
        ImageWriteParam imgWriteParams = null;
        if (quality > 0.0 && quality < 1.0) {
            imgWriteParams = writer.getDefaultWriteParam();
            if (imgWriteParams.canWriteCompressed()) {
                imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imgWriteParams.setCompressionQuality(quality);
            }
        }
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(imageOutputStream);
            if (null != imgWriteParams) {
                writer.write(null, new IIOImage(image, null, null), imgWriteParams);
            }
            else {
                writer.write(image);
            }
            imageOutputStream.flush();
        }
        catch (IOException e) {
            // skip
        }
        finally {
            writer.dispose();
        }
    }

}
