# pxc-framework-boot3-component

`pxc-framework-boot3-component`是下拉、树形组件框架。

## 使用方法
### maven
```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-component</artifactId>
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
    <artifactId>pxc-framework-boot3-component</artifactId>
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
