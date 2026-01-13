# pxc-framework-boot3-jackson
## 功能描述

pxc-framework-boot3-jackson 是一个基于 Spring Boot 3 的 Jackson 组件，用于简化 Jackson 的使用，提供了一些常用的功能，包括 JSON 序列化、反序列化、自定义序列化器、反序列化器等。



## 使用方法
### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-jackson</artifactId>
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
```

相关工程引入

```xml
<!-- 子工程引入 -->
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-jackson</artifactId>
</dependency>
```

## 注意事项
