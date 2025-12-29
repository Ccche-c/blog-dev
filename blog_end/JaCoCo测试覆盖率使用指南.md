# JaCoCo测试覆盖率使用指南

## 一、JaCoCo插件简介

JaCoCo（Java Code Coverage）是一个开源的Java代码覆盖率工具，可以统计代码的测试覆盖率，包括：
- **行覆盖率**（Line Coverage）
- **分支覆盖率**（Branch Coverage）
- **方法覆盖率**（Method Coverage）
- **类覆盖率**（Class Coverage）

## 二、插件配置说明

### 已配置的执行阶段

1. **prepare-agent**：在测试执行前准备JaCoCo代理
2. **report**：在测试执行后生成覆盖率报告
3. **check**：检查覆盖率是否达到设定的阈值

### 覆盖率阈值

当前配置的覆盖率阈值：
- **包级别**：行覆盖率 ≥ 50%
- **类级别**：行覆盖率 ≥ 50%

可以根据项目需要调整这些阈值。

### 排除的文件

以下类型的文件已配置为排除在覆盖率统计之外：
- 实体类（`entity`包）
- DTO类（`dto`包）
- VO类（`vo`包）
- 枚举类（`enums`包）
- 配置类（`config`包）
- 常量类（`Constants`、`FieldConstants`、`RedisConstants`、`SocialLoginConst`）
- Application主类

## 三、使用方法

### 1. 运行测试并生成覆盖率报告

```bash
# 清理并运行测试，自动生成覆盖率报告
mvn clean test

# 或者只运行测试（不清理）
mvn test
```

### 2. 查看覆盖率报告

测试执行完成后，覆盖率报告会自动生成在：

```
target/site/jacoco/index.html
```

**打开方式**：
1. 在文件管理器中导航到 `target/site/jacoco/` 目录
2. 双击 `index.html` 文件，在浏览器中打开
3. 或者使用IDE直接打开该文件

### 3. 只生成覆盖率报告（不运行测试）

如果测试已经运行过，可以只生成报告：

```bash
mvn jacoco:report
```

### 4. 检查覆盖率阈值

检查覆盖率是否达到配置的阈值：

```bash
mvn jacoco:check
```

如果覆盖率未达到阈值，构建会失败。

### 5. 生成聚合报告（多模块项目）

如果是多模块项目，可以生成聚合报告：

```bash
mvn jacoco:report-aggregate
```

## 四、覆盖率报告解读

### 报告页面说明

1. **总览页面**（Overview）
   - 显示所有包的覆盖率统计
   - 包括行覆盖率、分支覆盖率、方法覆盖率等

2. **包级别报告**（Package）
   - 显示每个包的覆盖率详情
   - 可以点击包名查看包内类的覆盖率

3. **类级别报告**（Class）
   - 显示每个类的覆盖率详情
   - 可以点击类名查看源代码级别的覆盖率

4. **源代码视图**（Source）
   - 显示源代码，并用颜色标记：
     - **绿色**：已覆盖的代码
     - **红色**：未覆盖的代码
     - **黄色**：部分覆盖的代码（分支）

### 覆盖率指标说明

- **行覆盖率（Line Coverage）**：执行过的代码行数占总行数的比例
- **分支覆盖率（Branch Coverage）**：执行过的分支数占总分支数的比例
- **方法覆盖率（Method Coverage）**：执行过的方法数占总方法数的比例
- **类覆盖率（Class Coverage）**：执行过的类数占总类数的比例

## 五、覆盖率目标建议

### 推荐覆盖率目标

- **行覆盖率**：≥ 80%
- **分支覆盖率**：≥ 70%
- **方法覆盖率**：≥ 80%
- **类覆盖率**：≥ 80%

### 调整覆盖率阈值

如果需要修改覆盖率阈值，编辑 `pom.xml` 中的JaCoCo插件配置：

```xml
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>  <!-- 修改为80% -->
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

## 六、常见问题

### Q1: 报告中没有显示某些类

**A**: 检查这些类是否在排除列表中。如果需要统计这些类的覆盖率，从 `<excludes>` 配置中移除相应的排除规则。

### Q2: 覆盖率报告显示为0%

**A**: 可能的原因：
1. 测试没有实际执行
2. 测试类没有正确运行
3. 检查 `target/site/jacoco/` 目录是否存在报告文件

**解决方案**：
```bash
# 清理后重新运行
mvn clean test
```

### Q3: 如何排除更多文件

**A**: 在JaCoCo插件的 `<excludes>` 配置中添加更多排除规则：

```xml
<excludes>
    <exclude>**/your/package/**</exclude>
    <exclude>**/YourClass.class</exclude>
</excludes>
```

### Q4: 如何只统计特定包的覆盖率

**A**: 使用 `<includes>` 配置：

```xml
<configuration>
    <includes>
        <include>**/controller/**</include>
        <include>**/service/**</include>
    </includes>
</configuration>
```

### Q5: 覆盖率检查失败，但不想修改代码

**A**: 临时跳过覆盖率检查：

```bash
mvn test -Djacoco.skip=true
```

或者调整覆盖率阈值。

## 七、集成到CI/CD

### GitHub Actions示例

```yaml
name: Tests and Coverage
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run tests with coverage
        run: mvn clean test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload coverage report
        uses: actions/upload-artifact@v2
        with:
          name: coverage-report
          path: target/site/jacoco/
```

### Jenkins示例

在Jenkins Pipeline中添加：

```groovy
stage('Test and Coverage') {
    steps {
        sh 'mvn clean test'
        sh 'mvn jacoco:report'
        publishHTML([
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'JaCoCo Coverage Report'
        ])
    }
}
```

## 八、最佳实践

1. **定期检查覆盖率**：在每次代码提交后运行覆盖率检查
2. **设置合理的阈值**：根据项目实际情况设置覆盖率目标
3. **关注关键代码**：优先保证核心业务逻辑的高覆盖率
4. **排除不必要的类**：排除实体类、DTO等不需要测试的类
5. **持续改进**：逐步提高覆盖率，而不是一次性要求100%

## 九、相关资源

- [JaCoCo官方文档](https://www.jacoco.org/jacoco/trunk/doc/)
- [JaCoCo Maven插件文档](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [代码覆盖率最佳实践](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

---

**注意**：覆盖率报告生成在 `target/site/jacoco/index.html`，每次运行 `mvn clean` 会清理该目录，需要重新运行测试生成报告。

