import Vue from 'vue'
import App from './App.vue'
import config from "./assets/js/config";
import './assets/font/iconfont.css'
import './assets/font/iconfont.js'
import InfiniteLoading from "vue-infinite-loading";
import store from './store'
import animated from 'animate.css'
import 'wowjs/css/libs/animate.css'
import wow from 'wowjs'
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';
import createLineNumbertPlugin from '@kangc/v-md-editor/lib/plugins/line-number/index';
import createCopyCodePlugin from '@kangc/v-md-editor/lib/plugins/copy-code/index';
import '@kangc/v-md-editor/lib/plugins/copy-code/copy-code.css';
import hljs from 'highlight.js';
import "@/icons";
import Toast from "./components/toast/index";
import vuetify from '@/plugins/vuetify'
import { vueBaberrage } from 'vue-baberrage'
import { setSkin } from '@/utils/skin'
import jsCookie from 'js-cookie'
Vue.prototype.$cookie = jsCookie;  // 在页面里可直接用 this.$cookie 调用
Vue.prototype.$setSkin = setSkin;

Vue.use(vueBaberrage)
VMdPreview.use(githubTheme, {
  Hljs: hljs,
});
VMdPreview.use(createLineNumbertPlugin())
VMdPreview.use(createCopyCodePlugin())
Vue.use(VMdPreview);
Vue.use(Toast);
Vue.prototype.$wow = wow
Vue.use(InfiniteLoading);

Vue.config.productionTip = false
Vue.prototype.config = config;
import router from './router'

new Vue({
  store,
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app')

// 全面抑制 ResizeObserver 警告（这是一个已知的浏览器警告，不影响功能）
if (typeof window !== 'undefined') {
  // 1. 捕获全局错误事件
  const resizeObserverErr = /ResizeObserver loop completed with undelivered notifications/;
  
  window.addEventListener('error', (e) => {
    if (resizeObserverErr.test(e.message)) {
      e.stopImmediatePropagation();
      e.preventDefault();
      return false;
    }
  }, true);

  // 2. 捕获未处理的 Promise rejection
  window.addEventListener('unhandledrejection', (e) => {
    if (e.reason && typeof e.reason === 'string' && resizeObserverErr.test(e.reason)) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    }
    if (e.reason && e.reason.message && resizeObserverErr.test(e.reason.message)) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    }
  }, true);

  // 3. 重写 console.error 来过滤 ResizeObserver 警告
  const originalError = console.error;
  console.error = function(...args) {
    const errorMessage = args.join(' ');
    if (resizeObserverErr.test(errorMessage)) {
      // 忽略 ResizeObserver 警告
      return;
    }
    originalError.apply(console, args);
  };

  // 4. 重写 console.warn 来过滤 ResizeObserver 警告
  const originalWarn = console.warn;
  console.warn = function(...args) {
    const warnMessage = args.join(' ');
    if (resizeObserverErr.test(warnMessage)) {
      // 忽略 ResizeObserver 警告
      return;
    }
    originalWarn.apply(console, args);
  };

  // 5. 拦截全局 onerror 处理器（用于 webpack-dev-server overlay）
  const originalOnError = window.onerror;
  window.onerror = function(message, source, lineno, colno, error) {
    if (resizeObserverErr.test(message) || 
        (error && error.message && resizeObserverErr.test(error.message))) {
      return true; // 阻止默认错误处理（包括 webpack overlay）
    }
    if (originalOnError) {
      return originalOnError.call(this, message, source, lineno, colno, error);
    }
    return false;
  };

  // 6. 拦截 Vue 的错误处理
  Vue.config.errorHandler = function(err, vm, info) {
    if (err && err.message && resizeObserverErr.test(err.message)) {
      // 忽略 ResizeObserver 错误
      return;
    }
    // 其他错误正常处理（可选，如果需要记录其他错误）
    // console.error('Vue Error:', err, info);
  };
}

router.beforeEach((to, from, next) => {
  // if (to.path) {
  //  window._hmt.push(['_trackPageview', to.fullPath]);
  //   }
  // }
  /* 路由发生变化修改页面title */
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})
router.afterEach(() => {
  window.scrollTo({
    top: 0,
    behavior: "instant"
  });
});


