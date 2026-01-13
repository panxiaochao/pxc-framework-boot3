# pxc-framework-boot3-mybatis-plus

## 功能描述

pxc-framework-boot3-mybatis-plus 是一个基于 Spring Boot 3 的 Mybatis-Plus 组件，用于简化 Mybatis-Plus 的使用，提供了一些常用的功能，包括
CRUD 操作、分页查询、条件构造器等。

## 使用方法

### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-mybatis-plus</artifactId>
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
    <artifactId>pxc-framework-boot3-mybatis-plus</artifactId>
</dependency>
```

## 注意事项
