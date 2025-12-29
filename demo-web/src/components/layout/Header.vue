<template>
    <div id="header" class="header">
        <!-- 进度条 -->
        <div id="pre" :style="{ width: widthPre + '%' }" />


        <!-- 内容 -->
        <div class="content">
            <div style="width: 100%,position = relative;">
                <!-- logo -->
                <a href="/" class="logo">
                    <img :src="logoUrl" @error="handleImageError" />
                </a>
                <!-- 手机端导航栏 -->
                <span v-if="isMobile" style="position: absolute;top:25%;left: 10px;color: var(--theme-color);">
                    <!-- <a @click="openSearch"><i class="iconfont iconsousuo" /></a> -->
                    <a @click="openDrawer" style="font-size:20px">
                        <i class="iconfont icon-menu_normal" style="font-size: 1.5rem;" />
                    </a>
                </span>
                <!-- 手机端搜索栏 -->
                <span v-if="isMobile" style="position: absolute;top:25%;right: 10px;color: var(--theme-color);">
                    <!-- <a @click="openSearch"><i class="iconfont iconsousuo" /></a> -->
                    <a @click="openSearchDrawer" style="font-size:20px">
                        <i class="iconfont icon-search" style="font-size: 1.5rem;;" />
                    </a>
                </span>
            </div>


            <!-- 菜单栏 -->
            <nav>
                <div class="nav-left">
                    <a href="/" :class="path == '/' ? 'active' : 'item'">
                        <i class="iconfont icon-home"></i> 首页
                    </a>
                    <a href="javascript:;" :class="path == '/archive' || path == '/categorys' || path == '/tag'
                        ? 'active menu-item article' : 'item menu-item article'" style="margin-left: 10px;">
                        <i class="iconfont icon-menu_normal "></i>文章归档
                        <i class="iconfont icon-xiangxia " style="padding-left: 2px;font-size: 12px;"></i>
                        <div>
                            <div class="menus-submenu">
                                <span @click="handleClike('/archive')">
                                    <i class="iconfont icon-guidangxiangmu" /> 归档
                                </span>
                                <span @click="handleClike('/categorys')"><i class="iconfont icon-fenlei" /> 分类</span>
                                <span @click="handleClike('/tag')"><i class="iconfont icon-biaoqian" /> 标签</span>
                            </div>
                        </div>
                    </a>

                    <!-- 画廊功能已下线 -->
                    <!-- <a href="/photo" :class="path == '/photo' ? 'active' : 'item'"><i class="iconfont icon-tupian"></i>画廊</a> -->
                    <a href="javascript:;" :class="path == '/email' ? 'active menu-item' : 'item menu-item'">
                        <i class="iconfont laboratory icon-shezhixitongshezhigongnengshezhishuxing" ref="item"></i>实验室
                        <i class="iconfont icon-xiangxia " style="padding-left: 2px;font-size: 12px;"></i>
                        <div>
                            <div class="menus-submenu">
                                <span @click="handleClike('/email')">
                                    <i class="iconfont icon-youxiang" /> 时光邮箱
                                </span>
                                <span @click="handleClike(adminUrl)">
                                    <i class="iconfont icon-houtaiguanli" /> 后台管理
                                </span>
                            </div>
                        </div>
                    </a>
                    <a href="/sponsor" :class="path == '/sponsor' ? 'active' : 'item'"><i
                            class="iconfont icon-shouye"></i>打赏名单</a>
                    <a href="/message" :class="path == '/message' ? 'active' : 'item'">
                        <i class="iconfont icon-pinglun"></i>留言板
                    </a>
                </div>
                
                <!-- 搜索框 - 居中 -->
                <div class="search">
                    <input @focus="focus" @blur="blur" class="searchInput" type="text" v-model="keywords"
                        placeholder="请输入关键字...">
                    <a class="commit" href="javascript:;" @click="search()">
                        <i class="iconfont icon-search"></i>
                    </a>
                    <!-- 热搜框 -->
                    <div class="hot_search_main" :style="style">
                        <a :href="'/articleInfo?articleId=' + item.id" v-for="(item, index) in $store.state.hotArticles"
                            :key="index">
                            <span :style="{ backgroundColor: `${colors[index]}` }">{{ index + 1 }}</span>
                            {{ item.title }}
                        </a>
                    </div>
                </div>

                <div class="nav-right">
                    <!-- 移除友情链接和关于入口 -->
                    <!--
                    <a href="/links" :class="path == '/links' ? 'active' : 'item'">
                        <i class="iconfont icon-fenxiang"></i>友情链接
                    </a>
                    <a href="/about" :class="path == '/about' ? 'active' : 'item'">
                        <i class="iconfont icon-guanyu1"></i>关于
                    </a>
                    -->
                    <a v-if="!user" href="javascript:;" @click="handleLogin()" class="item"><i
                            class="iconfont icon-gerenzhongxin"></i>登录</a>
                    <a v-else class="menu-item user">
                        <img class="user-avatar" :src="user.avatar || imgUrl" @error="handleAvatarError" height="80" width="80" left="-100px"/>
                        <div class="menus-submenu">
                            <span @click="handleClike('/user')">
                                <i class="iconfont icon-gerenzhongxin" /> 个人中心
                            </span>
                            <span @click="logout"><i class="iconfont icon-denglu1" /> 退出</span>

                        </div>
                    </a>
                </div>
            </nav>
        </div>

    </div>
