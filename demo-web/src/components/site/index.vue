<template>
    <aside class="containner">
        <v-card class="box">
            <img src="https://img.shetu66.com/2023/06/28/1687938449392095.png" alt="" @error="handleBgError">
            <div class="user">
                <div class="avatar_wrapper">
                    <img :src="authorAvatarUrl" alt="" @error="handleAvatarError">
                </div>
                <a class="username">{{ userDisplayName }}</a>
                <span v-if="userInfo?.intro" class="desc">{{ userInfo.intro }}</span>
                <!-- <div class="count">
                    <div class="item" @click="handleClike('/archive')">
                        <span class="num">{{ $store.state.siteCount.articleCount }}</span>
                        <span class="itemName">文章数</span>
                    </div>
                    <div class="item" @click="handleClike('/categorys')">
                        <span class="num">{{ $store.state.siteCount.categoryCount }}</span>
                        <span class="itemName">分类数</span>
                    </div>
                    <div class="item" @click="handleClike('/tag')">
                        <span class="num">{{ $store.state.siteCount.tagCount }}</span>
                        <span class="itemName">标签数</span>
                    </div>
                </div> -->
                <!-- <div class="lianxi">
                    <a v-if="socialLinks.github" :href="socialLinks.github" target="_blank" title="GitHub"
                        rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="github" />
                    </a>
                    <a v-if="socialLinks.gitee" class="gitee" :href="socialLinks.gitee" target="_blank" title="Gitee"
                        rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="gitee" />
                    </a>
                    <a v-if="socialLinks.qq" class="qq"
                        :href="'//wpa.qq.com/msgrd?v=3&amp;uin=' + socialLinks.qq + '&amp;site=qq&amp;menu=yes'"
                        target="_blank" title="QQ" rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="qq" />
                    </a>
                    <a v-if="socialLinks.weibo" class="weibo" :href="socialLinks.weibo" target="_blank" title="微博"
                        rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="weibo" />
                    </a>
                    <a v-if="socialLinks.email" class="email" :href="'mailto:' + socialLinks.email" target="_blank"
                        title="邮箱" rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="email" />
                    </a>
                    <a v-if="socialLinks.zhihu" class="zhihu" :href="socialLinks.zhihu" target="_blank" title="知乎"
                        rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="zhihu" />
                    </a>
                    <a v-if="socialLinks.webSite" class="webSite" :href="socialLinks.webSite" target="_blank" title="个人站点"
                        rel="noopener noreferrer nofollow">
                        <svg-icon icon-class="link" />
                    </a>
                </div> -->
                <!-- 收藏本站 -->
                <div class="collect">
                    <button class="btn" @click="handleCollect">加入书签</button>
                </div>
            </div>
        </v-card>
    </aside>
</template>
<script>
    
import { getUser } from '@/utils/auth'
import { getUserInfo } from '@/api'

