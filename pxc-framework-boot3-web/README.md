# pxc-framework-boot3-web

`pxc-framework-boot3-web`框架，集成`spring-boot-starter-web`模块，无需框架再次引入。

集成`EncodingFilter`、`CorsFilter`、`RequestWrapperFilter`，`JacksonConfiguration`模块。

## 使用方法

### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-web</artifactId>
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
    <artifactId>pxc-framework-boot3-web</artifactId>
</dependency>
```

## 注意事项