</template>
<script>
import { logout } from '@/api'
import { getUser, removeUser } from '@/utils/auth'
export default {
    name: 'Header',
    data() {
        return {
            dialog: false,
            widthPre: '',
            keywords: null,
            user: getUser(),
            style: null,
            path: null,
            isMobile: false,
            windowWidth: 0,
            adminUrl: process.env.VUE_APP_ADMIN_API,
            colors: ["#FE2D46", "#FF6600", "#FAA90E", "#7f7f8c", "#7f7f8c"],
            logo: "http://img.shiyit.com/logo.png",
            imgUrl: "data:image/png;base64,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"
        };
    },
    mounted() {
        const bc = new BroadcastChannel('my-channel');
        bc.onmessage = (event) => {
            const { userValue } = event.data;
            if (userValue !== this.user) { // 如果cookie值有变化，则更新数据
                this.user = JSON.parse(userValue);
            }
        }
        // <!--把window.onresize事件挂在到mounted函数上-->
        window.onresize = () => {
            return (() => {
                this.windowWidth = window.innerWidth // 宽
            })()
        }
        window.addEventListener('scroll', this.scrollFn, false)
    },

    computed: {
        // 安全获取logo URL
        logoUrl() {
            const storeLogo = this.$store.state.webSiteInfo?.logo
            return (storeLogo && storeLogo.trim()) || this.logo
        }
    },
    watch: {
        $route(newName, oldName) {
            this.path = newName.path
        },
        user(newName) {
            if (typeof (newName) == 'string') {
                this.user = JSON.parse(newName)
            } else {
                this.user = newName
            }
        },
        // 监听页面宽度
        windowWidth(val) {
            this.isMobile = val < 1119
        }

    },

    methods: {
        handleImageError(e) {
            // 如果logo加载失败，使用默认logo
            if (e.target.src !== this.logo) {
                e.target.src = this.logo;
            }
        },
        handleAvatarError(e) {
            // 如果头像加载失败，使用默认头像
            if (e.target.src !== this.imgUrl) {
                e.target.src = this.imgUrl;
            }
        },
        handleClike(val) {
            window.location.href = val
        },
        focus() {
            this.style = "transform:translate3d(0, 0, 0);opacity:1;visibility:visible"

        },
        blur() {
            this.style = "opacity:0;visibility:hidden"
        },
        search() {
            if (this.keywords == null || this.keywords == "") {
                this.$toast({ type: "warnning", message: "请输入搜索内容" })
                return;
            }
            this.$router.push({ path: '/search', query: { keyword: this.keywords } })
        },
        scrollFn() {
            let page = document.body.scrollHeight
            let client = document.documentElement.clientHeight
            let comend = page - client
            let scrTop = document.documentElement.scrollTop
            this.widthPre = Math.round(scrTop / comend * 10000) / 100

            // 页面滚动距顶部距离
            let scrollTop =
                window.pageYOffset ||
                document.documentElement.scrollTop ||
                document.body.scrollTop;
            let scroll = scrollTop - this.i;
            this.i = scrollTop;


            // 鼠标向上滚动
            if (scroll < 0) {
                document.getElementById("header").className = null
                document.getElementById("header").className = "header slideDown"
            } else {
                document.getElementById("header").className = null
                // 鼠标向下滚动
                document.getElementById("header").className = "header slideUp"
            }
        },
        handleLogin() {
            this.$store.commit("setLoginFlag", true);// 存储到vuex
        },
        logout() {
            //如果在个人中心则跳回上一页
            if (this.path === "/user") {
                this.$router.go(-1);
            }
            logout().then(res => {
                removeUser()
                location.reload()
                this.$toast({ type: "success", message: "注销成功" });

            }).catch(err => {
                console.log(err)
            });
        },
        openDrawer() {
            this.$store.commit("setDrawer", true);
        },
        openSearchDrawer() {
            this.$store.commit("setSearchDrawer", true);
        }
    }
}
</script>
<style scoped lang="scss">
@media screen and (max-width: 1118px) {

    /*针对宽度小于等于767px的屏幕设备，定义如下样式*/
    .header {
        width: 100%;
        z-index: 5;
        top: 0;

        nav,
        .search {
            display: none;
        }

        #pre {
            position: fixed;
            top: 0;
            height: 2px;
            background: linear-gradient(45deg, #3884d9 50%, #40d968);
            z-index: 10;
            width: 100%;
        }

        .content {
            z-index: 5;
            position: fixed;
            width: 100%;
            background-color: var(--header-back-color);
            backdrop-filter: blur(4px);
            display: flex;
            justify-content: center;

            .logo {
                height: 60px;
                display: flex;
                align-items: center;
                margin-left: 20px;
                position: relative;
                margin: 0 auto;

                img {
                    width: 80px;
                    height: 30px;
                    margin: 0 15px 0 10px;
                }
            }
        }



    }
}

