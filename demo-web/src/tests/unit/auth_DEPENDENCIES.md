# auth.js 工具函数测试依赖说明

## 测试文件
- `auth.spec.js` - auth.js 工具函数的测试

## 必要的依赖

### 1. 核心测试框架
```json
{
  "jest": "^26.6.3",
  "babel-jest": "^26.6.3"
}
```

### 2. Babel 配置
```json
{
  "@babel/core": "^7.28.5",
  "@babel/preset-env": "^7.28.5"
}
```

### 3. 测试环境
```json
{
  "jsdom": "^16.7.0"
}
```

## Mock 配置

### 库 Mock
- `js-cookie` - Cookie 操作库（被 mock）

### 全局 Mock
- `window.sessionStorage` - 浏览器 sessionStorage API（被 mock）

## 测试范围

### getUser 函数测试
- ✅ 从 Cookie 获取用户信息
- ✅ 返回 null 当 Cookie 不存在
- ✅ 返回 null 当 Cookie 值为 undefined
- ✅ 正确解析 JSON 字符串
- ✅ 处理无效的 JSON 字符串

### setUser 函数测试
- ✅ 设置用户信息到 Cookie
- ✅ 使用正确的过期时间
- ✅ 能够设置不同的用户数据

### removeUser 函数测试
- ✅ 删除用户 Cookie
- ✅ 能够多次调用

### setSkin 函数测试
- ✅ 设置皮肤到 sessionStorage
- ✅ 能够设置不同的皮肤值
- ✅ 处理空字符串

### getSkin 函数测试
- ✅ 从 sessionStorage 获取皮肤
- ✅ 返回 null 当皮肤不存在
- ✅ 返回正确的皮肤值

### setUrl 函数测试
- ✅ 设置 URL 到 sessionStorage
- ✅ 能够设置不同的 URL
- ✅ 处理空字符串

### getUrl 函数测试
- ✅ 从 sessionStorage 获取 URL
- ✅ 返回 null 当 URL 不存在
- ✅ 返回正确的 URL

### hasAuth 函数测试
- ✅ 返回 true 当权限存在
- ✅ 返回 true 当权限部分匹配
- ✅ 返回 false 当权限不存在
- ✅ 返回 false 当权限数组为空
- ✅ 处理多个匹配的情况
- ✅ 处理空权限字符串
- ✅ 处理不匹配的情况

### 边界情况测试
- ✅ getUser 处理 null 值
- ✅ setUser 处理对象数据
- ✅ hasAuth 处理 undefined 权限
- ✅ hasAuth 处理 null 权限

## 运行测试

```bash
# 运行 auth.js 测试
npm run test:auth

# 运行测试并生成覆盖率
npm test -- src/tests/unit/auth.spec.js --coverage
```

## 注意事项

1. **Mock js-cookie**：`js-cookie` 库被 mock，避免真实的 Cookie 操作
2. **Mock sessionStorage**：`window.sessionStorage` 被 mock，避免浏览器 API 依赖
3. **纯函数测试**：这些是纯 JavaScript 函数，不需要 Vue 组件测试工具

## 函数说明

### getUser()
从 Cookie 中获取用户信息，如果存在则解析 JSON，否则返回 null。

### setUser(value)
将用户信息保存到 Cookie，过期时间为 1/24 天（1小时）。

### removeUser()
从 Cookie 中删除用户信息。

### setSkin(value)
将皮肤值保存到 sessionStorage。

### getSkin()
从 sessionStorage 获取皮肤值。

### setUrl(value)
将 URL 保存到 sessionStorage。

### getUrl()
从 sessionStorage 获取 URL。

### hasAuth(perms, perm)
检查权限数组中是否包含指定的权限（使用 indexOf 进行部分匹配）。

## 如果遇到编译错误

如果遇到 JS 文件解析错误，确保：

1. **Babel 配置正确**：
   - `babel.config.js` 存在且配置正确
   - `jest.config.js` 中的 `transform` 包含 `'^.+\\.jsx?$': 'babel-jest'`

2. **依赖已安装**：
   ```bash
   npm install --save-dev @babel/core @babel/preset-env babel-jest
   ```

3. **清除缓存**：
   ```bash
   npm cache clean --force
   rm -rf node_modules/.cache
   npm test -- --no-cache
   ```

