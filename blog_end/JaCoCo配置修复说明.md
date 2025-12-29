# JaCoCo配置修复说明

## 问题

运行测试时出现错误：
```
[ERROR] Failed to execute goal ... maven-surefire-plugin:3.0.0-M5:test ... 
Invalid prefix or suffix
```

## 根本原因

1. **属性名冲突**：JaCoCo默认使用 `${argLine}` 属性，但Surefire插件也使用相同的属性名，导致冲突
2. **属性未初始化**：`${argLine}` 在某些情况下可能未定义，导致 "Invalid prefix or suffix" 错误

## 解决方案

### 1. 使用自定义属性名

修改JaCoCo配置，使用自定义属性名 `jacoco.argLine`：

```xml
<execution>
    <id>prepare-agent</id>
    <goals>
        <goal>prepare-agent</goal>
    </goals>
    <configuration>
        <!-- 指定自定义属性名，避免与Surefire冲突 -->
        <propertyName>jacoco.argLine</propertyName>
    </configuration>
</execution>
```

### 2. 在Surefire中引用自定义属性

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M5</version>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <!-- 使用JaCoCo的自定义属性名 -->
        <argLine>${jacoco.argLine} -Djava.io.tmpdir=C:\temp</argLine>
        <tempDir>${java.io.tmpdir}</tempDir>
    </configuration>
</plugin>
```

### 3. 初始化argLine属性（可选）

在 `properties` 中初始化，确保总是有值：

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <argLine></argLine>
</properties>
```

## 验证修复

运行测试后，应该看到：

```
[INFO] --- jacoco:0.8.7:prepare-agent (prepare-agent) @ blog ---
[INFO] jacoco.argLine set to -javaagent:...org.jacoco.agent...=destfile=...target\jacoco.exec...
```

这表明JaCoCo代理已正确配置。

## 完整配置示例

### pom.xml 中的关键配置

```xml
<properties>
    <springfox.version>2.7.0</springfox.version>
    <argLine></argLine>
</properties>

<build>
    <plugins>
        <!-- Surefire插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M5</version>
            <configuration>
                <forkCount>1</forkCount>
                <reuseForks>true</reuseForks>
                <argLine>${jacoco.argLine} -Djava.io.tmpdir=C:\temp</argLine>
                <tempDir>${java.io.tmpdir}</tempDir>
            </configuration>
        </plugin>
        
        <!-- JaCoCo插件 -->
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
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 运行测试和生成报告

```bash
# 运行测试
mvn clean test -Dtest=ApiAiControllerTest

# 生成覆盖率报告
mvn jacoco:report

# 或者一步完成
mvn clean test jacoco:report -Dtest=ApiAiControllerTest
```

## 预期结果

1. ✅ 测试成功运行
2. ✅ 生成 `target/jacoco.exec` 执行数据文件
3. ✅ 生成 `target/site/jacoco/index.html` 覆盖率报告

## 技术说明

### 为什么使用自定义属性名？

- **避免冲突**：Surefire和JaCoCo都使用 `argLine`，使用自定义名称可以避免冲突
- **更清晰**：`jacoco.argLine` 明确表示这是JaCoCo的属性
- **更灵活**：可以同时使用多个工具的argLine参数

### propertyName 的作用

`propertyName` 配置告诉JaCoCo将代理参数设置到哪个Maven属性中。默认是 `argLine`，我们改为 `jacoco.argLine`。

## 相关文件

- `pom.xml` - Maven配置文件
- `target/jacoco.exec` - JaCoCo执行数据文件
- `target/site/jacoco/index.html` - 覆盖率HTML报告

