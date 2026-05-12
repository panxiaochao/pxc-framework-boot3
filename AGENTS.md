# AGENTS.md - pxc-framework-boot3 (霄徵)

## Overview

`pxc-framework-boot3` (霄徵) 是一个基于 Spring Boot 3.x 的复合型框架和工具包，由23个Maven模块组成，用于快速开发。发布到 Maven Central。

| Item | Value |
|------|-------|
| **Java** | 17+ |
| **Spring Boot** | 3.5.11 |
| **Version** | `${revision}` (flatten-maven-plugin)，定义在 root pom.xml |
| **Current Version** | 3.0.2-SNAPSHOT |
| **License** | Apache 2.0 - 所有 `.java` 文件需要头部License |
| **GroupId** | `io.github.panxiaochao` |

## Build Commands

```bash
# Full build (validates formatting, runs tests)
mvn clean install

# Faster build (skip tests and javadoc)
mvn clean install -DskipTests -Dmaven.javadoc.skip=true

# Run single module with dependencies (-am builds ancestors first)
mvn clean install -pl pxc-framework-boot3-core -am -DskipTests

# Validate formatting only (Spring JavaFormat - runs automatically on mvn install)
mvn validate

# Release build (requires GPG key configured)
mvn clean install -P release
```

## Technical Stack

| Category | Technology |
|----------|------------|
| **Framework** | Spring Boot 3.5.11, Spring 6.2.16 |
| **ORM** | MyBatis-Plus 3.5.16, dynamic-datasource-spring-boot3-starter 4.5.0 |
| **Cache** | Redisson 3.52.0, Caffeine 3.2.3, Spring Data Redis 3.5.9 |
| **JSON** | Jackson 2.21.0, FastJSON 1.2.83 / FastJSON2 2.0.61 |
| **Utils** | Hutool 5.8.43, Commons IO 2.21.0, Commons Lang3 3.20.0 |
| **Cloud** | Spring Cloud 2025.0.1, OpenFeign 4.3.1, Gateway 4.3.3 |
| **WeChat** | weixin-java 4.8.0 (miniapp, pay, mp, cp, open, channel) |
| **Excel** | fastexcel 1.3.0 |
| **Other** | OkHttp3 4.12.0, Lombok 1.18.42, jasypt 4.0.4 |

## Project Architecture

### Package Convention

```
io.github.panxiaochao.boot3.{module}
```

示例：`io.github.panxiaochao.boot3.core`、`io.github.panxiaochao.boot3.redis`

### Layered Structure

| Layer | Module | Purpose |
|-------|--------|---------|
| **基础设施层** | `pxc-framework-boot3-core` | 线程池、异步执行、上下文配置、框架属性(`PxcFrameWorkProperties`) |
| **公共层** | `pxc-framework-boot3-common` | 通用响应(`R`, `RPage`)、常量(`CommonResponseEnum`)、异常、枚举 |
| **组件层** | `pxc-framework-boot3-component` | 可复用组件（select选择器、tree树形结构） |
| **工具层** | `pxc-framework-boot3-util` | 工具类封装 |
| **基础功能层** | `pxc-framework-boot3-bom` | BOM统一依赖版本管理 |
| **特性模块层** | `pxc-framework-boot3-*{14个模块}` | 各功能模块（见下方模块列表） |

### Module Dependencies Overview

