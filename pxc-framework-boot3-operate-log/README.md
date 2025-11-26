# pxc-framework-boot3-operate-log


## 使用方法
### maven

```xml
<dependency>
    <groupId>io.github.panxiaochao</groupId>
    <artifactId>pxc-framework-boot3-operate-log</artifactId>
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
    <artifactId>pxc-framework-boot3-operate-log</artifactId>
</dependency>
```

```yaml
  # 开启框架配置
  pxc-framework:
    operatelog:
      # LOGGER(默认) or OTHER
      log-type: other
      # 当是other时，填写自定义处理类，并且继承AbstractOperateLogHandler
      handler: io.github.panxiaochao.system.application.event.OperateLogEventHandler
```

## 注意事项

---
* Before: 在切点之前，织入相关代码；
* After: 在切点之后，织入相关代码;
* AfterReturning: 在切点返回内容后，织入相关代码，一般用于对返回值做些加工处理的场景；
* AfterThrowing: 用来处理当织入的代码抛出异常后的逻辑处理;
* Around: 在切入点前后织入代码，并且可以自由的控制何时执行切点；

---
* 当方法符合切点规则不符合环绕通知的规则时候，执行的顺序如下: Before→After→AfterRunning(如果有异常→AfterThrowing)
* 当方法符合切点规则并且符合环绕通知的规则时候，执行的顺序如下: Around→Before→Around→After执行 ProceedingJoinPoint.proceed() 之后的操作→AfterRunning(如果有异常→AfterThrowing)
