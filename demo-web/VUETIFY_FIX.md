# Vuetify "Multiple instances of Vue detected" 错误修复

## 问题描述

运行测试时出现错误：
```
[Vuetify] Multiple instances of Vue detected
See https://github.com/vuetifyjs/vuetify/issues/4068
```

## 原因

在测试文件中，直接在 `localVue` 上使用 `Vuetify` 会导致多个 Vue 实例冲突：
```javascript
const localVue = createLocalVue()
localVue.use(Vuetify)  // ❌ 这会导致错误
```

## 解决方案

### 1. 移除 `localVue.use(Vuetify)`

不要在 `localVue` 上直接使用 Vuetify，而是在每个测试中传入 Vuetify 实例。

**修复前：**
```javascript
const localVue = createLocalVue()
localVue.use(Vuex)
localVue.use(Vuetify)  // ❌ 移除这行
```

**修复后：**
```javascript
const localVue = createLocalVue()
localVue.use(Vuex)
// 注意：不要在这里使用 Vuetify，而是在每个测试中传入 Vuetify 实例
```

### 2. 在 `beforeEach` 中创建 Vuetify 实例

```javascript
beforeEach(() => {
    // 创建 Vuetify 实例（使用默认配置）
    vuetify = new Vuetify({
        // 可以在这里添加 Vuetify 配置
    })
    
    // ... 其他初始化代码
})
```

### 3. 在 `shallowMount` 时传入 `vuetify` 选项

```javascript
wrapper = shallowMount(NewlyArticle, {
    localVue,
    store,
    vuetify,  // ✅ 传入 Vuetify 实例
    mocks: {
        $router: mockRouter
    }
})
```

## 测试结果

修复后，所有 19 个测试用例都通过了：
- ✅ 组件渲染测试（3个）
- ✅ 文章列表显示测试（4个）
- ✅ 点击事件处理测试（3个）
- ✅ 响应式数据更新测试（1个）
- ✅ 边界情况测试（4个）
- ✅ 组件结构测试（2个）
- ✅ 数据绑定测试（2个）

## 注意事项

1. **Vue 警告**：测试中可能会出现 `<v-card> - did you register the component correctly?` 警告，这是正常的，因为 `shallowMount` 不会完全渲染子组件。这不影响测试结果。

2. **Vuetify 配置**：如果需要特定的 Vuetify 配置，可以在 `beforeEach` 中创建 Vuetify 实例时传入：
   ```javascript
   vuetify = new Vuetify({
       theme: {
           dark: true
       }
   })
   ```

3. **每个测试独立**：每个测试都会在 `beforeEach` 中创建新的 Vuetify 实例，确保测试之间不会相互影响。

## 相关文件

- `src/tests/unit/NewlyArticle.spec.js` - 测试文件（已修复）
- `src/tests/setup.js` - 测试环境设置文件

## 参考链接

- [Vuetify GitHub Issue #4068](https://github.com/vuetifyjs/vuetify/issues/4068)
- [Vue Test Utils 文档](https://vue-test-utils.vuejs.org/)
- [Vuetify 测试指南](https://vuetifyjs.com/en/getting-started/unit-testing/)

