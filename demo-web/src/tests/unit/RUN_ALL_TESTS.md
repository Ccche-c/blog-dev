# 运行所有测试代码指南

## 快速开始

### 运行所有测试（不生成覆盖率）

```bash
npm test
```

或者：

```bash
npm run test:all
```

或者：

```bash
npm run test:unit
```

### 运行所有测试并生成覆盖率报告

```bash
npm run test:coverage
```

或者：

```bash
npm run test:coverage:all
```

## 测试文件匹配规则

根据 `jest.config.js` 中的配置，Jest 会自动运行所有匹配以下模式的文件：

```javascript
testMatch: [
    '**/tests/unit/**/*.spec.(js|jsx|ts|tsx)|**/__tests__/*.(js|jsx|ts|tsx)'
]
```

这意味着会运行：
- `src/tests/unit/**/*.spec.js` - 所有 `.spec.js` 文件
- `src/tests/unit/**/*.spec.jsx` - 所有 `.spec.jsx` 文件
- `src/tests/unit/**/*.spec.ts` - 所有 `.spec.ts` 文件
- `src/tests/unit/**/*.spec.tsx` - 所有 `.spec.tsx` 文件
- `**/__tests__/*.js` - 所有 `__tests__` 目录下的测试文件

## 当前测试文件

根据项目结构，以下测试文件会被运行：

1. `src/tests/unit/NewlyArticle.spec.js` - NewlyArticle 组件测试
2. `src/tests/unit/ArticleList.spec.js` - ArticleList 组件测试

## 可用的测试命令

### 基本命令

| 命令 | 说明 |
|------|------|
| `npm test` | 运行所有测试 |
| `npm run test:all` | 运行所有测试（别名） |
| `npm run test:unit` | 运行所有单元测试 |
| `npm run test:watch` | 监听模式运行测试（文件变化时自动重新运行） |

### 单个测试文件

| 命令 | 说明 |
|------|------|
| `npm run test:newly-article` | 只运行 NewlyArticle 测试 |
| `npm run test:list-article` | 只运行 ArticleList 测试 |

### 覆盖率命令

| 命令 | 说明 |
|------|------|
| `npm run test:coverage` | 运行所有测试并生成覆盖率 |
| `npm run test:coverage:all` | 运行所有测试并生成覆盖率（别名） |
| `npm run test:coverage:newly-article` | 只运行 NewlyArticle 测试并生成覆盖率 |
| `npm run test:coverage:list-article` | 只运行 ArticleList 测试并生成覆盖率 |

## 覆盖率报告

运行 `npm run test:coverage` 后，会生成：

1. **终端输出**：显示覆盖率摘要
2. **HTML 报告**：`coverage/index.html` - 详细的 HTML 覆盖率报告
3. **LCOV 报告**：`coverage/lcov.info` - 用于 CI/CD 集成
4. **JSON 报告**：`coverage/coverage-final.json` - 机器可读的覆盖率数据

### 查看 HTML 覆盖率报告

```bash
# 在浏览器中打开
open coverage/index.html  # macOS
start coverage/index.html  # Windows
xdg-open coverage/index.html  # Linux
```

## 覆盖率阈值

根据 `jest.config.js` 配置，覆盖率阈值设置为：

- **Statements（语句）**: 70%
- **Branches（分支）**: 70%
- **Functions（函数）**: 70%
- **Lines（行）**: 70%

如果覆盖率低于这些阈值，测试会失败。

## 监听模式

使用监听模式可以在文件变化时自动重新运行测试：

```bash
npm run test:watch
```

在监听模式下，你可以：
- 按 `a` 运行所有测试
- 按 `f` 只运行失败的测试
- 按 `p` 按文件名模式过滤
- 按 `t` 按测试名称模式过滤
- 按 `q` 退出监听模式

## 示例输出

运行 `npm test` 的典型输出：

```
PASS  src/tests/unit/NewlyArticle.spec.js
PASS  src/tests/unit/ArticleList.spec.js

Test Suites: 2 passed, 2 total
Tests:       70 passed, 70 total
Snapshots:   0 total
Time:        5.189 s
Ran all test suites.
```

运行 `npm run test:coverage` 的典型输出：

```
PASS  src/tests/unit/NewlyArticle.spec.js
PASS  src/tests/unit/ArticleList.spec.js

-------------------|---------|----------|---------|---------|-------------------
File               | % Stmts | % Branch | % Funcs | % Lines | Uncovered Line #s
-------------------|---------|----------|---------|---------|-------------------
All files          |   85.71 |    75.00 |   83.33 |   85.71 |
 NewlyArticle.vue  |   85.71 |    75.00 |   83.33 |   85.71 |
 ArticleList.vue   |   85.71 |    75.00 |   83.33 |   85.71 |
-------------------|---------|----------|---------|---------|-------------------

Test Suites: 2 passed, 2 total
Tests:       70 passed, 70 total
Snapshots:   0 total
Time:        6.234 s
Ran all test suites.
```

## 故障排除

### 测试不运行

1. 检查测试文件是否匹配 `testMatch` 模式
2. 确保测试文件在正确的目录中
3. 检查文件扩展名是否正确（`.spec.js`）

### 覆盖率不显示

1. 确保在 `jest.config.js` 的 `collectCoverageFrom` 中添加了要测试的文件
2. 使用 `--coverage` 标志运行测试
3. 检查 `coverage` 目录是否生成

### 测试运行缓慢

1. 使用 `--maxWorkers=2` 限制并行工作进程数
2. 使用 `--bail` 在第一个失败时停止
3. 使用单个测试文件命令而不是运行所有测试

