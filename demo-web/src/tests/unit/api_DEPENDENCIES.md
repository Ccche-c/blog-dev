# api/index.js API 函数测试依赖说明

## 测试文件
- `api.spec.js` - api/index.js 中所有 API 函数的测试

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

### 工具函数 Mock
- `@/utils/request` - HTTP 请求工具函数（被 mock）

## 测试范围

### 文章相关 API（11个函数）
- ✅ fetchArticleList - 获取文章列表
- ✅ getUserArticles - 获取用户文章列表
- ✅ searchArticle - 搜索文章
- ✅ articleInfo - 获取文章信息
- ✅ articleLike - 文章点赞
- ✅ archive - 归档
- ✅ newArticle - 最新文章
- ✅ addOrUpdateArticle - 添加/编辑文章
- ✅ getArticleDetail - 获取文章详情
- ✅ pubOrShelfArticle - 发布/下架文章
- ✅ deleteArticle - 删除文章

### 标签相关 API（1个函数）
- ✅ fetchTagList - 获取标签列表

### 评论相关 API（2个函数）
- ✅ featchComments - 获取评论
- ✅ postComment - 发表评论

### 链接相关 API（2个函数）
- ✅ featchLinks - 获取链接列表
- ✅ addLink - 添加链接

### 首页相关 API（1个函数）
- ✅ featchHomeData - 获取首页数据

### 网站信息相关 API（1个函数）
- ✅ getWebSiteInfo - 获取网站信息

### 留言相关 API（2个函数）
- ✅ listMessage - 获取留言列表
- ✅ addMessage - 添加留言

### 用户相关 API（11个函数）
- ✅ emailLogin - 邮箱登录
- ✅ qqLogin - QQ 登录
- ✅ gitEELogin - Gitee 登录
- ✅ weiboLogin - 微博登录
- ✅ logout - 退出登录
- ✅ getWecahtLoginQr - 获取微信登录二维码
- ✅ wxIsLogin - 微信登录状态检查
- ✅ updateUserInfo - 更新用户信息
- ✅ getUserInfo - 获取用户信息
- ✅ sendEmailCode - 发送邮箱验证码
- ✅ emailRegister - 邮箱注册

### 分类相关 API（1个函数）
- ✅ featchCategory - 获取分类列表

### 相册相关 API（2个函数）
- ✅ featchPhotoAlbum - 获取相册列表
- ✅ featchPhoto - 获取照片列表

### 支付相关 API（1个函数）
- ✅ getPayUrl - 获取支付链接

### AI 相关 API（1个函数）
- ✅ aiChat - AI 聊天

### 参数传递测试（4个测试）
- ✅ 正确传递 GET 请求的 params
- ✅ 正确传递 POST 请求的 data
- ✅ 正确传递 PUT 请求的 data
- ✅ 正确传递 DELETE 请求的 params

### 边界情况测试（4个测试）
- ✅ 处理空参数
- ✅ 处理 null 参数
- ✅ 处理空对象 data
- ✅ 处理复杂对象 data

## 运行测试

```bash
# 运行 api/index.js 测试
npm run test:api

# 运行测试并生成覆盖率
npm test -- src/tests/unit/api.spec.js --coverage
```

## 注意事项

1. **Mock request 函数**：`@/utils/request` 被 mock，避免真实的 HTTP 请求
2. **测试 API 调用**：只测试 API 函数是否正确调用 `request` 并传递正确的参数
3. **不测试网络请求**：不测试实际的网络请求和响应处理
4. **纯函数测试**：这些是纯 JavaScript 函数，不需要 Vue 组件测试工具

## API 函数说明

### 文章相关
- `fetchArticleList(params)` - GET `/v1/article/list`
- `getUserArticles(params)` - GET `/v1/user/article/list`
- `searchArticle(params)` - GET `/v1/article/search`
- `articleInfo(id)` - GET `/v1/article/info`
- `articleLike(id)` - GET `/v1/article/like`
- `archive()` - GET `/v1/article/archive`
- `newArticle()` - GET `/v1/upToDateArticle`
- `addOrUpdateArticle(data)` - POST `/system/article/add`
- `getArticleDetail(id)` - GET `/v1/article/info`
- `pubOrShelfArticle(data)` - POST `/system/article/pubOrShelf`
- `deleteArticle(id)` - DELETE `/system/article/delete`

### 用户相关
- `emailLogin(data)` - POST `/v1/user/emailLogin`
- `qqLogin(data)` - POST `v1/user/login`
- `gitEELogin(code)` - GET `v1/user/gitEELogin`
- `weiboLogin(code)` - GET `v1/user/weiboLogin`
- `logout()` - GET `/logout`
- `getWecahtLoginQr()` - GET `/v1/user/wxQr`
- `wxIsLogin(id)` - GET `/v1/user/wx/is_login`
- `updateUserInfo(data)` - PUT `/v1/user/`
- `getUserInfo()` - GET `/v1/user/info`
- `sendEmailCode(email)` - GET `/v1/user/sendEmailCode`
- `emailRegister(data)` - POST `/v1/user/register`

### 其他
- `fetchTagList()` - GET `/v1/tag/`
- `featchComments(params)` - GET `/v1/comment/selectCommentByArticleId`
- `postComment(data)` - POST `/v1/comment/`
- `featchLinks()` - GET `/v1/link/selectLinkList`
- `addLink(data)` - POST `/v1/link/`
- `featchHomeData()` - GET `/v1/`
- `getWebSiteInfo()` - GET `/v1/webSiteInfo`
- `listMessage()` - GET `/v1/message/list`
- `addMessage(data)` - POST `/v1/message/`
- `featchCategory()` - GET `/v1/category/list`
- `featchPhotoAlbum()` - GET `/v1/photoAlbum/`
- `featchPhoto(params)` - GET `/v1/photo/`
- `getPayUrl(params)` - GET `/v1/sponsor/createOrder`
- `aiChat(data)` - POST `/v1/ai/chat`

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

