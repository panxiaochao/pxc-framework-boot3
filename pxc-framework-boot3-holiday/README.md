# pxc-framework-boot3-holiday

`pxc-framework-boot3-holiday`框架节假日工具框架，集成`HolidayClient`类调用。

也可以自提供扩展年份json数据，格式参考：

```json
{
  "year": 2024,
  "days": [
    {
      "name": "元旦",
      "date": "2024-01-01"
    },
    ...
  ]
}
```

## 使用方法

### maven

```xml

<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-holiday</artifactId>
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
    <artifactId>pxc-framework-boot3-holiday</artifactId>
</dependency>
```

## 注意事项
