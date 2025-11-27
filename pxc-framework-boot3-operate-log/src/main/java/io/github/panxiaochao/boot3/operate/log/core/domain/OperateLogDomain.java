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
package io.github.panxiaochao.boot3.operate.log.core.domain;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.github.panxiaochao.boot3.core.utils.IpUtil;
import io.github.panxiaochao.boot3.core.utils.ObjectUtil;
import io.github.panxiaochao.boot3.core.utils.RequestUtil;
import io.github.panxiaochao.boot3.operate.log.core.annotation.OperateLog;
import io.github.panxiaochao.boot3.operate.log.core.enums.OperateLogStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志 domain
 * </p>
 *
 * @author Lypxc
 * @since 2023-07-03
 */
@Getter
@Setter
@ToString
public class OperateLogDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = -8831737354114961499L;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 操作人员设备类型
     */
    private String operateType;

    /**
     * 请求url
     */
    private String requestUrl;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求类型
     */
    private String requestContentType;

    /**
     * 请求浏览器
     */
    private String browser;

    /**
     * 请求操作系统
     */
    private String os;

    /**
     * 请求Ip
     */
    private String ip;

    /**
     * 请求Ip地址
     */
    private String address;

    /**
     * 请求类名
     */
    private String className;

    /**
     * 请求类方法
     */
    private String classMethod;

    /**
     * GET - 请求参数
     */
    private String requestParam;

    /**
     * POST - 请求参数
     */
    private String requestBody;

    /**
     * 返回内容
     */
    private Object responseData;

    /**
     * 自定义参数值
     */
    private Object value;

    /**
     * 执行耗时, 单位毫秒
     */
    private long costTime;

    /**
     * 请求时间
     */
    private LocalDateTime requestDateTime;

    /**
     * 是否成功 1=成功, 0=失败
     */
    private Integer code;

    /**
     * 错误原因
     */
    private String errorMessage;

    /**
     * 精简版-错误原因
     */
    private String errorSimpleMessage;

    /**
     * 基础构建日志对象
     */
    public static OperateLogDomain build(OperateLog operateLog, Class<?> targetClass, String methodName) {
        OperateLogDomain operateLogDomain = new OperateLogDomain();
        if (ObjectUtil.isEmpty(targetClass)) {
            return operateLogDomain;
        }
        operateLogDomain.setClassName(targetClass.getSimpleName());
        operateLogDomain.setClassMethod(targetClass.getName() + "." + methodName + "()");
        if (ObjectUtil.isNotEmpty(operateLog)) {
            operateLogDomain.setTitle(operateLog.title());
            operateLogDomain.setDescription(operateLog.description());
            operateLogDomain.setBusinessType(operateLog.businessType().name());
            operateLogDomain.setOperateType(operateLog.operatorType());
        }
        if (RequestUtil.getRequest() != null) {
            operateLogDomain.setRequestUrl(RequestUtil.getRequest().getRequestURI());
            operateLogDomain.setRequestMethod(RequestUtil.getRequest().getMethod());
            operateLogDomain.setRequestContentType(RequestUtil.getRequest().getContentType());
            operateLogDomain.setIp(IpUtil.ofRequestIp());
            // 设置请求浏览器和操作系统
            String uaString = RequestUtil.getRequest().getHeader("User-Agent").toLowerCase();
            UserAgent userAgent = UserAgentUtil.parse(uaString);
            operateLogDomain.setBrowser(userAgent.getBrowser().toString() + " " + userAgent.getVersion());
            operateLogDomain.setOs(userAgent.getPlatform().toString() + " " + userAgent.getOs().toString());
        }
        operateLogDomain.setRequestDateTime(LocalDateTime.now());
        operateLogDomain.setCode(OperateLogStatus.SUCCESS.getCode());
        operateLogDomain.setCostTime(0);
        return operateLogDomain;
    }

}
