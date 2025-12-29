# Surefire "Invalid prefix or suffix" 错误 - 彻底解决方案

## 问题分析

即使设置了默认值，仍然出现 "Invalid prefix or suffix" 错误，可能的原因：

1. **Surefire 3.0.0 对 argLine 解析更严格**
2. **属性值中包含特殊字符**（如路径中的反斜杠）
3. **属性值格式不正确**

## 解决方案A：完全移除 argLine（推荐用于测试）

如果暂时不需要 JaCoCo 覆盖率，可以完全移除 argLine：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <!-- 完全移除 argLine 配置 -->
        <tempDir>${java.io.tmpdir}</tempDir>
    </configuration>
</plugin>
```

**优点**：简单可靠，不会出错  
**缺点**：无法生成 JaCoCo 覆盖率报告

## 解决方案B：使用系统属性文件

在 `src/test/resources` 创建 `surefire.properties`：

```properties
# surefire.properties
java.io.tmpdir=C:\\temp
```

然后移除 argLine 中的临时目录参数。

## 解决方案C：降级 Surefire 版本

使用 Spring Boot 2.4.1 默认的 Surefire 版本：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>  <!-- Spring Boot 2.4.1 默认版本 -->
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <argLine>${jacoco.argLine} -Djava.io.tmpdir=C:\temp</argLine>
        <tempDir>${java.io.tmpdir}</tempDir>
    </configuration>
</plugin>
```

## 解决方案D：使用环境变量

在 properties 中使用环境变量：

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <java.io.tmpdir>C:\temp</java.io.tmpdir>
    <jacoco.argLine>-Djava.io.tmpdir=${java.io.tmpdir}</jacoco.argLine>
</properties>
```

## 解决方案E：分离 JaCoCo 和临时目录参数

使用两个独立的属性：

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <surefire.tmpdir>-Djava.io.tmpdir=C:\temp</surefire.tmpdir>
</properties>

<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <argLine>${jacoco.argLine} ${surefire.tmpdir}</argLine>
        <tempDir>${java.io.tmpdir}</tempDir>
    </configuration>
</plugin>
```

## 解决方案F：使用 Maven 属性转义

如果路径中有特殊字符，尝试转义：

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <tmp.dir>C:/temp</tmp.dir>  <!-- 使用正斜杠 -->
    <jacoco.argLine>-Djava.io.tmpdir=${tmp.dir}</jacoco.argLine>
</properties>
```

## 当前推荐配置（最安全）

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <!-- 不设置 jacoco.argLine，让 JaCoCo 自动设置 -->
</properties>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>  <!-- 使用稳定版本 -->
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <!-- 只使用 JaCoCo 的 argLine，不添加额外参数 -->
        <argLine>${jacoco.argLine}</argLine>
        <tempDir>C:\temp</tempDir>  <!-- 直接设置，不使用属性 -->
    </configuration>
</plugin>

<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
            <configuration>
                <propertyName>jacoco.argLine</propertyName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 调试步骤

1. **查看有效 POM**：
   ```bash
   mvn help:effective-pom > effective-pom.xml
   ```
   然后搜索 `argLine` 查看实际值

2. **查看 JaCoCo 设置的属性值**：
   ```bash
   mvn clean jacoco:prepare-agent
   mvn help:evaluate -Dexpression=jacoco.argLine -q -DforceStdout
   ```

3. **使用详细日志**：
   ```bash
   mvn test -X -Dtest=ApiAiControllerTest | findstr -i "argLine"
   ```

## 如果所有方案都失败

最后的选择：**完全移除 argLine 配置**

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <!-- 不设置 argLine -->
        <tempDir>C:\temp</tempDir>
    </configuration>
</plugin>
```

然后使用 JaCoCo 的默认属性名：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
            <!-- 使用默认属性名 argLine -->
            <!-- 不设置 propertyName，使用默认的 argLine -->
        </execution>
    </executions>
</plugin>
```

然后在 Surefire 中：
```xml
<argLine>${argLine}</argLine>
```

