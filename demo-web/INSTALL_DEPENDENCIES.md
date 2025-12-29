# 安装测试依赖

## 必需依赖安装命令

运行以下命令安装所有必需的测试依赖：

```bash
npm install --save-dev babel-jest @babel/preset-env @babel/core
```

## 完整安装步骤

1. **安装 Babel 相关依赖**：
   ```bash
   npm install --save-dev babel-jest @babel/preset-env @babel/core
   ```

2. **创建或更新 `.babelrc` 或 `babel.config.js`**（如果不存在）：
   
   创建 `babel.config.js`：
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

3. **运行测试**：
   ```bash
   npm test -- src/tests/unit/NewlyArticle.spec.js
   ```

## 为什么需要这些依赖？

- **babel-jest**: Jest 需要它来转换 ES6+ JavaScript 代码
- **@babel/preset-env**: Babel 预设，用于根据目标环境转换代码
- **@babel/core**: Babel 的核心库，babel-jest 的依赖

## 验证安装

安装完成后，运行：
```bash
npm list babel-jest @babel/preset-env @babel/core
```

应该能看到这些包的版本信息。