export default {
    data() {
        return {
            defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNzUiIGhlaWdodD0iNzUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9Ijc1IiBoZWlnaHQ9Ijc1IiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7lsLHosKk8L3RleHQ+PC9zdmc+',
            userInfo: null, // 存储完整的用户信息
            loading: false
        }
    },
    created() {
        // 获取已登录用户信息
        this.fetchUserInfo()
        
        // 监听用户登录状态变化（通过 BroadcastChannel）
        const bc = new BroadcastChannel('my-channel')
        bc.onmessage = (event) => {
            const { userValue } = event.data
            if (userValue) {
                // 用户登录或更新了信息，重新获取用户信息
                this.fetchUserInfo()
            } else {
                // 用户退出登录
                this.userInfo = null
            }
        }
    },
    computed: {
        // 检查用户是否已登录
        isLoggedIn() {
            return getUser() !== null
        },
        // 安全获取用户头像URL
        authorAvatarUrl() {
            if (this.userInfo?.avatar) {
                return this.userInfo.avatar
            }
            const cookieUser = getUser()
            if (cookieUser?.avatar) {
                return cookieUser.avatar
            }
            return this.defaultAvatar
        },
        // 显示的用户名称
        userDisplayName() {
            if (this.userInfo?.nickname) {
                return this.userInfo.nickname
            }
            const cookieUser = getUser()
            if (cookieUser?.nickname) {
                return cookieUser.nickname
            }
            // 如果未登录，显示网站作者名称作为后备
            return this.$store.state.webSiteInfo?.author || '游客'
        },
        // 社交链接（优先使用用户信息，否则使用网站配置）
        socialLinks() {
            const user = this.userInfo || getUser()
            const webSiteInfo = this.$store.state.webSiteInfo || {}
            
            return {
                github: user?.github || webSiteInfo.github || '',
                gitee: user?.gitee || webSiteInfo.gitee || '',
                qq: user?.qqNumber || webSiteInfo.qqNumber || '',
                weibo: user?.weibo || webSiteInfo.weibo || '',
                email: user?.email || webSiteInfo.email || '',
                zhihu: user?.zhihu || webSiteInfo.zhihu || '',
                webSite: user?.webSite || ''
            }
        }
    },
    methods: {
        // 获取用户信息
        fetchUserInfo() {
            const cookieUser = getUser()
            if (!cookieUser) {
                // 未登录，不获取用户信息
                return
            }
            
            this.loading = true
            getUserInfo().then(res => {
                this.userInfo = res.data
            }).catch(err => {
                console.warn('获取用户信息失败，使用 Cookie 中的用户数据:', err)
                // 如果 API 调用失败，使用 Cookie 中的用户数据
                this.userInfo = cookieUser
            }).finally(() => {
                this.loading = false
            })
        },
        handleBgError(e) {
            // 背景图加载失败时，设置默认背景色
            e.target.style.display = 'none';
            e.target.parentElement.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
        },
        handleAvatarError(e) {
            // 头像加载失败时，使用默认头像
            if (e.target.src !== this.defaultAvatar) {
                e.target.src = this.defaultAvatar;
            }
        },
        handleClike(val) {
            window.location.href = val
        },
        handleCollect() {
            this.$toast({ type: "success", message: "按CTRL+D 键将本页加入书签" })
        }
    }
}
</script>
<style lang="scss" scoped>
.containner {
    padding: 0;
    margin-top: 80px;

    .box {

        width: 300px;
        height: auto;
        background-color: var(--background-color);
        display: block;
        border-radius: 10px;
        position: relative;
        border-radius: 10px;

        &:hover {
            box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
            transition: all .3s;
        }

        &::before {
            content: "";
            position: absolute;
            top: 90px;
            left: 0;
            width: 100%;
            height: 30px;
            z-index: 2;
            background: linear-gradient(to bottom, rgba(255, 255, 255, 0), #fff);
        }

        img {
            width: 100%;
            height: 120px;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            object-fit: cover;
            position: absolute;
            top: 0;
        }

        .user {
            padding: 45px 15px 15px;
            display: flex;
            align-items: center;
            position: relative;
            flex-direction: column;

            .avatar_wrapper {
                position: relative;
                width: 75px;
                height: 75px;
                margin-bottom: 12px;
                // 调整头像位置：可以使用以下属性
                // margin-top: 0;        // 调整头像距离顶部的距离（负值向上，正值向下）
                // margin-left: -60;       // 调整头像左右位置（负值向左，正值向右）
                // transform: translateY(-10px); // 使用 transform 微调位置（更平滑）
                // transform: translateX(10px);  // 水平微调

                img {
                    width: 75px;
                    height: 75px;
                    border-radius: 50%;
                    display: block;
                    overflow: hidden;
                    padding: 5px;
                    -o-object-fit: cover;
                    object-fit: cover;
                    transition: transform .5s;

                    &:hover {
                        transform: rotate(360deg);
                    }
                }
            }

            .username {
                color: var(--theme-color);
                display: block;
                font-size: 16px;
                font-weight: 500;
                margin-bottom: 10px;
            }

            .desc {
                font-size: 14px;
                color: var(--text-color);
            }

            .count {
                width: 100%;
                padding-bottom: 10px;
                display: flex;
                align-items: center;
                padding-bottom: 10px;
                -webkit-box-align: center;
                margin-top: 20px;

                .item {
                    position: relative;
                    min-width: 0;
                    -webkit-box-flex: 1;
                    -ms-flex: 1;
                    flex: 1;
                    -webkit-box-orient: vertical;
                    -webkit-box-direction: normal;
                    -ms-flex-direction: column;
                    flex-direction: column;
                    font-size: 12px;
                    display: flex;
                    border-right: 1px solid var(--border-line);
                    cursor: pointer;

                    &:last-child {
                        border-right: 0;
                    }

                    .num {
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        font-weight: 500;
                        font-size: 22px;
                        margin-bottom: 3px;
                        text-align: center;
                        color: var(--site-color);
                    }

                    .itemName {
                        font-size: 12px;
                        text-align: center;
                        color: var(--text-color);
                    }
                }


            }

            .collect {
                margin-top: 8px;
                width: 100%;

                .btn {
                    background-color: transparent;
                    border-radius: 5px;
                    border: 2px solid var(--theme-color);
                    color: var(--theme-color);
                    padding: 10px 20px;
                    font-weight: 700;
                    position: relative;
                    transition: all 1s;
                    z-index: 1;
                    overflow: hidden;
                    height: 30px;
                    width: 100%;
                    line-height: 8px;

                    &:hover {
                        color: white;

                        &::before {
                            width: 180%;

                        }
                    }

                    &::before {
                        content: '';
                        height: 100%;
                        position: absolute;
                        left: -30px;
                        top: 0;
                        background-color: var(--theme-color);
                        transform: skewX(45deg);
                        width: 0%;
                        transition: all 1s;
                        z-index: -1;
                    }
                }
            }

            .lianxi {
                border-top: 1px solid var(--border-line);
                justify-content: space-around;
                padding-top: 10px;
                text-align: center;
                align-items: center;
                font-size: 14px;
                display: flex;
                margin-top: 6px;

                a {
                    display: inline-block;
                    margin: 0 10px;
                    transition: all 0.5s;

                    &:hover {
                        transform: scale(1.2);
                    }

                    svg {
                        width: 18px;
                        height: 18px;
                    }
                }
            }
        }

    }

}

@keyframes img {
    0% {
        -webkit-transform: rotate(0deg);
    }

    50% {
        -webkit-transform: rotate(180deg);
    }

    100% {
        -webkit-transform: rotate(360deg);
    }

}
</style>