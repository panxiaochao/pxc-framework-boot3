# pxc-framework-boot3-mybatis-plus-generator

## 功能描述

pxc-framework-boot3-mybatis-plus-generator 是一个基于 Spring Boot 3 的 Mybatis-Plus 代码生成器组件，用于根据数据库表结构自动生成 Mybatis-Plus 相关的代码，包括实体类、Mapper 接口、Service 接口、Controller 类等。

## 使用方法
### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-mybatis-plus-generator</artifactId>
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
    <artifactId>pxc-framework-boot3-mybatis-plus-generator</artifactId>
</dependency>
```

## 注意事项
