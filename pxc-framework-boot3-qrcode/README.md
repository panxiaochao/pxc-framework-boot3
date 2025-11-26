# pxc-framework-boot3-qrcode


## 使用方法
### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-qrcode</artifactId>
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
    <artifactId>pxc-framework-boot3-qrcode</artifactId>
</dependency>
```

使用工具类[QRCodeUtil.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fpanxiaochao%2Fqrcode%2Futils%2FQRCodeUtil.java)。


## 注意事项
