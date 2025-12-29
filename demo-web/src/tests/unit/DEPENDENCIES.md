# 测试依赖安装指南

## 已安装的依赖

根据 `package.json`，以下依赖已经安装：

### 核心测试框架
- ✅ `jest` (^27.5.1) - 测试框架
- ✅ `@vue/test-utils` (^1.3.0) - Vue 组件测试工具
- ✅ `jsdom` (^27.3.0) - DOM 环境模拟
- ✅ `vue-jest` (^3.0.7) - Vue 文件转换器
- ✅ `vue-template-compiler` (^2.7.14) - Vue 模板编译

### Vue 相关
- ✅ `vue` (^2.6.14) - Vue 框架
- ✅ `vuex` (^3.6.2) - 状态管理
- ✅ `vuetify` (^2.6.15) - UI 框架
- ✅ `vue-router` (^3.6.5) - 路由

### Babel 相关（可能缺失）
- ❓ `babel-jest` - JavaScript 转换器（Jest 配置中使用）
- ❓ `@babel/preset-env` - Babel 预设（Jest 配置中使用）
- ❓ `@babel/core` - Babel 核心（babel-jest 需要）

## 需要安装的依赖

### 必需依赖

如果运行测试时出现错误，可能需要安装以下依赖：

```bash
# 安装 Babel 相关依赖（用于 JavaScript 转换）
npm install --save-dev babel-jest @babel/preset-env @babel/core
```

### 完整安装命令

```bash
# 一次性安装所有测试依赖
npm install --save-dev \
  babel-jest \
  @babel/preset-env \
  @babel/core
```

## 依赖说明

### babel-jest
- **用途**: 将 ES6+ JavaScript 代码转换为 Jest 可以运行的代码
- **版本**: 建议与 Jest 版本兼容（27.x）
- **必需性**: ⭐⭐⭐⭐⭐ 必需（Jest 配置中使用了 `babel-jest`）

### @babel/preset-env
- **用途**: 根据目标环境自动确定需要的 Babel 插件和 polyfills
- **版本**: 建议使用最新稳定版
- **必需性**: ⭐⭐⭐⭐ 高度推荐（Jest 配置的 globals 中使用了）

### @babel/core
- **用途**: Babel 的核心库，babel-jest 需要它来工作
- **版本**: 建议使用最新稳定版
- **必需性**: ⭐⭐⭐⭐⭐ 必需（babel-jest 的依赖）

## 验证安装

安装完成后，可以运行以下命令验证：

```bash
# 检查依赖是否安装成功
npm list babel-jest @babel/preset-env @babel/core

# 运行测试验证
npm test -- src/tests/unit/NewlyArticle.spec.js
```

## 可选依赖（用于增强功能）

如果需要更多测试功能，可以考虑安装：

```bash
# 快照序列化器（用于快照测试）
npm install --save-dev jest-serializer-vue

# CSS 模块 Mock（如果使用 CSS Modules）
npm install --save-dev identity-obj-proxy
```

## 依赖版本兼容性

- **Jest 27.x** 兼容：
  - `babel-jest`: ^27.0.0
  - `@babel/core`: ^7.0.0
  - `@babel/preset-env`: ^7.0.0

## 故障排除

如果安装后仍然出现问题：

1. **清除缓存并重新安装**：
   ```bash
   npm cache clean --force
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **检查 Node.js 版本**：
   - 建议使用 Node.js 14.x 或更高版本

3. **检查 Jest 配置**：
   - 确保 `jest.config.js` 中的配置正确
   - 检查 `package.json` 中的测试脚本