```
pxc-framework-boot3-bom (BOM - 顶层)
       ↓
pxc-framework-boot3-core → pxc-framework-boot3-common
       ↓
pxc-framework-boot3-web (依赖 core + common)
pxc-framework-boot3-jackson (依赖 common)
pxc-framework-boot3-redis (依赖 core)
pxc-framework-boot3-cache (依赖 core)
pxc-framework-boot3-mybatis-plus (依赖 common)
pxc-framework-boot3-util (依赖 common)
       ↓
pxc-framework-boot3-operate-log (依赖 core + mybatis-plus + redis)
pxc-framework-boot3-trace-log (依赖 core)
pxc-framework-boot3-ratelimiter (依赖 core)
pxc-framework-boot3-repeatsubmit (依赖 core)
pxc-framework-boot3-sensitive (依赖 common)
pxc-framework-boot3-crypto (依赖 common)
pxc-framework-boot3-captcha (依赖 core)
pxc-framework-boot3-email (依赖 core)
pxc-framework-boot3-holiday (依赖 common)
pxc-framework-boot3-ip2region (依赖 common)
pxc-framework-boot3-excel (依赖 common)
pxc-framework-boot3-weixin (依赖 common)
pxc-framework-boot3-dynamic-datasource (依赖 common)
pxc-framework-boot3-mybatis-plus-generator (依赖 mybatis-plus)
pxc-framework-boot3-component (依赖 common + core)
```

## Module List (23 Modules)

| Module | Purpose | Error Code |
|--------|---------|------------|
| `pxc-framework-boot3-bom` | Bill of Materials - 依赖版本管理 | - |
| `pxc-framework-boot3-core` | 线程池、异步、上下文配置 | - |
| `pxc-framework-boot3-common` | 通用响应体、常量、枚举、异常 | - |
| `pxc-framework-boot3-component` | 选择器、树形等组件 | - |
| `pxc-framework-boot3-util` | 工具类 | - |
| `pxc-framework-boot3-jackson` | Jackson增强配置 | - |
| `pxc-framework-boot3-web` | OkHttp、Filter、WebMvc配置 | - |
| `pxc-framework-boot3-redis` | Redisson3、Redis字典配置 | - |
| `pxc-framework-boot3-cache` | Caffeine本地缓存配置 | - |
| `pxc-framework-boot3-ratelimiter` | 限流 | 6020-6029 |
| `pxc-framework-boot3-repeatsubmit` | 防重复提交 | 6010-6019 |
| `pxc-framework-boot3-operate-log` | 操作日志 | 6000-6009 |
| `pxc-framework-boot3-trace-log` | 日志链路追踪 | - |
| `pxc-framework-boot3-mybatis-plus` | ORM增强配置 | - |
| `pxc-framework-boot3-mybatis-plus-generator` | MyBatis-Plus代码生成器 | - |
| `pxc-framework-boot3-dynamic-datasource` | 动态多数据源 | - |
| `pxc-framework-boot3-sensitive` | 数据脱敏加密 | - |
| `pxc-framework-boot3-crypto` | 加解密模块 | - |
| `pxc-framework-boot3-captcha` | 验证码 | - |
| `pxc-framework-boot3-email` | 邮件发送 | - |
| `pxc-framework-boot3-holiday` | 节假日管理 | - |
| `pxc-framework-boot3-ip2region` | IP地域解析 | - |
| `pxc-framework-boot3-excel` | Excel导入导出 | - |
| `pxc-framework-boot3-weixin` | 微信小程序/公众号/支付等 | - |

## Auto-Configuration Mechanism

