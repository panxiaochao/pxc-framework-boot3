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
package io.github.panxiaochao.captcha.draw;

import io.github.panxiaochao.captcha.enums.CaptchaType;
import io.github.panxiaochao.captcha.enums.ImageType;
import io.github.panxiaochao.captcha.generator.CharacterCaptchaGenerator;
import io.github.panxiaochao.captcha.generator.ICaptchaGenerator;
import io.github.panxiaochao.captcha.utils.Graphics2DUtil;
import io.github.panxiaochao.core.utils.ObjectUtil;
import io.github.panxiaochao.core.utils.RandomUtil;
import io.github.panxiaochao.core.utils.StringPools;
import io.github.panxiaochao.crypto.utils.Base64Util;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 字符串验证码，默认<code>PNG</code>格式, Builder模式.
 * </p>
 *
 * @author Lypxc
 * @since 2024-08-12
 * @version 1.0
 */
public class CharacterCaptcha implements IDrawCaptcha {

    private static final long serialVersionUID = 1L;

    private static final Builder INSTANCE = new Builder();

    /**
     * 验证码
     */
    private String captchaCode;

    /**
     * 验证码字节数组
     */
    private byte[] imageBytes;

    /**
     * 验证码生成器
     */
    private final ICaptchaGenerator generator;

    /**
     * 图片生成格式
     */
    private final ImageType imageType;

    /**
     * 图片的宽度 - width
     */
    private final int width;

    /**
     * 图片的高度- height
     */
    private final int height;

    /**
     * 验证码干扰元素个数
     */
    private final int interfereCount;

    /**
     * 背景色
     */
    private final Color background;

    /**
     * 字体
     */
    private final Font font;

    /**
     * 干扰类型，圈圈 - 干扰线
     */
    private final int interfereType;

    /**
     * 构造方法
     */
    public CharacterCaptcha(Builder builder) {
        this.generator = new CharacterCaptchaGenerator(builder.codeLength, builder.captchaType);
        this.imageType = builder.imageType;
        this.width = builder.width;
        this.height = builder.height;
        this.interfereCount = builder.interfereCount;
        this.background = builder.background;
        this.font = builder.font;
        this.interfereType = builder.interfereCount;
    }

    /**
     * 获取实例
     * @return CharacterCaptcha
     */
    public static Builder builder() {
        return INSTANCE;
    }

    public static class Builder {

        /**
         * 图片的宽度 - width
         */
        private int width;

        /**
         * 图片的高度- height
         */
        private int height;

        /**
         * 验证码干扰元素个数
         */
        private int interfereCount;

        /**
         * 字体
         */
        private Font font;

        /**
         * 验证码生成器
         */
        private ICaptchaGenerator generator;

        /**
         * 验证码字符个数
         */
        private int codeLength;

        /**
         * 验证码字符类型
         */
        private CaptchaType captchaType;

        /**
         * 背景色
         */
        private Color background;

        /**
         * 验证码生成格式
         */
        private ImageType imageType;

        /**
         * 干扰类型，圈圈 - 干扰线
         */
        private int interfereType;

        /**
         * Instantiates a new Builder.
         */
        public Builder() {
            // Default value
            this.width = 100;
            this.height = 40;
            this.interfereCount = 50;
            this.codeLength = 4;
            this.font = new Font(Font.SANS_SERIF, Font.PLAIN, (int) (this.height * 0.8));
            this.captchaType = CaptchaType.CHAR_NUMBER;
            this.background = Color.WHITE;
            this.imageType = ImageType.PNG;
            this.interfereType = 0;
        }

        /**
         * 图片的宽度，默认100px
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * 图片的高度，默认40px
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * 验证码干扰元素个数，默认50个
         */
        public Builder interfereCount(int interfereCount) {
            this.interfereCount = interfereCount;
            return this;
        }

        /**
         * 字体，默认<code>Font.SANS_SERIF</code>
         */
        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        /**
         * 验证码字符个数，默认4个
         */
        public Builder codeLength(int codeLength) {
            this.codeLength = codeLength;
            return this;
        }

        /**
         * 验证码字符类型，默认字符数字混合
         */
        public Builder captchaType(CaptchaType captchaType) {
            this.captchaType = captchaType;
            return this;
        }

