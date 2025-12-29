# NewlyArticle 组件测试

## 测试文件
- `NewlyArticle.spec.js` - NewlyArticle 组件的完整测试套件

## 运行测试

### 运行单个测试文件
```bash
npm test -- src/tests/unit/NewlyArticle.spec.js
```

### 运行测试并生成覆盖率报告
```bash
npm test -- src/tests/unit/NewlyArticle.spec.js --coverage
```

或者使用快捷命令：
```bash
npm run test:coverage:newly-article
```

### 查看覆盖率报告
运行测试后，覆盖率报告会生成在 `coverage/` 目录下：
- HTML 报告：`coverage/lcov-report/index.html`
- LCOV 报告：`coverage/lcov.info`
- JSON 报告：`coverage/coverage-final.json`

## 测试覆盖范围

### 组件渲染测试
- ✅ 组件正确渲染
- ✅ 标题和图标显示
- ✅ "更多"链接显示

### 文章列表显示测试
- ✅ 空列表处理
- ✅ 单个文章显示
- ✅ 多个文章显示和编号
- ✅ 文章 key 绑定

### 点击事件处理测试
- ✅ handleClick 方法调用
- ✅ 路由跳转功能
- ✅ 不同文章 ID 处理

### 响应式数据更新测试
- ✅ Store 数据变化响应

### 边界情况测试
- ✅ 空标题处理
- ✅ null 标题处理
- ✅ undefined 标题处理
- ✅ 大量文章处理（100+）

### 组件结构测试
- ✅ CSS 类名正确性
- ✅ Vuetify 组件渲染

### 数据绑定测试
- ✅ 文章数据绑定
- ✅ v-for index 编号

## 代码覆盖率目标

- **Branches（分支）**: 70%
- **Functions（函数）**: 70%
- **Lines（行）**: 70%
- **Statements（语句）**: 70%

## 注意事项

1. 测试使用 `@vue/test-utils` 进行组件测试
2. Mock 了 Vuex store 和 Vue Router
3. 使用 Vuetify 的 shallowMount 进行浅渲染
4. 所有测试都是独立的，使用 `beforeEach` 和 `afterEach` 进行清理

## 测试依赖

- `@vue/test-utils` - Vue 组件测试工具
- `jest` - 测试框架
- `vue-jest` - Vue 文件转换器
- `babel-jest` - JavaScript 转换器

