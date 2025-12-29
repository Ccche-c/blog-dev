# Jest 版本冲突修复说明

## 问题

安装依赖时出现错误：
```
ERESOLVE unable to resolve dependency tree
vue-jest@4.0.1 requires jest@"26.x"
但项目中使用的是 jest@^27.5.1
```

## 原因

`vue-jest@4.0.1` 的 peer dependency 要求 `jest` 版本为 `26.x`，但项目中使用的是 `jest@^27.5.1`，版本不兼容。

## 解决方案

已将所有相关依赖降级到与 `jest@26.x` 兼容的版本：

### 已更新的依赖

| 包 | 原版本 | 新版本 | 说明 |
|---|---|---|---|
| jest | ^27.5.1 | ^26.6.3 | 测试框架（降级到 26.x） |
| jsdom | ^27.3.0 | ^16.7.0 | DOM 环境（与 jest 26 兼容） |
| babel-jest | ^30.2.0 | ^26.6.3 | Babel 转换器（与 jest 26 兼容） |

### 版本兼容性

- ✅ `jest@26.6.3` - 与 `vue-jest@4.0.1` 兼容
- ✅ `babel-jest@26.6.3` - 与 `jest@26.x` 兼容
- ✅ `jsdom@16.7.0` - 与 `jest@26.x` 兼容
- ✅ `@babel/core@7.x` - 与 `babel-jest@26.x` 兼容

## 安装步骤

1. **删除旧的依赖**（推荐）：
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

## 替代方案（如果需要 Jest 27+）

如果您需要使用 Jest 27 的新特性，可以考虑：

1. **升级到 @vue/vue2-jest**（支持 Jest 27+）：
   ```bash
   npm uninstall vue-jest
   npm install --save-dev @vue/vue2-jest@28
   ```

2. **更新 jest.config.js**：
   ```javascript
   transform: {
       '^.+\\.vue$': '@vue/vue2-jest',
       '^.+\\.jsx?$': 'babel-jest'
   }
   ```

3. **恢复 jest 到 27.x**：
   ```json
   "jest": "^27.5.1",
   "jsdom": "^27.3.0",
   "babel-jest": "^27.5.1"
   ```

## 注意事项

- Jest 26.x 是稳定版本，功能完整
- 如果项目不需要 Jest 27 的新特性，降级到 26.x 是最简单的解决方案
- 所有测试代码无需修改，完全兼容

