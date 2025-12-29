const { defineConfig } = require('@vue/cli-service')

const NodePolyfillPlugin = require('node-polyfill-webpack-plugin')
let path = require('path');
function resolve(dir) {
  return path.join(__dirname, dir)
}
module.exports = defineConfig({
  publicPath: "/",
  lintOnSave: false,
  devServer: {
    historyApiFallback: true,
    port: 80,
    client: {
      overlay: {
        errors: true,
        warnings: false,
        // 忽略 ResizeObserver 错误
        // runtimeErrors: (error) => {
        //   const resizeObserverErr = /ResizeObserver loop completed with undelivered notifications/;
        //   if (resizeObserverErr.test(error.message)) {
        //     return false; // 不显示此错误
        //   }
        //   return true; // 显示其他错误
        // }
        runtimeErrors: false
      }
    }
  },
  configureWebpack: {
    plugins: [new NodePolyfillPlugin()],
  },
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@', resolve('src'))
      // key,value可以自行定义，比如.set('@@', resolve('src/components'))
      .set("assets", resolve("src/assets"))
      .set("components", resolve("src/components"))
      .set("utils", resolve("src/utils"))

      // 配置 svg-sprite-loader
      config.module
        .rule('svg')
        .exclude.add(resolve('src/icons'))
        .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  },
})
