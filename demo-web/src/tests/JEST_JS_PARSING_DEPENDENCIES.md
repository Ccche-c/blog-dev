# Jest 测试中解析 JS 文件的依赖说明

## 必需的依赖

### 1. Babel 核心依赖（已安装）
```json
{
  "@babel/core": "^7.28.5",
  "@babel/preset-env": "^7.28.5",
  "babel-jest": "^26.6.3"
}
```

这些依赖用于将 ES6+ JavaScript 代码转换为 Jest 可以执行的代码。

### 2. Babel 配置文件（已存在）
`babel.config.js` 文件已存在，配置如下：

```javascript
module.exports = {
    presets: [
        ['@babel/preset-env', {
            targets: {
                node: 'current'
            }
        }]
    ]
}
```

### 3. Jest 配置（已配置）
在 `jest.config.js` 中已配置：

```javascript
transform: {
    '^.+\\.vue$': 'vue-jest',
    '^.+\\.jsx?$': 'babel-jest'  // ✅ 已配置 JS 文件转换
}
```

## 当前配置状态

### ✅ 已安装的依赖
- `@babel/core@^7.28.5` - Babel 核心库
- `@babel/preset-env@^7.28.5` - Babel 环境预设
- `babel-jest@^26.6.3` - Jest 的 Babel 转换器

### ✅ 已配置的文件
- `babel.config.js` - Babel 配置文件
- `jest.config.js` - Jest 配置文件（包含 JS 文件转换规则）

## 如果遇到 JS 文件解析错误

### 问题 1: "SyntaxError: Unexpected token"
**原因**：Babel 没有正确转换 ES6+ 语法

**解决方案**：
1. 确保 `babel.config.js` 存在且配置正确
2. 确保 `jest.config.js` 中的 `transform` 配置包含 `'^.+\\.jsx?$': 'babel-jest'`
3. 清除缓存并重新运行：
   ```bash
   npm cache clean --force
   rm -rf node_modules/.cache
   npm test -- --no-cache
   ```

### 问题 2: "Cannot find module '@babel/core'"
**原因**：缺少 Babel 核心依赖

**解决方案**：
```bash
npm install --save-dev @babel/core @babel/preset-env babel-jest
```

### 问题 3: "ReferenceError: regeneratorRuntime is not defined"
**原因**：缺少 async/await 支持

**解决方案**：
安装 `@babel/plugin-transform-runtime`：
```bash
npm install --save-dev @babel/plugin-transform-runtime @babel/runtime
```

然后在 `babel.config.js` 中添加：
```javascript
module.exports = {
    presets: [
        ['@babel/preset-env', {
            targets: {
                node: 'current'
            }
        }]
    ],
    plugins: [
        '@babel/plugin-transform-runtime'
    ]
}
```

### 问题 4: "Jest encountered an unexpected token" (JS 文件)
**原因**：`transformIgnorePatterns` 排除了需要转换的文件

**解决方案**：
检查 `jest.config.js` 中的 `transformIgnorePatterns`，确保不排除需要转换的 JS 文件：

```javascript
transformIgnorePatterns: [
    '/node_modules/(?!(vuetify|@kangc)/)'  // 只排除 node_modules，不排除项目文件
]
```

## 验证配置

运行以下命令验证配置是否正确：

```bash
# 运行测试，检查是否能正确解析 JS 文件
npm test

# 如果遇到问题，使用 --no-cache 清除缓存
npm test -- --no-cache
```

## 完整的依赖列表

### 核心测试依赖
```json
{
  "@vue/test-utils": "^1.3.0",
  "jest": "^26.6.3",
  "vue-jest": "^4.0.1",
  "jsdom": "^16.7.0"
}
```

### Babel 依赖（用于解析 JS 文件）
```json
{
  "@babel/core": "^7.28.5",
  "@babel/preset-env": "^7.28.5",
  "babel-jest": "^26.6.3"
}
```

### 可选依赖（如果需要 async/await 支持）
```json
{
  "@babel/plugin-transform-runtime": "^7.28.5",
  "@babel/runtime": "^7.28.5"
}
```

## 注意事项

1. **babel-jest 版本兼容性**：
   - `babel-jest@^26.6.3` 与 `jest@^26.6.3` 兼容
   - 确保版本匹配

2. **Babel 配置位置**：
   - Jest 会自动查找 `babel.config.js` 或 `.babelrc`
   - 确保配置文件在项目根目录

3. **转换规则**：
   - `'^.+\\.jsx?$': 'babel-jest'` 会转换所有 `.js` 和 `.jsx` 文件
   - Vue 文件由 `vue-jest` 处理

4. **缓存问题**：
   - 如果修改了 Babel 配置，可能需要清除 Jest 缓存
   - 使用 `--no-cache` 标志运行测试

