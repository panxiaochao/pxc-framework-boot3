# pxc-framework-boot3-core

`pxc-framework-boot3-core`框架，集成`ThreadPoolTaskExecutor`，`TaskScheduler`模块，以及异步`AsyncExecutor`执行器。

## 使用方法
### maven
```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-core</artifactId>
    <version>${最新版本}</version>
</dependency>
```

或者

```xml
<!-- 父工程引入 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.panxiaochao</groupId>
            <artifactId>pxc-framework-boot3-bom</artifactId>
            <version>${最新版本}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 子工程引入 -->
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-core</artifactId>
</dependency>
```

## YAML配置
```yaml
spring: 
  # 开启框架配置
  pxc-framework:
    # 是否开启异步, 默认false
    async: true
```

## 注意事项
