# Banner 组件测试依赖说明

## 测试文件
- `Banner.spec.js` - Banner 组件的渲染测试

## 必要的依赖

### 1. 核心测试框架
```json
{
  "@vue/test-utils": "^1.3.0",
  "jest": "^26.6.3",
  "vue-jest": "^4.0.1"
}
```

### 2. Vue 相关依赖
```json
{
  "vue": "^2.6.14",
  "vuex": "^3.6.2",
  "vuetify": "^2.6.15"
}
```

### 3. Babel 配置
```json
{
  "@babel/core": "^7.28.5",
  "@babel/preset-env": "^7.28.5",
  "babel-jest": "^26.6.3"
}
```

### 4. 测试环境
```json
{
  "jsdom": "^16.7.0"
}
```

## Mock 配置

### 全局 Mock
- `$router` - Vue Router（路由跳转）
- `document.getElementById` - DOM 元素获取
- `setInterval` - 定时器
- `clearInterval` - 清除定时器

### Mock 数据
- `mockImages` - 轮播图数据数组，包含 id、title、avatar 字段

## 测试范围

### 仅测试组件渲染
- ✅ 组件正确渲染
- ✅ 轮播容器渲染
- ✅ 轮播列表渲染
- ✅ 控制点区域渲染
- ✅ 控制按钮区域渲染
- ✅ 底部标题区域渲染
- ✅ CSS 类名存在
- ✅ 空数据渲染
- ✅ 单个/多个图片渲染
- ✅ 初始数据正确
- ✅ 组件结构正确

### 不测试的功能
- ❌ 轮播逻辑（handleGo, handleStop）
- ❌ 切换逻辑（handleChange）
- ❌ 点击跳转（handleInfo）
- ❌ 计算属性（prevIndex, nextIndex）
- ❌ DOM 操作（mounted 中的样式设置）
- ❌ 定时器功能

## 运行测试

```bash
# 运行 Banner 组件测试
npm run test:banner

# 运行测试并生成覆盖率
npm test -- src/tests/unit/Banner.spec.js --coverage
```

## 注意事项

1. **使用 shallowMount**：只渲染当前组件，子组件被 stub 替换
2. **Mock DOM 操作**：`document.getElementById` 被 mock，避免真实 DOM 操作
3. **Mock 定时器**：`setInterval` 和 `clearInterval` 被 mock
4. **仅测试渲染**：不测试业务逻辑和交互功能

## 组件特点

Banner 组件是一个轮播图组件，具有以下特点：
- 接收 `images` prop（图片数组）
- 自动轮播功能（每 3 秒切换）
- 鼠标悬停停止轮播
- 支持左右切换
- 显示控制点和标题

## 如果遇到编译错误

如果遇到 `vue-template-es2015-compiler` 的编译错误，可以尝试：

1. **清除缓存**：
   ```bash
   npm cache clean --force
   rm -rf node_modules/.cache
   ```

2. **重新安装依赖**：
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **检查 vue-jest 版本**：
   确保 `vue-jest@4.0.1` 与 `jest@26.6.3` 兼容

