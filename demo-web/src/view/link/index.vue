<template>
    <div class="link-main">
        <div class="content">
            <h1>友情链接</h1>
            <h3 class="directory1">友链列表 <span class="num"> {{ linkList.length }} 条</span></h3>
            <div class="links">
                <a class="item" target="_blank" :href="item.url" :style="{ backgroundColor: `${randomColor()}` }"
                    v-for="item in linkList">
                    <span class="name">{{ item.name }}</span>
                    <div class="item-content">
                        <div class="info">{{ item.info }}</div>
                        <img :src="item.avatar" alt="" />
                    </div>
                </a>
            </div>
            <div class="infoBox">
                <div style="display: flex;position: relative;">
                    <h3 class="directory2">申请格式</h3>
                    <div class="btn-box">
                        <button class="btn" @click="handleCklike">
                            <svg-icon icon-class="add"></svg-icon>
                            加入友链
                        </button>
                    </div>

                </div>

                <div class="site">
                    <span class="site-title">&lt; 博客名称 + 博客地址 + 博客Logo + 博客简介 &gt;</span>
                    <span>博客名称：{{ $store.state.webSiteInfo.name }}</span>
                    <span>博客地址: <a :href="$store.state.webSiteInfo.webUrl" target="_blank">{{
                        $store.state.webSiteInfo.webUrl
                    }}</a> </span>
                    <span>博客Logo: <a :href="$store.state.webSiteInfo.logo" target="_blank">{{ $store.state.webSiteInfo.logo
                    }}</a>
                    </span>
                    <span>博客简介：{{ $store.state.webSiteInfo.summary }}</span>
                    <span style="color: red;padding-bottom: 10px;">
                        注:如果已经申请过友链 再次接入则会进行下架处理 需重新审核
                    </span>
                </div>
            </div>
        </div>
        <Sidebar></Sidebar>
    </div>
</template>
<script>
import { featchLinks } from '@/api'
import Sidebar from '@/components/sidebar/index.vue'
export default {
    components: {
        Sidebar,
    },
    data() {
        return {
            linkList: [],
        }
    },
    created() {
        document.title = "友情链接";
        featchLinks().then(res => {
            this.linkList = res.data
        })
    },
    methods: {
        handleCklike() {
            this.$store.commit("setLinkFlag", true);// 存储到vuex
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
        }

    }
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .link-main {
        display: flex;

        .content {
            background-color: var(--background-color);
            margin-top: 60px;
            border-radius: 12px;
            padding: 20px;
            height: 100%;
            width: 100%;

            h1 {
                width: 100%;
                font-size: 24px;
                color: var(--article-color);
                text-align: center;
                padding-top: 40px;
                margin-bottom: 15px;
                word-break: break-word;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                font-weight: 500;
            }

            .directory1,
            .directory2 {
                margin-left: 10px;
                margin-top: 20px;
                font-size: 1.3em;
                line-height: 24px;
                font-weight: 500;
                color: var(--article-color);

                &::before {
                    position: relative;
                    display: inline-block;
                    vertical-align: middle;
                    content: "";
                    top: -3px;
                    margin-right: 8px;
                    background-position: center;
                    width: 20px;
                    height: 20px;
                    background-size: auto 100%;
                    left: 0;
                    background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAH1JREFUWEft1qENgDAQheH/JANg60DAJIgmrMFCzEEYgTFgDyRFIClp0yBf9evl8pl3RsYbp7BjNLHoMptljPiMZH3WAhKQgAQkIIGXQAXUT79cniHaNMa5draliqqojIID86nRHEtvbSqlBSQgAQlIQAISkECRAA746SC5Ad6XpiGnnOGPAAAAAElFTkSuQmCC);
                }

                .num {
                    position: relative;
                    top: -1px;
                    display: inline-block;
                    margin-left: 5px;
                    padding: 0 5px;
                    height: 18px;
                    line-height: 18px;
                    font-size: 12px;
                    color: #909399;
                    background: #f5f5f5;
                    border-radius: 3px;
                }
            }

            .links {
                margin-top: 20px;
                grid-template-columns: repeat(3, 1fr);
                display: grid;
                gap: 15px;
                margin-bottom: 50px;
                width: 100%;

                .item {
                    display: inline-block;
                    width: 100%;
                    height: auto;
                    border-top-left-radius: 10px;
                    border-bottom-right-radius: 30px;
                    transition: all 0.3s;
                    text-decoration: none;

                    &:hover {
                        transform: scale(1.1);
                    }

                    .item-content {
                        display: flex;
                        margin-top: 20px;
                    }

                    .name {
                        color: #fff;
                        border-bottom: 1px solid #fff;
                        font-weight: 700;
                        padding-bottom: 5px;
                        margin-left: 15px;
                        margin-top: 10px;
                        display: inline-block;

                    }

                    .info {
                        margin-left: 15px;
                        color: #fff;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        flex: 1;
                        margin-right: 10px;
                        word-break: break-all;
                    }

                    img {
                        width: 40px;
                        height: 40px;
                        border-radius: 50%;
                        object-fit: cover;
                        margin: 3px 0;
                        margin-right: 8px;

                    }
                }
            }

            .infoBox {

                .btn-box {
                    margin-left: auto;
                    margin-left: 10px;
                    color: var(--theme-color);
                    font-size: 16px;
                    margin-top: 20px;
                    position: absolute;
                    right: 0;
                    line-height: 24px;

                    svg {
                        position: relative;
                        top: 4px;
                        width: 18px;
                        height: 18px;
                    }
                }
            }

            .site {
                background-color: #1d72f320;
                color: #1d72f3;
                border-left: 5px solid #1d72f3;
                margin-bottom: 50px;
                border-top-right-radius: 5px;
                border-bottom-right-radius: 5px;
                margin-top: 20px;
                min-width: 200;

                a {
                    text-decoration: none;
                    color: var(--theme-color);
                    word-wrap: break-word;
                }

                .site-title {
                    font-weight: 700;
                    font-size: 15px
                }

                span {
                    padding: 5px 10px;
                    display: block;
                }
            }
        }
    }
}

