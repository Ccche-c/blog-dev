# 依赖冲突修复说明

## 问题

安装依赖时出现错误：
```
ERESOLVE unable to resolve dependency tree
vue-jest@4.0.1 requires babel-jest@">= 24 < 27"
但项目中使用的是 babel-jest@30.2.0
```

## 原因

`vue-jest@4.0.1` 的 peer dependency 要求 `babel-jest` 版本在 `>= 24 < 27` 范围内，但项目中使用的是 `babel-jest@30.2.0`，版本不兼容。

## 解决方案

已将 `babel-jest` 从 `^30.2.0` 降级到 `^26.6.3`，这个版本：
- ✅ 满足 `vue-jest@4.0.1` 的要求（`>= 24 < 27`）
- ✅ 与 `jest@27.5.1` 兼容
- ✅ 与 `@babel/core@7.x` 兼容

## 安装步骤

1. **删除旧的依赖**（可选，但推荐）：
   ```bash
   rm -rf node_modules package-lock.json
   ```

2. **安装依赖**：
   ```bash
   npm install
   ```

3. **如果仍有冲突，使用 legacy peer deps**：
   ```bash
   npm install --legacy-peer-deps
   ```

## 验证

安装完成后，运行测试验证：
```bash
npm test -- src/tests/unit/NewlyArticle.spec.js
```

## 版本兼容性

| 包 | 版本 | 说明 |
|---|---|---|
| jest | ^27.5.1 | 测试框架 |
| babel-jest | ^26.6.3 | Jest 的 Babel 转换器（与 Jest 27 兼容） |
| vue-jest | ^4.0.1 | Vue 文件转换器（需要 babel-jest < 27） |
| @babel/core | ^7.28.5 | Babel 核心（与 babel-jest 26.x 兼容） |

## 如果问题仍然存在

如果安装后仍有问题，可以尝试：

1. **使用 npm 的 legacy peer deps 模式**：
   ```bash
   npm install --legacy-peer-deps
   ```

2. **或者使用 yarn**（如果已安装）：
   ```bash
   yarn install
   ```

3. **检查是否有其他冲突**：
   ```bash
   npm ls babel-jest vue-jest
   ```

