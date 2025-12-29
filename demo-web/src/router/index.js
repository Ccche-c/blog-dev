import { setSkin } from "@/utils/skin";
import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);
const routes = [
    {
        path: "/",
        component: resolve => require(["@/view/home/index.vue"], resolve),
        meta: {
            title: "拾壹博客"
        }
    },
    // 友情链接页面已下线
    // {
    //     path: "/links",
    //     component: resolve => require(["@/view/link/index.vue"], resolve),
    //     meta: {
    //         title: "友情链接"
    //     }
    // },
    {
        path: "/articleInfo",
        component: resolve => require(["@/view/article/index.vue"], resolve),
        meta: {
            title: "文章详情"
        }
    },
    {
        path: "/message",
        component: resolve => require(["@/view/message/index.vue"], resolve),
        meta: {
            title: "留言板"
        }
    },
    // 关于页面已下线
    // {
    //     path: "/about",
    //     component: resolve => require(["@/view/about/index.vue"], resolve),
    //     meta: {
    //         title: "关于作者"
    //     }
    // },
    {
        path: "/search",
        component: resolve => require(["@/view/search/index"], resolve),
        meta: {
            title: "搜索文章"
        }
    },
    {
        path: "/archive",
        component: resolve => require(["@/view/archive/index"], resolve),
        meta: {
            title: "文章归档"
        }
    },
    {
        path: "/categorys",
        component: resolve => require(["@/view/category/Category"], resolve),
        meta: {
            title: "文章分类"
        }
    },
    {
        name: "/category",
        path: "/category",
        component: resolve => require(["@/view/category/index"], resolve),
    },
    {
        path: "/tag",
        component: resolve => require(["@/view/tag/Tag"], resolve),
    },
    {
        path: "/tags",
        name: "/tags",
        component: resolve => require(["@/view/tag/index"], resolve),
    },
    // 画廊页面已下线
    // {
    //     path: "/photo",
    //     component: resolve => require(["@/view/photo/index"], resolve),
    // },
    {
        path: "/email",
        component: resolve => require(["@/view/email/index"], resolve),
    },
    {
        path: "/sponsor",
        component: resolve => require(["@/view/sponsor/index"], resolve),
    },
    {
        path: "/user",
        component: resolve => require(["@/view/user/index"], resolve),
    },
    {
        path: "/callback/qq",
        component: resolve => require(["@/components/model/OauthLogin.vue"], resolve)
    },
    {
        path: "/callback/gitee",
        component: resolve => require(["@/components/model/OauthLogin.vue"], resolve)
    },
    {
        path: "/callback/weibo",
        component: resolve => require(["@/components/model/OauthLogin.vue"], resolve)
    },
    {
        path: "*",
        name: "NotFound",
        component: () => import("@/view/404/404.vue"),
    }
];

const router = new VueRouter({
    mode: "history",
    scrollBehavior: () => ({ y: 0 }),
    routes
});
// 获取原型对象push函数
const originalPush = VueRouter.prototype.push

// 获取原型对象replace函数
const originalReplace = VueRouter.prototype.replace

// 修改原型对象中的push函数
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

// 修改原型对象中的replace函数
VueRouter.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err)
}

router.beforeEach((to, from, next) => {
    setSkin()
    next()
})
export default router;