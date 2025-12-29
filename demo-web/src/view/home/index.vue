<template>
    <div class="main-container">
        <div class="main">
            <div class="main-box">
                <Banner :images="bannerList" />
                <HotCategory :category-list="categoryList" />
                <ArticleList :article-list="articleList" />
            </div>
            <!-- 分页按钮 -->
            <pagination v-if="pageTotal > 0" :page="pageData.pageNo" :page-size="pageData.pageSize" :total="total"
                :on-page-change="onPage" class="pagi page-content" />
        </div>

        <Sidebar />
    </div>
</template>

<script>
import { fetchArticleList, featchHomeData } from '@/api'

import Banner from '@/components/banner/Banner.vue'
import HotCategory from '../category/HotCategory.vue'
import ArticleList from '../article/ArticleList.vue'
import Pagination from '@/components/pagination/index.vue'
import Sidebar from '@/components/sidebar/index.vue'
export default {
    components: {
        Banner,
        HotCategory,
        ArticleList,
        Pagination,
        Sidebar,
    },
    name: 'Home',
    data() {
        return {
            emojis: [],
            pageData: {
                pageNo: 1,
                pageSize: 6,
            },
            bannerList: [],
            categoryList: [],
            articleList: [],
            pageTotal: 0,
        };
    },
    computed: {
        total() {
            return this.pageTotal || 0;
        },
    },

    // require引用：
    mounted() {
        this.fetchArticleList()
        this.getHomeData()
    },
    methods: {
        // 分页
        onPage(pageNow) {
            this.pageData.pageNo = pageNow;
            document.documentElement.scrollTop = 400;
            new this.$wow.WOW().init()
            this.fetchArticleList()
        },
        fetchArticleList() {
            fetchArticleList(this.pageData).then(res => {
                this.articleList = res.data.records;
                this.pageTotal = res.data.total
            })
        },
        getHomeData() {
            featchHomeData().then(res => {
                this.categoryList = res.extra.categories
                // 如果后端返回了轮播图数据，使用后端数据
                // 否则使用自定义的轮播图数据
                if (res.extra.articles && res.extra.articles.length > 0) {
                    this.bannerList = res.extra.articles
                } else {
                    // 自定义轮播图数据（可以在这里修改）
                    this.bannerList = this.getCustomBannerList()
                }
            }).catch(() => {
                // API请求失败时使用自定义数据
                this.bannerList = this.getCustomBannerList()
            })
        },
        // 获取自定义轮播图数据
        getCustomBannerList() {
            return [
                {
                    id: 1, // 文章ID，点击时跳转
                    title: '轮播图标题1', // 显示在轮播图底部的标题
                    avatar: 'https://ts1.tc.mm.bing.net/th/id/R-C.bd89f73f1615505e440b1ea697576fa0?rik=7s%2f0hPdgllpdzQ&riu=http%3a%2f%2fup.deskcity.org%2fpic_source%2fbd%2f89%2ff7%2fbd89f73f1615505e440b1ea697576fa0.jpg&ehk=KAGG1mA2Eayz%2f0S53WBnZeowmjjqhb4NbkPdAa5jNt8%3d&risl=&pid=ImgRaw&r=0' // 图片URL，替换为你的图片地址
                },
                {
                    id: 2,
                    title: '轮播图标题2',
                    avatar: 'https://npimg.hellouikit.com/9c/80/9c802e50facf9bfb4f377c876ece5deb?imageView2/2/w/1000'
                },
                {
                    id: 3,
                    title: '轮播图标题3',
                    avatar: 'https://www.2008php.com/09_Website_appreciate/10-11-28/1290924227try.jpg'
                }
            ]
        },
    },
}
</script>

<style lang="scss" scoped >
@media screen and (max-width: 1118px) {
    .main-container {
        display: flex;
        justify-content: center;

        .main {
            width: 100%;

            .main-box {
                border-radius: 10px;
            }
        }

    }
}

@media screen and (min-width: 1119px) {
    .main-container {
        display: flex;
        justify-content: center;


        .main {
            width: 55%;

            .main-box {
                border-radius: 10px;

                &:hover {
                    box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                    transition: all .3s;
                }
            }
        }

    }
}
</style>