@media screen and (min-width: 1119px) {
    .link-main {
        display: flex;
        justify-content: center;

        .content {
            background-color: var(--background-color);
            border-radius: 12px;
            padding: 20px;
            height: 100%;
            width: 55%;
            margin-top: 80px;

            &:hover {
                box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                transition: all .3s;
            }

            h1 {
                font-size: 24px;
                color: var(--article-color);
                text-align: center;
                padding-top: 40px;
                margin-bottom: 15px;
                word-break: break-word;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                font-weight: 500;
            }

            .directory1,
            .directory2 {
                margin-left: 10px;
                margin-top: 20px;
                font-size: 1.3em;
                line-height: 24px;
                font-weight: 500;
                color: var(--article-color);

                &::before {
                    position: relative;
                    display: inline-block;
                    vertical-align: middle;
                    content: "";
                    top: -3px;
                    margin-right: 8px;
                    background-position: center;
                    width: 20px;
                    height: 20px;
                    background-size: auto 100%;
                    left: 0;
                    background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAH1JREFUWEft1qENgDAQheH/JANg60DAJIgmrMFCzEEYgTFgDyRFIClp0yBf9evl8pl3RsYbp7BjNLHoMptljPiMZH3WAhKQgAQkIIGXQAXUT79cniHaNMa5draliqqojIID86nRHEtvbSqlBSQgAQlIQAISkECRAA746SC5Ad6XpiGnnOGPAAAAAElFTkSuQmCC);
                }

                .num {
                    position: relative;
                    top: -1px;
                    display: inline-block;
                    margin-left: 5px;
                    padding: 0 5px;
                    height: 18px;
                    line-height: 18px;
                    font-size: 12px;
                    color: #909399;
                    background: #f5f5f5;
                    border-radius: 3px;
                }
            }

            .links {
                margin-left: 40px;
                margin-top: 20px;
                grid-template-columns: repeat(3, 1fr);
                display: grid;
                gap: 15px;
                margin-bottom: 50px;

                .item {
                    display: inline-block;
                    width: 237px;
                    height: 118px;
                    border-top-left-radius: 10px;
                    border-bottom-right-radius: 30px;
                    transition: all 0.3s;
                    text-decoration: none;

                    &:hover {
                        transform: scale(1.1);
                    }

                    .item-content {
                        display: flex;
                        margin-top: 20px;
                    }

                    .name {
                        color: #fff;
                        border-bottom: 1px solid #fff;
                        font-weight: 700;
                        padding-bottom: 5px;
                        margin-left: 15px;
                        margin-top: 10px;
                        display: inline-block;

                    }

                    .info {
                        margin-left: 15px;
                        color: #fff;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        flex: 1;
                        margin-right: 10px;
                        word-break: break-all;
                    }

                    img {
                        width: 40px;
                        height: 40px;
                        border-radius: 50%;
                        object-fit: cover;
                        margin: 3px 0;
                        margin-right: 8px;

                    }
                }
            }

            .infoBox {

                .btn-box {
                    margin-left: auto;
                    margin-left: 10px;
                    color: var(--theme-color);
                    font-size: 16px;
                    margin-top: 20px;
                    position: absolute;
                    right: 0;
                    line-height: 24px;

                    svg {
                        position: relative;
                        top: 4px;
                        width: 18px;
                        height: 18px;
                    }
                }
            }

            .site {
                background-color: #1d72f320;
                color: #1d72f3;
                border-left: 5px solid #1d72f3;
                margin-left: 30px;
                margin-bottom: 50px;
                border-top-right-radius: 5px;
                border-bottom-right-radius: 5px;
                margin-top: 20px;

                a {
                    text-decoration: none;
                    color: var(--theme-color);
                }

                .site-title {
                    font-weight: 700;
                }

                span {
                    padding: 5px 10px;
                    display: block;
                }
            }
        }
    }
}
</style>