        /**
         * 背景色，默认白色
         */
        public Builder background(Color background) {
            this.background = background;
            return this;
        }

        /**
         * 验证码生成格式，默认输出<code>PNG</code>格式
         */
        public Builder imageType(ImageType imageType) {
            this.imageType = imageType;
            return this;
        }

        /**
         * 干扰类型，圈圈 - 干扰线，默认圈圈
         *
         * <p>
         * 圈圈：0，干扰线：1
         * </p>
         */
        public Builder interfereType(int interfereType) {
            this.interfereType = interfereType;
            return this;
        }

        public CharacterCaptcha build() {
            return new CharacterCaptcha(this);
        }

    }

    /**
     * 图片输出媒体类型
     */
    public String getContentType() {
        return this.imageType.getContentType();
    }

    /**
     * 获取验证码的内容
     */
    @Override
    public String getCaptchaCode() {
        if (ObjectUtil.isEmpty(this.captchaCode)) {
            this.captchaCode = generator.generateCode();
        }
        return this.captchaCode;
    }

    /**
     * 将验证码写出入到目标流中
     * @param out 目标流
     */
    @Override
    public void writeTo(OutputStream out) {
        try {
            IOUtils.write(getImageBytes(), out);
        }
        catch (IOException e) {
            // skip
        }
    }

    /**
     * 获取图形验证码图片bytes
     */
    @Override
    public byte[] getImageBytes() {
        if (ObjectUtil.isEmpty(this.imageBytes)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(drawImage(), this.imageType.getSuffix(), out);
                this.imageBytes = out.toByteArray();
            }
            catch (IOException e) {
                // skip
            }
        }
        return this.imageBytes;
    }

    /**
     * 获得图片的Base64形式
     */
    @Override
    public String getImageBase64() {
        return Base64Util.encodeToString(getImageBytes());
    }

    /**
     * 获取图片的Base64Data格式
     */
    @Override
    public String getImageBase64Data() {
        return String.join(StringPools.COMMA, imageType.getImageData(), Base64Util.encodeToString(getImageBytes()));
    }

    /**
     * 绘制图片
     *
     * <ul>
     * <li>TYPE_INT_RGB: 包含 8 位 RGB 像素的图像，其中每个像素由 32 位整数表示</li>
     * <li>TYPE_4BYTE_ABGR: 包含 8 位 ABGR 像素的图像，其中每个像素由 32 位整数表示，支持alpha通道的rgb图像</li>
     * </ul>
     */
    private BufferedImage drawImage() {
        final BufferedImage image = new BufferedImage(this.width, this.height,
                ObjectUtil.isEmpty(this.background) ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = Graphics2DUtil.createGraphics(image, this.background);
        try {
            // 随机画干扰元素
            if (this.interfereType == 0) {
                // 干扰圈圈
                drawInterfereCircle(graphics);
            }
            else {
                // 干扰线
                drawInterfereLine(graphics);
            }
            // 画字符串
            Graphics2DUtil.drawString(graphics, getCaptchaCode(), this.font, this.width, this.height);
        }
        catch (Exception e) {
            throw new RuntimeException("Draw Graphics2D is error!", e);
        }
        finally {
            // 释放资源
            graphics.dispose();
        }
        return image;
    }

    /**
     * 绘制干扰圈圈
     * @param graphics {@link Graphics2D}画笔
     */
    private void drawInterfereCircle(Graphics2D graphics) {
        final ThreadLocalRandom random = RandomUtil.threadLocalRandom();
        for (int i = 0; i < this.interfereCount; i++) {
            graphics.setColor(Graphics2DUtil.randomColor());
            graphics.drawOval(random.nextInt(width), random.nextInt(height), random.nextInt(height >> 2),
                    random.nextInt(height >> 2));
        }
    }

    /**
     * 绘制干扰线
     * @param graphics {@link Graphics2D}画笔
     */
    private void drawInterfereLine(Graphics2D graphics) {
        final ThreadLocalRandom random = RandomUtil.threadLocalRandom();
        for (int i = 0; i < this.interfereCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 5);
            int ye = ys + random.nextInt(height / 5);
            graphics.setColor(Graphics2DUtil.randomColor());
            graphics.drawLine(xs, ys, xe, ye);
        }
    }

}