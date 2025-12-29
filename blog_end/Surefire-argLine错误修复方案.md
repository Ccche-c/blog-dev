# Surefire "Invalid prefix or suffix" 错误修复方案

## 问题描述

运行测试时出现错误：
```
Failed to execute goal ... maven-surefire-plugin:3.0.0:test ... 
Invalid prefix or suffix
```

## 问题原因

1. **空的 argLine 属性**：在 `properties` 中设置了空的 `<argLine></argLine>` 或 `<jacoco.argLine></jacoco.argLine>`
2. **属性解析问题**：当 `${jacoco.argLine}` 为空时，argLine 可能变成 ` -Djava.io.tmpdir=C:\temp`（前面有空格），导致 Surefire 解析失败
3. **属性未正确初始化**：JaCoCo 的 prepare-agent 可能在某些情况下没有正确设置属性

## 解决方案

### 方案1：移除空的属性初始化（推荐）

**修改前**：
```xml
<properties>
    <jacoco.argLine></jacoco.argLine>
</properties>
```

**修改后**：
```xml
<properties>
    <!-- JaCoCo的prepare-agent会自动设置jacoco.argLine属性，无需在此初始化 -->
</properties>
```

### 方案2：确保 argLine 格式正确

在 Surefire 配置中：
```xml
<argLine>${jacoco.argLine} -Djava.io.tmpdir=C:\temp</argLine>
```

**注意**：
- 如果 `${jacoco.argLine}` 为空，Maven 会将其替换为空字符串
- 结果：` -Djava.io.tmpdir=C:\temp`（前面有空格）
- 这可能导致 Surefire 解析错误

### 方案3：使用条件配置（如果支持）

某些 Maven 版本支持条件配置，但标准 Maven 不支持三元运算符。

## 当前配置

### pom.xml 配置

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <!-- JaCoCo的prepare-agent会自动设置jacoco.argLine属性，无需在此初始化 -->
</properties>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <argLine>${jacoco.argLine} -Djava.io.tmpdir=C:\temp</argLine>
        <tempDir>${java.io.tmpdir}</tempDir>
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

## 验证步骤

1. **清理项目**
   ```bash
   mvn clean
   ```

2. **运行测试**
   ```bash
   mvn test -Dtest=ApiAiControllerTest
   ```

3. **检查 JaCoCo 代理**
   应该看到：
   ```
   [INFO] jacoco.argLine set to -javaagent:...=destfile=...target\jacoco.exec...
   ```

4. **如果仍有错误**
   - 检查 `target` 目录是否已清理
   - 检查 Maven 版本（建议 3.6+）
   - 检查 Java 版本（需要 Java 8+）

## 替代方案

如果上述方案仍有问题，可以尝试：

### 方案A：不使用 JaCoCo argLine（仅测试）

```xml
<configuration>
    <forkCount>1</forkCount>
    <reuseForks>true</reuseForks>
    <argLine>-Djava.io.tmpdir=C:\temp</argLine>
    <tempDir>${java.io.tmpdir}</tempDir>
</configuration>
```

**缺点**：无法生成覆盖率报告

### 方案B：使用默认 argLine

完全移除 argLine 配置，让 Surefire 使用默认值：

```xml
<configuration>
    <forkCount>1</forkCount>
    <reuseForks>true</reuseForks>
    <tempDir>${java.io.tmpdir}</tempDir>
</configuration>
```

然后在 JaCoCo 中设置默认属性名：

```xml
<configuration>
    <propertyName>argLine</propertyName>
</configuration>
```

## 常见问题

### Q: 为什么会出现 "Invalid prefix or suffix"？

**A**: 通常是因为 argLine 的值格式不正确，例如：
- 以空格开头：` -Djava.io.tmpdir=C:\temp`
- 包含无效字符
- 属性值为空但格式不正确

### Q: 如何验证 argLine 的值？

**A**: 运行以下命令查看有效 POM：
```bash
mvn help:effective-pom | findstr argLine
```

### Q: JaCoCo 代理没有启动？

**A**: 检查：
1. 是否运行了 `mvn clean`（清理旧的配置）
2. 是否在 `test` 阶段之前运行了 `prepare-agent`
3. 检查日志中是否有 `jacoco.argLine set to ...` 消息

## 相关文档

- [Maven Surefire Plugin 文档](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JaCoCo Maven Plugin 文档](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

