# 快速修复命令

## 一键修复

运行以下命令修复所有问题：

```bash
# 更新 vue-jest 到支持 Babel 7 的版本
npm install --save-dev vue-jest@^4.0.1

# 运行测试验证
npm test -- src/tests/unit/NewlyArticle.spec.js
```

## 如果更新后仍有问题

```bash
# 清除缓存
npm cache clean --force

# 重新安装依赖
rm -rf node_modules package-lock.json
npm install

# 再次运行测试
npm test -- src/tests/unit/NewlyArticle.spec.js
```