@media screen and (min-width: 1119px) {

    /*针对宽度小于等于767px的屏幕设备，定义如下样式*/
    .slideDown {
        transition: transform .5s ease-out;
        transform: translateY(0);
    }

    .slideUp {
        transition: transform .5s ease-out;
        transform: translateY(-100px);
    }

    .header {
        position: fixed;
        width: 100%;
        z-index: 5;
        top: 0;

        #pre {
            position: fixed;
            top: 0;
            height: 2px;
            background: linear-gradient(45deg, #3884d9 50%, #40d968);
            z-index: 10;
        }

        .content {
            display: flex;
            width: 100%;
            background-color: var(--header-back-color);
            backdrop-filter: blur(4px);

            .logo {
                height: 60px;
                display: flex;
                align-items: center;
                margin-left: 20px;  // 调整 Logo 容器的左右位置（增大值向右，减小值向左）
                position: relative;

                &::after {
                    content: "";
                    border-right: 1px solid #ebeef5;
                    position: absolute;
                    right: 0;
                    width: 20px;
                    height: 20px;
                }

                img {
                    width: 80px;
                    height: 30px;
                    margin: 0 15px 0 10px;  // 调整 Logo 图片的内边距
                    // 可以使用以下属性进一步调整 Logo 图片位置：
                    // margin-top: -5px;    // 向上移动（负值向上，正值向下）
                    // margin-bottom: 5px;  // 向下移动
                    // transform: translateY(-5px); // 使用 transform 微调垂直位置
                }
            }
        }
    }

    nav {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
        flex: 1;
        position: relative;

        .nav-left {
            display: flex;
            align-items: center;
            flex: 1;
        }

        .nav-right {
            display: flex;
            align-items: center;
            flex: 1;
            justify-content: flex-end;
            // left: -60px;
        }

        svg {
            width: 30px;
            height: 30px;
        }

        .active {
            text-decoration: none;
            font-size: 15px;
            transition: color .35s;
            line-height: 60px;
            height: 60px;
            display: flex;
            color: var(--header-text-color);
            position: relative;
            justify-content: center;
            width: 90px;
            white-space: nowrap;
            transition: color .35s;
            padding: 0 8px;
            margin-right: 20px;


            i {
                font-weight: 700;
                display: inline-block;
                margin-right: 5px;
                font-size: 18px;
            }

            &::after {
                content: "";
                height: 4px;
                overflow: hidden;
                display: block;
                bottom: 0;
                left: 0;
                right: 0;
                position: absolute;
                width: 50%;
                background: var(--theme-color);
                margin: auto;
            }
        }

        .item {
            text-decoration: none;
            font-size: 15px;
            transition: color .35s;
            line-height: 60px;
            height: 60px;
            display: flex;
            color: var(--header-text-color);
            position: relative;
            justify-content: center;
            width: 90px;
            white-space: nowrap;
            transition: color .35s;
            padding: 0 8px;
            margin-right: 20px;

            i {
                font-weight: 700;
                display: inline-block;
                margin-right: 5px;
                font-size: 18px;
            }

            &::after {
                content: "";
                height: 4px;
                overflow: hidden;
                display: block;
                bottom: 0;
                left: 0;
                right: 0;
                position: absolute;
                width: 40px;
                background: var(--theme-color);
                transform: scaleX(0);
                transition: all 0.5s;
                margin: auto;
            }

            &:hover {
                &::after {
                    transform: scaleX(1);
                }

                color: var(--theme-color);

                i {
                    animation-delay: 0s;
                    animation-name: shock;
                    animation-duration: .1s;
                    animation-iteration-count: 3;
                    animation-direction: normal;
                    animation-timing-function: linear;
                }
            }
        }

        .article .menus-submenu {
            height: 120px;
        }

        .menu-item {
            position: relative;
            padding: 0;


            &:hover .menus-submenu {
                display: block;
            }

            .user-avatar {
                // border-radius: 50%;
                // width: 50px;
                // height: 50px;
                    width: 75px;
                    height: 75px;
                    border-radius: 50%;
                    display: block;
                    overflow: hidden;
                    padding: 5px;
                    -o-object-fit: cover;
                    object-fit: cover;
                    transition: transform .5s;

                    
                // 调整用户头像位置的方法：
                // 方法1：使用 left/right 调整水平位置
                // left: 100px;        // 距离左边的距离（增大值向右移动）
                // right: -50px;       // 距离右边的距离（负值向左移动）
                
                // 方法2：使用 margin 调整位置
                // margin-left: 10px;  // 向右移动
                // margin-right: 10px; // 向左移动
                // margin-top: -5px;   // 向上移动
                // margin-bottom: 5px; // 向下移动
                
                // 方法3：使用 transform 微调（推荐，更平滑）
                transform: translateX(-40px);  // 向右移动10px
                // transform: translateY(-5px);  // 向上移动5px
                // transform: translate(10px, -5px); // 同时调整水平和垂直位置
                
                // 当前设置（可根据需要修改）
                position: relative;
            }

            .menus-submenu {
                position: absolute;
                display: none;
                right: 0;
                top: 60px;
                left: -20px;
                width: max-content;
                box-shadow: 0 5px 20px -4px rgba(0, 0, 0, 0.5);
                background-color: var(--background-color);
                animation: submenu 0.3s 0.1s ease both;
                list-style: none;

                &:before {
                    position: absolute;
                    top: -8px;
                    left: 0;
                    width: 100%;
                    height: 10px;
                    content: "";
                }


                span {
                    width: 130px;
                    display: block;
                    cursor: pointer;
                    color: var(--text-color);
                    text-shadow: none;
                    text-align: center;
                    height: 40px;
                    line-height: 40px;
                    font-weight: 700;

                    i {
                        line-height: 40px;
                    }

                    &:hover {
                        background-color: var(--theme-color);
                        color: white;
                    }
                }
            }

            @keyframes submenu {
                0% {
                    opacity: 0;
                    filter: alpha(opacity=0);
                    transform: translateY(10px);
                }

                100% {
                    opacity: 1;
                    filter: none;
                    transform: translateY(0);
                }
            }
        }

        .user {
            .menus-submenu {
                top: 70px;
                height: 80px;
                left: -70px;
            }
        }
    }

    @keyframes shock {
        0% {
            margin-top: 0px;
        }

        100% {
            margin-top: 2px;
            /* 可以自定义上下左右的幅度 */
        }
    }

    .search {
        display: flex;
        position: relative;
        flex-shrink: 0;
        margin: 0 20px;

        .searchInput {
            outline: 0;
            border: 0;
            border-radius: 30px;
            background-color: var(--search-input-background-color);
            width: 220px;
            height: 30px;
            padding: 0;
            padding-left: 10px;
            caret-color: var(--theme-color);
            border: 1px solid var(--search-input-background-color);
            color: var(--text-color);

            &:focus {
                border: 1px solid var(--theme-color);
                background-color: var(--background-color);
            }
        }

        .commit {
            position: absolute;
            top: 0px;
            right: 0px;
            height: 30px;
            width: 60px;
            background-color: var(--theme-color);
            border-top-right-radius: 30px;
            border-bottom-right-radius: 30px;
            text-decoration: none;
            text-align: center;

            i {
                line-height: 33px;
                color: #fff;
            }
        }

        .hot_search_main {
            background-color: var(--background-color);
            border-radius: 4px;
            box-shadow: 0 0 10px rgba(0, 0, 0, .5);
            position: absolute;
            z-index: 2;
            top: 40px;
            left: 0;
            right: 0;
            opacity: 0;
            visibility: hidden;
            transition: visibility .35s, opacity .35s, transform .35s;
            transform: translate3d(0, 15px, 0);

            a {
                height: 40px;
                text-decoration: none;
                display: block;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                /* border-bottom: 1px solid #f2f6fc; */
                line-height: 40px;
                padding: 4px 5px;
                color: #606266;
                font-size: 12px;

                span {
                    width: 20px;
                    height: 20px;
                    display: inline-block;
                    line-height: 21px;
                    text-align: center;
                    border-radius: 3px;
                    color: var(--background-color);

                }

                &:first-child {
                    border-top-left-radius: 4px;
                    border-top-right-radius: 4px;
                }

                &:last-child {
                    border-bottom-left-radius: 4px;
                    border-bottom-right-radius: 4px;
                }

                &:hover {
                    background-color: rgb(235, 238, 245);
                }
            }
        }
    }
}
</style>