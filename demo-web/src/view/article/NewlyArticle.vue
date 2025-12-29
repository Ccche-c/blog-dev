<template>
    <div class="container">
        <v-card class="article-card">
            <div class="title">
                <i class="iconfont icon-zuire"></i>
                <span>最新文章</span>
                <a href="/archive" class="more">更多</a>
            </div>
            <div class="article">
                <div class="articleContent" v-for="(item, index) in $store.state.newArticleList" :key="index">
                    <a @click="handleClick(item.id)">{{ index + 1 }}. {{ item.title }}</a>
                </div>
            </div>
        </v-card>
    </div>
</template>
<script>
import { newArticle } from '@/api'
export default {
    data() {
        return {
        };
    },

    methods: {
        handleClick(id) {
            this.$router.push({ path: '/articleInfo', query: { articleId: id } })
        }
    }
}
</script>
<style lang="scss" scoped>
.container {
    padding: 0;

    .article-card {
        margin-top: 20px;
        border-radius: 10px;
        width: 300px;
        background-color: var(--background-color);

        &:hover {
            box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
            transition: all .3s;
        }

        .title {
            height: 45px;
            line-height: 45px;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            border-bottom: 1px solid var(--border-line);

            .more {
                float: right;
                margin-right: 10px;
                font-size: 13px;
                color: gray;
                text-decoration: none;
            }

            i {
                margin-left: 20px;
                margin-right: 10px;
                color: var(--theme-color);
            }

            span {
                font-size: 16px;
                font-weight: 700;
                color: var(--box-title_color);
            }
        }

        .article {
            width: 300px;
            border-bottom-left-radius: 10px;
            border-bottom-right-radius: 10px;
            padding-bottom: 5px;

            .articleContent {
                display: flex;
                position: relative;
                padding: 5px 0;

                a {
                    text-decoration: none;
                    line-height: 20px;
                    color: var(--text-color);
                    font-size: 14PX;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    margin-left: 20px;
                    padding: 5px 0;
                    cursor: pointer;

                    &::after {
                        content: "";
                        height: 2px;
                        overflow: hidden;
                        display: block;
                        left: 0;
                        bottom: 5px;
                        position: absolute;
                        width: 100%;
                        background: var(--theme-color);
                        transform: scaleX(0);
                        transition: all 0.5s;
                    }

                    &:hover::after {
                        transform: scaleX(1);
                    }
                }
            }
        }
    }


}
</style>