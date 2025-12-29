# Surefire "Invalid prefix or suffix" 错误 - 最终解决方案

## 问题根源

当 `${jacoco.argLine}` 为空时，argLine 变成 ` -Djava.io.tmpdir=C:\temp`（前面有空格），Surefire 3.0.0 可能无法正确解析。

## 最终解决方案

### 步骤1：移除空的属性初始化

**在 `properties` 中**：
```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <!-- 不要初始化空的 jacoco.argLine -->
</properties>
```

### 步骤2：确保 JaCoCo prepare-agent 正确运行

JaCoCo 的 `prepare-agent` 目标会在 `test` 阶段之前自动运行，设置 `jacoco.argLine` 属性。

### 步骤3：使用正确的 argLine 配置

```xml
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
```

## 如果问题仍然存在

### 方案A：使用属性默认值（推荐）

在 `properties` 中设置一个默认值：

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <!-- 设置默认值，避免空值问题 -->
    <jacoco.argLine>-Djava.io.tmpdir=C:\temp</jacoco.argLine>
</properties>
```

然后在 Surefire 中：
```xml
<argLine>${jacoco.argLine}</argLine>
```

JaCoCo 的 prepare-agent 会覆盖这个默认值。

### 方案B：使用两个独立的属性

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <surefire.argLine>-Djava.io.tmpdir=C:\temp</surefire.argLine>
</properties>

<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>${jacoco.argLine} ${surefire.argLine}</argLine>
    </configuration>
</plugin>
```

### 方案C：完全移除 argLine（仅用于测试，不生成覆盖率）

```xml
<configuration>
    <forkCount>1</forkCount>
    <reuseForks>true</reuseForks>
    <!-- 不设置 argLine，让 Surefire 使用默认值 -->
    <tempDir>${java.io.tmpdir}</tempDir>
</configuration>
```

**缺点**：无法生成 JaCoCo 覆盖率报告

## 验证配置

运行以下命令验证：

```bash
# 1. 清理项目
mvn clean

# 2. 查看有效POM中的argLine配置
mvn help:effective-pom | findstr -i "argLine"

# 3. 运行测试，查看JaCoCo代理是否启动
mvn test -Dtest=ApiAiControllerTest | findstr -i "jacoco"

# 4. 检查是否生成执行数据文件
dir target\jacoco.exec
```

## 预期输出

### 成功的标志

1. **JaCoCo代理启动**：
   ```
   [INFO] jacoco.argLine set to -javaagent:...=destfile=...target\jacoco.exec...
   ```

2. **测试成功运行**：
   ```
   [INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
   [INFO] BUILD SUCCESS
   ```

3. **生成执行数据文件**：
   ```
   target\jacoco.exec (文件存在且大小>0)
   ```

## 当前推荐配置

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <!-- 不初始化 jacoco.argLine，让 JaCoCo 的 prepare-agent 设置它 -->
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

## 如果仍然失败

1. **检查 Maven 版本**：建议使用 Maven 3.6+
2. **检查 Java 版本**：需要 Java 8+
3. **清理并重新构建**：`mvn clean install`
4. **查看详细日志**：`mvn test -X`

