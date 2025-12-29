<template>
  <div id="app">
    <!-- 头部 -->
    <Header></Header>
    <!-- 侧边导航栏 -->
    <SideNavBar></SideNavBar>
    <SearchModle></SearchModle>
    <!-- 通知框 -->
    <Notice></Notice>

    <!-- 内容 -->
    <transition name="moveCartoon" appear>
      <div v-if="validate()" style="min-height: calc(100vh - 167px);">
        <router-view :key="$route.fullPath" />
      </div>
      <div v-else style="min-height:100vh">
        <router-view :key="$route.fullPath" />
      </div>
      <!-- <router-view :key="$route.fullPath" :style="!$store.state.noticeFlag ? 'margin-top: 80px;' : ''" /> -->
    </transition>
    <!-- 底部 -->
    <Footer></Footer>

    <!-- 登录模态框 -->
    <Login></Login>
    <!-- 微信登录模态框 -->
    <WechatLogin></WechatLogin>
    <!-- 忘记密码模态框 -->
    <ForgetModel></ForgetModel>
    <!-- 注册模态框 -->
    <RegisterModel></RegisterModel>
    <!-- 友链模态框 -->
    <LinkModel></LinkModel>
    <!-- 侧边栏 -->
    <Sidebar v-if="!isMobile"></Sidebar>
  </div>
</template>

<script>
import Header from '@/components/layout/Header.vue'
import SideNavBar from "@/components/layout/SideNavBar.vue";
import SearchModle from "@/components/layout/Search.vue";
import Home from '@/view/home/index.vue'
import Footer from '@/components/layout/Footer.vue'
import Sidebar from '@/components/layout/Sidebar.vue'
import Login from '@/components/model/Login.vue'
import ForgetModel from '@/components/model/ForgetModel.vue'
import RegisterModel from '@/components/model/RegisterModel.vue'
import LinkModel from '@/components/model/LinkModel.vue'
import WechatLogin from '@/components/model/WechatLogin.vue'
import Notice from '@/components/notice/Notice.vue'
import { getWebSiteInfo } from '@/api'
import { setSkin, getSkin } from '@/utils/auth'
export default {
  name: 'App',
  components: {
    Header,
    Footer,
    Sidebar,
    Home,
    Login,
    ForgetModel,
    RegisterModel,
    LinkModel,
    SideNavBar,
    Notice,
    WechatLogin,
    SearchModle
  },
  data() {
    return {
      widthPre: '',
      height: "min-height"
    }
  },
  methods: {
    validate() {
      return this.$route.path == '/search' || this.$route.path == '/category';
    }
  },

  created() {
    // 先设置默认值，确保页面立即有内容显示
    const defaultWebConfig = {
      logo: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iMzAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjgwIiBoZWlnaHQ9IjMwIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5MT0dPPC90ZXh0Pjwvc3ZnPg==',
      authorAvatar: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2RkZCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjE4IiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5aSx6LSlPC90ZXh0Pjwvc3ZnPg==',
      name: '博客',
      author: '作者'
    }
    
    getWebSiteInfo().then(res => {
      console.log('API返回数据:', res)
      let siteCount = {
        articleCount: res.extra?.articleCount || 0,
        tagCount: res.extra?.tagCount || 0,
        categoryCount: res.extra?.categoryCount || 0,
      }
      // 合并API返回的数据和默认值
      const webConfig = {
        ...defaultWebConfig,
        ...(res.extra?.webConfig || {}),
        // 确保logo和authorAvatar不为空
        logo: (res.extra?.webConfig?.logo && res.extra.webConfig.logo.trim()) || defaultWebConfig.logo,
        authorAvatar: (res.extra?.webConfig?.authorAvatar && res.extra.webConfig.authorAvatar.trim()) || defaultWebConfig.authorAvatar
      }
      
      console.log('设置网站信息:', webConfig)
      this.$store.commit("setWebSiteInfo", webConfig)
      this.$store.commit("setSiteCount", siteCount)
      this.$store.commit("setTagCloud", res.extra?.tagCloud || [])
      this.$store.commit("setHotArticles", res.extra?.hotArticles || [])
      this.$store.commit("setNewArticleList", res.extra?.newArticleList || [])
    }).catch(err => {
      console.error('获取网站信息失败:', err)
      console.error('错误详情:', err.response || err.message)
      // 使用默认值，确保页面可以正常显示
      this.$store.commit("setWebSiteInfo", defaultWebConfig)
      this.$store.commit("setSiteCount", {
        articleCount: 0,
        tagCount: 0,
        categoryCount: 0
      })
      this.$store.commit("setTagCloud", [])
      this.$store.commit("setHotArticles", [])
      this.$store.commit("setNewArticleList", [])
    })
  },
  beforeCreate() {
    if (getSkin() == null) {
      setSkin("shallow")
    } else {
      setSkin(getSkin())
    }
  },
  computed: {
    // 判断是否是手机端，如果是，返回true
    isMobile() {
      const flag = navigator.userAgent.match(
        /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
      );
      return flag;
    },
  },
  mounted() {
    this.$setSkin()
  },

}
</script>

<style lang="scss" scoped>
/* 类名要对应回 name 的属性值 */
.moveCartoon-enter-active {
  animation: move 1s;
}

.moveCartoon-leave-active {
  animation: move 1s reverse;
}

@keyframes move {
  from {
    transform: translateX(-100%);
  }

  to {
    transform: translate(0);
  }
}

body {
  padding: 0;
  margin: 0;
}

#app {
  background: var(--body-color);
}

// ::-webkit-scrollbar {
//   width: 8px;
// }

// ::-webkit-scrollbar-thumb {
//   background: linear-gradient(180deg, #F0BBC3, #10A44A);
//   border-radius: 8px;
// }
</style>
