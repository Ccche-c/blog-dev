<template>
    <div class="nav" v-show="drawer">
        <!-- 遮罩层 -->
        <div class=" mask" @click="close" @touchmove.stop.prevent></div>
        <!-- 内容 -->
        <div class="warpper" :style="{ width: drawer ? '240px' : '0px' }">
            <!-- 博主介绍 -->
            <div class="blogger-info">
                <v-avatar size="110" style="margin-bottom:0.5rem">
                    <img :src="$store.state.webSiteInfo.authorAvatar" alt="">
                </v-avatar>
            </div>
            <!-- 博客信息 -->
            <div class="blog-info-wrapper">
                <div class="blog-info-data">
                    <a href="/archive">
                        <div style="font-size: 0.875rem">文章</div>
                        <div style="font-size: 1.125rem;">
                            {{ $store.state.siteCount.articleCount }}
                        </div>
                    </a>
                </div>
                <div class="blog-info-data">

                    <a href="/categorys">
                        <div style="font-size: 0.875rem">分类</div>
                        <div style="font-size: 1.125rem">
                            {{ $store.state.siteCount.categoryCount }}
                        </div>
                    </a>
                </div>
                <div class="blog-info-data">
                    <a href="/tag">
                        <div style="font-size: 0.875rem">标签</div>
                        <div style="font-size: 1.125rem">
                            {{ $store.state.siteCount.tagCount }}
                        </div>
                    </a>
                </div>
            </div>
            <hr />
            <!-- 页面导航 -->
            <div class="menu-container">
                <div class="menus-item">
                    <a href="/">
                        <i class="iconfont icon-home" /> 首页
                    </a>
                </div>
                <div class="menus-item">
                    <a href="/archive">
                        <i class="iconfont icon-guidangxiangmu" /> 归档
                    </a>
                </div>

                <div class="menus-item">

                    <a href="/categorys">
                        <i class="iconfont icon-fenlei" /> 分类
                    </a>
                </div>
                <div class="menus-item">
                    <a href="/tag">
                        <i class="iconfont icon-biaoqian" /> 标签
                    </a>
                </div>
                <!-- 画廊功能已下线 -->
                <!--
                <div class="menus-item">
                    <a href="/photo">
                        <i class="iconfont icon-tupian" /> 画廊
                    </a>
                </div>
                -->

                <div class="menus-item">
                    <a href="/sponsor">
                        <i class="iconfont icon-shouye" /> 打赏
                    </a>
                </div>
                <div class="menus-item">
                    <a href="/message">
                        <i class="iconfont icon-pinglun" /> 留言
                    </a>
                </div>
                <div class="menus-item">
                    <a href="/links">
                        <i class="iconfont icon-fenxiang" /> 友链
                    </a>
                </div>
                <div class="menus-item">
                    <a href="/about">
                        <i class="iconfont icon-guanyu1" /> 关于
                    </a>
                </div>

                <div class="menus-item" v-if="!isLogin">
                    <a @click="openLogin"><i class="iconfont icon-gerenzhongxin" /> 登录 </a>
                </div>
                <div v-else>
                    <div class="menus-item">
                        <a href="/user"><i class="iconfont icon-gerenzhongxin" /> 个人中心 </a>
                    </div>
                    <div class="menus-item">
                        <a @click="logout"><i class="iconfont icon-denglu1" /> 退出</a>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
import { logout } from '@/api'
import { getUser, removeUser } from '@/utils/auth'
export default {
    data: function () {
        return {
            img: process.env.VUE_APP_IMG_API,
        }
    },
    computed: {
        drawer: {
            set(value) {
                this.$store.state.drawer = value;
            },
            get() {
                return this.$store.state.drawer;
            }
        },
        isLogin() {
            let user = getUser()
            return user != null;
        }
    },

    methods: {
        close() {
            this.$store.state.drawer = false;
        },
        openLogin() {
            this.$store.state.loginFlag = true;
        },
        logout() {
            //如果在个人中心则跳回上一页
            if (this.$route.path === "/user") {
                this.$router.go(-1);
            }
            logout().then(res => {
                removeUser()
                location.reload()
                this.$toast({ type: "success", message: "注销成功" });
            }).catch(err => {
                console.log(err)
            });;
        }
    }
};
</script>

<style lang="scss" scoped>
a {
    color: var(--text-color);
}

.nav {
    height: 100%;

    .mask {
        background: rgba(101, 101, 101, 0.8); //设置透明度 ，改最后一个数值，0-1
        width: 100%;
        height: 100%;
        z-index: 9;
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

    .warpper {
        font-weight: 700;
        font-size: 1.2rem;
        background-color: #f6f8fa;
        position: fixed;
        top: 0;
        z-index: 10;
        height: 100vh;
        animation: 0.8s ease 0s 1 normal none running sidebarItem;

        .blogger-info {
            padding: 26px 30px 0;
            text-align: center;
            z-index: 99;
        }

        .blog-info-wrapper {
            display: flex;
            align-items: center;
            padding: 12px 10px 0;

            .blog-info-data {
                flex: 1;
                line-height: 2;
                text-align: center;

                a {
                    text-decoration: none;

                }
            }
        }

        hr {
            border: 2px dashed #d2ebfd;
            margin: 20px 0;
        }

        .menu-container {
            padding: 0 10px 40px;
            overflow: scroll;

            .menus-item {


                a {
                    padding: 6px 30px;
                    display: block;
                    line-height: 2;
                    text-decoration: none;
                }

                i {
                    margin-right: 1rem;
                }
            }
        }
    }
}

@keyframes sidebarItem {
    0% {
        transform: translateX(200px);
    }

    100% {
        transform: translateX(0);
    }
}
</style>
