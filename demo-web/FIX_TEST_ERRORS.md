# 修复测试错误指南

## 问题描述

运行测试时出现以下错误：
```
Requires Babel "^7.0.0-0", but was loaded with "6.26.3"
```

这是因为 `vue-jest` 3.x 版本使用的是旧版本的 `babel-core` (6.x)，而项目使用的是 `@babel/core` (7.x)。

## 解决方案

### 方案 1：更新 vue-jest（推荐）

更新 `vue-jest` 到支持 Babel 7 的版本：

```bash
npm install --save-dev vue-jest@^4.0.1
```

或者更新到最新版本：
```bash
npm install --save-dev vue-jest@latest
```

### 方案 2：安装 babel-core@6 作为兼容层（不推荐）

如果无法更新 vue-jest，可以安装旧版本的 babel-core：

```bash
npm install --save-dev babel-core@^6.26.3
```

## 已完成的配置

以下配置已经完成：

1. ✅ 创建了 `babel.config.js`
2. ✅ 创建了 `jest.config.js`
3. ✅ 创建了 `src/tests/setup.js`（使用 CommonJS 语法）
4. ✅ 创建了测试文件 `src/tests/unit/NewlyArticle.spec.js`
5. ✅ 创建了 Mock 文件

## 修复步骤

1. **更新 vue-jest**：
   ```bash
   npm install --save-dev vue-jest@^4.0.1
   ```

2. **运行测试**：
   ```bash
   npm test -- src/tests/unit/NewlyArticle.spec.js
   ```

3. **运行测试并生成覆盖率**：
   ```bash
   npm test -- src/tests/unit/NewlyArticle.spec.js --coverage
   ```

## 当前依赖状态

### 已安装
- ✅ `jest` (^27.5.1)
- ✅ `@vue/test-utils` (^1.3.0)
- ✅ `jsdom` (^27.3.0)
- ✅ `babel-jest` (^30.2.0)
- ✅ `@babel/core` (^7.28.5)
- ✅ `@babel/preset-env` (^7.28.5)
- ✅ `vue-template-compiler` (^2.7.14)

### 需要更新
- ❌ `vue-jest` (当前: ^3.0.7，需要: ^4.0.1 或更高)

## 验证修复

更新后运行：
```bash
npm test -- src/tests/unit/NewlyArticle.spec.js
```

应该能看到测试通过。

## 如果仍有问题

1. **清除缓存**：
   ```bash
   npm cache clean --force
   ```

2. **删除 node_modules 并重新安装**：
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **检查版本兼容性**：
   - Jest 27.x 需要 vue-jest 4.x 或 5.x
   - Babel 7.x 需要 vue-jest 4.x 或更高

