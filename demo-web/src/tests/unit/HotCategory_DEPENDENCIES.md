# HotCategory 组件测试依赖说明

## 测试文件
- `HotCategory.spec.js` - HotCategory 组件的渲染测试

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

### Mock 数据
- `mockCategoryList` - 分类数据数组，包含 id、name、avatar 字段

## 测试范围

### 组件渲染测试
- ✅ 组件正确渲染
- ✅ 标题区域渲染
- ✅ "全部分类"链接渲染
- ✅ 分类列表容器渲染
- ✅ CSS 类名存在

### 分类列表显示测试
- ✅ 显示空列表
- ✅ 显示单个分类
- ✅ 显示多个分类
- ✅ 显示分类名称
- ✅ 显示分类图片
- ✅ 正确绑定分类数据

### 点击事件处理测试
- ✅ 调用 handleClike 方法
- ✅ 使用正确的分类 ID 和名称进行路由跳转
- ✅ handleClike 方法处理不同的分类

### 响应式数据更新测试
- ✅ 响应 categoryList prop 的变化

### 边界情况测试
- ✅ 处理空的分类列表
- ✅ 处理空名称的分类
- ✅ 处理大量分类的情况

### 组件结构测试
- ✅ 正确渲染组件结构
- ✅ 正确使用 v-for 渲染分类列表

## 运行测试

```bash
# 运行 HotCategory 组件测试
npm run test:hotcategory

# 运行测试并生成覆盖率
npm test -- src/tests/unit/HotCategory.spec.js --coverage
```

## 注意事项

1. **使用 shallowMount**：只渲染当前组件，子组件被 stub 替换
2. **仅测试渲染**：不测试业务逻辑和交互功能
3. **Mock 路由**：`$router` 被 mock，避免真实路由跳转

## 组件特点

HotCategory 组件是一个热门分类展示组件，具有以下特点：
- 接收 `categoryList` prop（分类数组）
- 显示分类图片和名称
- 点击分类项跳转到分类页面
- 响应式布局（移动端和桌面端）

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

