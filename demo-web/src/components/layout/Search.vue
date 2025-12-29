<template>
    <div class="search-container " v-show="drawer">
        <!-- 遮罩层 -->
        <div class=" mask" @click="close" @touchmove.stop.prevent></div>
        <!-- 内容 -->
        <div class="warpper ">
            <!-- 搜索框 -->
            <div class="search">
                <input class="searchInput" type="text" v-model="keywords" placeholder="请输入关键字...">
                <a class="commit" href="javascript:;" @click="search()">
                    <i class="iconfont icon-search"></i>
                </a>
            </div>
            <!-- 标签搜索 -->
            <div class="tag-wrapper">
                <i class="iconfont icon-biaoqian"></i>
                <span>标签搜索</span>
                <div class="tag">
                    <span @click="handleClike(item)" :style="{ backgroundColor: `${randomColor()}` }" class="item"
                        v-for="(item, index) in $store.state.tagCloud" :key="index">
                        {{ item.name }}
                    </span>
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
            keywords: null,
            tagCloudList: []
        }
    },
    created() {
        document.scrollTop = 0
    },
    computed: {
        drawer: {
            set(value) {
                this.$store.state.searchDrawer = value;
            },
            get() {
                return this.$store.state.searchDrawer;
            }
        }
    },
    methods: {
        handleClike(item) {
            this.$router.push({ name: "/tags", query: { id: item.id, name: item.name } })
            this.$store.state.searchDrawer = false
        },
        search() {
            if (this.keywords == null || this.keywords == "") {
                this.$toast({ type: "warnning", message: "请输入搜索内容" })
                return;
            }
            this.$router.push({ path: '/search', query: { keyword: this.keywords } })
            this.$store.state.searchDrawer = false
        },
        randomColor() {
            var letters = '0123456789ABCDEF';
            var color = '#';
            do {
                for (var i = 0; i < 6; i++) {
                    color += letters[Math.floor(Math.random() * 16)];
                }
            } while (color === '#FFFFFF' || color === '#000000');
            return color;
        },
        close() {
            this.$store.state.searchDrawer = false;
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
.search-container {
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
        background-color: var(--background-color);
        z-index: 99;
        position: fixed;
        top: 0;
        height: auto;
        width: 100%;

        .search {
            width: 100%;
            margin-top: 30px;
            position: relative;
            top: 20px;
            margin: 0 auto;

            .searchInput {
                outline: 0;
                border: 0;
                border-top-left-radius: 30px;
                border-bottom-left-radius: 30px;
                background-color: var(--search-input-background-color);
                width: 75%;
                height: 35px;
                padding-left: 10px;
                caret-color: var(--theme-color);
                border: 1px solid var(--search-input-background-color);
                color: var(--text-color);
                margin-left: 20px;

                &:focus {
                    border: 1px solid var(--theme-color);
                    background-color: var(--background-color);
                }
            }

            .commit {
                position: absolute;
                top: 0px;
                height: 35px;
                width: 15%;
                background-color: var(--theme-color);
                border-top-right-radius: 30px;
                border-bottom-right-radius: 30px;
                text-decoration: none;
                text-align: center;

                i {
                    line-height: 35px;
                    color: #fff;
                }
            }
        }

        .tag-wrapper {
            margin-top: 50px;

            i {
                margin-left: 20px;
                margin-right: 10px;
            }

            i,
            span {
                font-size: 1rem;

            }

            .tag {
                padding-top: 20px;
                padding-left: 10px;
                padding-bottom: 10px;

                span {
                    text-decoration: none;
                    margin-left: 10px;
                    padding: 5px;
                    display: inline-block;
                    margin-bottom: 10px;
                    border-radius: 8px;
                    color: #fff;
                    cursor: pointer;
                    transition: all .3s;

                    &:hover {
                        border-radius: 0;
                    }
                }
            }
        }

    }
}
</style>
