# Pagination 组件测试依赖说明

## 测试文件
- `Pagination.spec.js` - Pagination 组件的渲染测试

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
- `onPageChange` - 分页变化回调函数（被 mock）

### Props
- `page` - 当前页码（Number，默认 1）
- `total` - 总记录数（Number，默认 0）
- `pageSize` - 每页记录数（Number，默认 10）
- `onPageChange` - 页码变化回调（Function）

## 测试范围

### 组件渲染测试
- ✅ 组件正确渲染
- ✅ 分页容器渲染
- ✅ 分页列表渲染
- ✅ 上一页按钮渲染
- ✅ 下一页按钮渲染
- ✅ CSS 类名存在

### 分页按钮显示测试
- ✅ 显示页码按钮
- ✅ 显示省略号
- ✅ 标记当前页为 active
- ✅ 禁用上一页按钮（第一页）
- ✅ 禁用下一页按钮（最后一页）

### 空数据渲染测试
- ✅ 处理总数为 0
- ✅ 处理单个页面
- ✅ 处理多个页面

### 初始数据测试
- ✅ 初始化正确的默认数据
- ✅ 使用默认的 page 值
- ✅ 使用默认的 pageSize 值

### 组件结构测试
- ✅ 正确渲染组件结构
- ✅ 正确使用 v-for 渲染页码

### 响应式数据更新测试
- ✅ 响应 page prop 的变化
- ✅ 响应 total prop 的变化

### 边界情况测试
- ✅ 处理 page 为 0
- ✅ 处理 pageSize 为 0
- ✅ 处理大量页面

## 运行测试

```bash
# 运行 Pagination 组件测试
npm run test:pagination

# 运行测试并生成覆盖率
npm test -- src/tests/unit/Pagination.spec.js --coverage
```

## 注意事项

1. **使用 shallowMount**：只渲染当前组件，子组件被 stub 替换
2. **仅测试渲染**：不测试业务逻辑和交互功能
3. **Mock 回调函数**：`onPageChange` 被 mock，避免真实回调执行

## 组件特点

Pagination 组件是一个分页组件，具有以下特点：
- 接收 `page`、`total`、`pageSize`、`onPageChange` props
- 自动计算总页数
- 智能显示页码（少于 5 页显示全部，否则显示省略号）
- 支持上一页/下一页导航
- 支持点击页码跳转

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