每个模块使用 Spring Boot 3 的新配置机制：
- 配置类使用 `@AutoConfiguration` 注解
- 在 `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中声明
- 统一放在 `{package}.config` 包下，命名规则：`*AutoConfiguration.java`

## Core Configuration Classes

### pxc-framework-boot3-core
| Class | Purpose |
|-------|---------|
| `FrameworkContextAutoConfiguration` | 框架上下文 |
| `AsyncExecutorAutoConfiguration` | 异步执行器 |
| `ThreadPoolAutoConfiguration` | 线程池配置 |
| `VirtualThreadConfig` | 虚拟线程配置 |
| `PxcFrameWorkProperties` | 框架统一配置属性类 |
| `YmlPropertySourceFactory` | YAML属性源工厂 |

### pxc-framework-boot3-web
| Class | Purpose |
|-------|---------|
| `OkHttpAutoConfiguration` | OkHttp连接池配置 |
| `WebMvcAutoConfiguration` | WebMvc配置 |
| `FilterAutoConfiguration` | 过滤器配置 |
| `CorsFilter` | 跨域过滤器 |
| `XssFilter` | XSS防护过滤器 |
| `EncodingFilter` | 编码过滤器 |
| `RestExceptionHandler` | 统一异常处理 |

### pxc-framework-boot3-redis
| Class | Purpose |
|-------|---------|
| `Redisson3AutoConfiguration` | Redisson配置 |
| `RedisDictAutoConfiguration` | Redis字典配置 |
| `PlusRedissonCacheManager` | 缓存管理器 |
| `RedisDictResolver` | 字典解析器 |

### pxc-framework-boot3-mybatis-plus
| Package | Purpose |
|---------|---------|
| `annotation/` | 自定义注解 |
| `config/` | MyBatis-Plus增强配置 |
| `handler/` | TypeHandler实现 |
| `injector/` | SQL注入器 |
| `interceptor/` | 拦截器 |
| `mapper/` | 通用Mapper |
| `po/` | 基础实体类 |

## Unified Response Structure

**pxc-framework-boot3-common** 中的响应类：
- `R<T>` - 通用响应体 (`code`, `message`, `data`)
- `RPage<T>` - 分页响应体
- `RPageObject` - 分页对象包装
- `CommonResponseEnum` - 通用响应枚举
- `ServerException` / `ServerRuntimeException` - 统一异常

## Code Conventions

- **格式化**: Spring JavaFormat，`mvn validate` 自动检查（在 install 时自动执行）
- **License Header**: Apache 2.0，头部格式固定（见现有文件），由 license-maven-plugin 管理
  - 排除：`src/test/**`, `README`, `*.xml`, `*.md`, `.gitignore`
- **Javadoc**: 新类需要 `@author` 标签
- **Lombok**: 全局启用，annotation processor 已配置
- **Error Codes**: 
  - operate-log: 6000-6009
  - repeatsubmit: 6010-6019
  - ratelimiter: 6020-6029

## Dependency Management

- 添加新依赖：先添加到 `pxc-framework-boot3-bom/pom.xml` 的 `dependencyManagement`
- 然后子模块直接引用 `artifactId` 即可自动获取版本
- 外部BOM：Hutool、MyBatis-Plus、OkHttp、Jackson

## Version Management

- `${revision}` 属性在 root pom.xml 中定义
- flatten-maven-plugin 自动处理 CI-friendly 版本号（如 `${revision}`）
- 执行 mvn 命令时会自动展开为实际版本号

## Release Process

1. 确保 GPG key 已配置
2. 执行 `mvn clean install -P release`
3. 使用 central-publishing-maven-plugin 发布到 Maven Central
4. Snapshot 版本发布到：https://central.sonatype.com/repository/maven-snapshots/
5. Release 版本发布到：https://ossrh-staging-api.central.sonatype.com/service/local/

## Test Commands

```bash
# Run all tests in a single module
mvn test -pl pxc-framework-boot3-core

# Run specific test class
mvn test -pl pxc-framework-boot3-util -Dtest=RegexUtilTest
```

## Common Issues

| Issue | Solution |
|-------|----------|
| Format violations | `mvn validate` before commit |
| Version not resolved | flatten-maven-plugin 会自动处理 `${revision}`，确保插件执行 |
| Missing dependency | 遵循 BOM -> 子模块的顺序添加 |
| CONTRIBUTING.md outdated | 忽略（仍提到 Gradle，实际使用 Maven） |
| Javadoc generation fails | 临时配置 `<additionalJOption>-Xdoclint:none</additionalJOption>` 已启用，可忽略非规范 Javadoc 警告 |

## Key Files

| File | Purpose |
|------|---------|
| `pom.xml` | Root POM - 定义 `${revision}` 版本、全局插件配置 |
| `pxc-framework-boot3-bom/pom.xml` | 依赖版本管理 |

## Reference

- [README.md](./README.md) - 模块概览和版本信息
- [Maven Central](https://central.sonatype.com/artifact/io.github.panxiaochao/pxc-framework-boot3-parent)