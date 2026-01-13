# pxc-framework-boot3-sensitive

## 功能描述

pxc-framework-boot3-sensitive 是一个基于 Spring Boot 3 的敏感信息处理组件，用于对应用程序中的敏感信息进行加密、解密和脱敏处理。



## 使用方法

### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-sensitive</artifactId>
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
    <artifactId>pxc-framework-boot3-sensitive</artifactId>
</dependency>
```

## 注意事项
