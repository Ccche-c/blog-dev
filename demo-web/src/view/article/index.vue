<template>
    <div class="article-info">
        <v-card class="main">
            <span @click="handleClike(article.category, '/category')" class="category">
                {{ article.category.name }}
            </span>

            <h1 class="article-title">{{ article.title }}</h1>

            <div class="article-desc">
                <div class="article-item">
                    <img src="https://foruda.gitee.com/avatar/1677050610632357168/5407895_quequnlong_1646130774.png" alt="">
                    <div class="meta">
                        <div class="author">
                            <a class="link" href="#" title="yyzq">{{ article.username }}</a>
                        </div>
                        <div class="item">
                            <span class="">{{ formatDate(article.createTime) }}</span>
                            <span class="line">/</span>
                            <span class="text">{{ article.commentCount }} 评论</span>
                            <span class="line">/</span>
                            <span class="text">{{ article.likeCount == null ? 0 : article.likeCount }} 点赞</span>
                            <span class="line">/</span>
                            <span class="text">{{ article.quantity }} 阅读</span>
                            <span class="line">/</span>
                            <span class="text">{{ fontNumber }} 字</span>
                        </div>
                    </div>
                </div>
                <time class="time">
                    {{ formatDate(article.createTime, "MM/dd") }}
                </time>
            </div>
            <div class="tips">
                <div class="tipsName">
                    <i class="iconfont icon-tongzhi1"></i>
                    <span>温馨提示：</span>
                </div>
                <p>本文最后更新于 {{ formatDate(article.updateTime) }}，若内容或图片失效，请留言反馈。部分素材来自网络，若不小心影响到您的利益，请联系我们删除</p>
            </div>
            <!-- 文章内容 -->
            <v-md-preview class="content" :text="this.article.contentMd" ref="preview" />
            <!-- 文章标签和分享 -->
            <div class="tag-share">
                <div>
                    <a class="tagItem" href="javascript:;" @click="handleClike(item, '/tags')"
                        v-for="item in article.tagList" :key="item.id">
                        <svg-icon icon-class="tag"></svg-icon>
                        <span>
                            {{ item.name }}
                        </span>
                    </a>
                </div>
                <!-- 分享 -->
                <div class="social-share">
                    <a href="" class="social-share-icon icon-qzone">
                        <i class="iconfont icon-qqkongjian"></i>
                    </a>
                    <a href="" class="social-share-icon icon-qq">
                        <i class="iconfont icon-QQ"></i>
                    </a>
                    <a href="" class="social-share-icon icon-wechat">
                        <i class="iconfont icon-weixin"></i>
                    </a>
                    <a href="" class="social-share-icon icon-weibo">
                        <i class="iconfont icon-shejiaotubiao-06"></i>
                    </a>
                </div>
            </div>

            <!-- 版权 -->
            <div class="copyright">
                <div class="copyrightItem">
                    <svg-icon icon-class="copyright"></svg-icon>
                    <span class="text name">版权归属:</span>
                    <span class="text"> {{ article.username }}</span>
                </div>
                <div class="copyrightItem">
                    <svg-icon icon-class="link"></svg-icon>
                    <span class="text name">本文链接:</span>
                    <a href="" class="text"> {{ appUrl }}articleInfo?articleId={{ articleId }} </a>
                </div>
                <div class="copyrightItem">
                    <svg-icon icon-class="xkxy"></svg-icon>
                    <span class="text name">许可协议:</span>
                    <span class="text">
                        本博客所有文章除特别声明外，均采用
                        <a class="text" href="https://creativecommons.org/licenses/by-nc-sa/4.0/">
                            CC BY-NC-SA 4.0
                        </a>
                        许可协议。转载请注明文章出处。
                    </span>
                </div>
            </div>
            <!-- 点赞和打赏 -->
            <div class="like-reward">
                <a href="javascript:void(0)" @click="like(article.id)" :class="article.isLike ? 'like active' : 'unlike'">
                    <i class="iconfont icon-dianzan1"></i>
                    点赞
                    <span>{{ article.likeCount }}</span>
                    <span class="one">+1</span>
                </a>
                <a class="reward-btn">
                    <i class="iconfont icon-jineqiandaiyueshangjin"></i>
                    打赏
                    <!-- 二维码 -->
                    <div class="animated fadeInDown reward-main">
                        <ul class="reward-all">
                            <li class="reward-item">
                                <img class="reward-img" :src="$store.state.webSiteInfo.weixinPay" />
                                <div class="reward-desc">微信</div>
                            </li>
                            <li class="reward-item">
                                <img class="reward-img" :src="$store.state.webSiteInfo.aliPay" />
                                <div class="reward-desc">支付宝</div>
                            </li>
                        </ul>
                    </div>
                </a>
            </div>
            <!-- 评论 -->
            <div class="comment-mian">
                <div class="title">
                    <i class="iconfont icon-pinglun"></i>
                    评论
                </div>
                <Comment :comment-list="commentList" :total="commentPages" @reloadComment="getCommens"
                    :articleUserId="article.userId" />
            </div>
        </v-card>
        <!-- 文章目录 -->
        <div class="sidebar">
            <SiteInfo />
            <div class="directory">
                <div class="directory-item">
                    <div class="title">
                        <i class="iconfont icon-menu_normal "></i>
                        <span>文章目录</span>
                    </div>
                    <div :class="active == index ? 'structure active' : 'structure'" v-for="(anchor, index) in titles"
                        @click="handleAnchorClick(anchor)">{{
                            anchor.title }}</div>
                </div>

                <NewlyArticle />
            </div>
        </div>
    </div>
</template>
<script>
import { articleInfo, featchComments, articleLike } from '@/api'
import Sidebar from '@/components/sidebar/index.vue'
import SiteInfo from '@/components/site/index.vue'
import Comment from '@/components/comment/index.vue'
import NewlyArticle from './NewlyArticle.vue'
import { getUser } from '@/utils/auth'
export default {
    components: {
        Sidebar,
        SiteInfo,
        NewlyArticle,
        Comment
    },
    data() {
        return {
            article: {
                category: {},
                comments: [],
                tagList: []
            },
            titles: [],
            active: 0,
            fontNumber: 0,

            appUrl: process.env.VUE_APP_URL,
            likeClass: 'unlike',
            commentList: [],
            tempList: null,
            articleId: this.$route.query.articleId,
            commentQuery: {
                pageNo: 1,
                pageSize: 5,
                articleId: this.$route.query.articleId,
            },
            commentPages: 0,
            count: null,
            timer: null,
            user: {}
        }
    },

    mounted() {
        window.setTimeout(() => {
            if (this.$refs.preview) {
                const anchors = this.$refs.preview.$el.querySelectorAll('h1,h2,h3,h4,h5,h6');
                const titles = Array.from(anchors).filter((title) => !!title.innerText.trim());
                if (!this.titles.length) {
                    this.titles = [];
                }
                const hTags = Array.from(new Set(titles.map((title) => title.tagName))).sort();
                this.titles = titles.map((el) => ({
                    title: el.innerText,
                    lineIndex: el.getAttribute('data-v-md-line'),
                    indent: hTags.indexOf(el.tagName),
                }));
                this.tempList = anchors
            }
        }, 500)
        // 监听滚动事件
        window.addEventListener('scroll', this.onScroll, false)
    },
    created() {
        articleInfo(this.articleId).then(res => {
            this.article = res.data
            //修改标题
            document.title = this.article.title
            // 计算文章字数
            this.fontNumber = this.deleteHTMLTag(this.article.content).length
            this.getCommens();
            //判断文章
        }).catch(err => {
            this.$toast({ type: "error", message: err.message });
        });

    },
    methods: {
        onScroll() {
            // 所有锚点元素的 offsetTop
            const offsetTopArr = []
            if (this.tempList) {
                this.tempList.forEach(item => {
                    offsetTopArr.push(item.offsetTop)
                })
                // 获取当前文档流的 scrollTop
                const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
                // 定义当前点亮的导航下标
                let navIndex = 0
                for (let n = 0; n < offsetTopArr.length; n++) {
                    // 如果 scrollTop 大于等于第n个元素的 offsetTop 则说明 n-1 的内容已经完全不可见
                    // 那么此时导航索引就应该是n了
                    if (scrollTop >= offsetTopArr[n]) {
                        navIndex = n
                    }
                }
                this.active = navIndex
            }


        },
        handleClike(item, path) {
            this.$router.push({ name: path, query: { id: item.id, name: item.name } })
        },
        getCommens() {
            featchComments(this.commentQuery).then(res => {
                this.commentList = res.data != null ? res.data.records : [];
                this.commentPages = res.data != null ? res.data.pages : 0;
            })
        },

        formatDate: function (value, args) {
            var dt = new Date(value);
            let year = dt.getFullYear();
            let month = (dt.getMonth() + 1).toString().padStart(2, '0');
            let date = dt.getDate().toString().padStart(2, '0');
            if (args === "MM/dd") {
                return `${month}/${date}`;
            }
            return `${year}-${month}-${date}`;
        },
        deleteHTMLTag(content) {
            return content
                .replace(/<\/?[^>]*>/g, "")
                .replace(/[|]*\n/, "")
                .replace(/&npsp;/gi, "");
        },
        handleAnchorClick(anchor) {
            const { preview } = this.$refs;
            const { lineIndex } = anchor;
            const heading = preview.$el.querySelector(`[data-v-md-line="${lineIndex}"]`);

            if (heading) {
                preview.scrollToTarget({
                    target: heading,
                    scrollContainer: window,
                    top: 60,
                });
            }
        },
        like(articleId) {
            if (this.count) {
                this.$toast({ type: "error", message: "3秒后再操作" });
                return false;
            }

            articleLike(articleId).then(res => {
                if (this.article.isLike) {
                    this.article.likeCount--;
                    this.article.isLike = false
                } else {
                    this.article.likeCount++;
                    this.article.isLike = true
                }
                this.$toast({ type: "success", message: "点赞成功" });
                const TIME_COUNT = 3;
                if (!this.timer) {
                    this.count = TIME_COUNT;
                    this.timer = setInterval(() => {
                        if (this.count > 0 && this.count <= 3) {
                            this.count--;
                        } else {
                            clearInterval(this.timer);
                            this.timer = null;
                        }
                    }, 1000);
                }
            }).catch(err => {
                this.$toast({ type: "error", message: err.message });
            })

        }
    },
    beforeDestroy() {
        // 组件销毁时清除计时器
        clearInterval(this.timer);
    },
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .article-info {
        display: flex;
        justify-content: center;
        position: relative;

        .main {
            height: 100%;
            width: 100%;
            margin-top: 80px;

            .category {
                display: inline-block;
                background-color: #0396ff;
                margin-right: 10px;
                padding: 5px 5px;
                border-radius: 3px;
                cursor: pointer;
                font-size: 13px;
                color: #fff;
            }

            .article-title {
                font-size: 1.2rem;
                color: var(--article-color);
                text-align: center;
                padding-top: 20px;
                margin-bottom: 15px;
                word-break: break-word;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                font-weight: 500;
            }

            .article-desc {
                height: 50px;
                border-bottom: 1px solid var(--border-line);
                margin-bottom: 15px;
                position: relative;
                display: flex;
                align-items: center;
                padding-bottom: 15px;

                &::after {
                    content: '';
                    position: absolute;
                    bottom: -1.5px;
                    left: 0;
                    width: 80px;
                    height: 3px;
                    border-radius: 1.5px;
                    background: var(--theme-color);
                }

                .article-item {
                    display: flex;

                    img {
                        width: 30px;
                        height: 30px;
                        border-radius: 50%;
                        margin-right: 10px;
                        padding: 3px;
                        -o-object-fit: cover;
                        object-fit: cover;
                        background: var(--border-line);
                        border: 1px solid #dcdfe6;
                    }

                    .meta {
                        display: flex;
                        flex-direction: column;
                        height: 100%;
                        justify-content: space-between;
                        font-size: 12px;

                        .link {
                            font-weight: 500;
                            color: var(--theme-color);
                            text-decoration: none;
                        }

                        .item {
                            color: #909399;
                            line-height: 16px;
                            display: flex;
                            align-items: center;
                            width: 100%;

                            .line,
                            .text {
                                margin: 0 5px
                            }
                        }
                    }
                }


                .time {
                    display: none;
                }

            }

            .tips {
                color: #db7c22;
                background-color: var(--article-tips-color);
                border: 1px solid var(--article-tips-border);
                border-radius: 5px;
                margin-bottom: 20px;

                .tipsName {
                    margin-left: 20px;
                    margin-top: 10px;

                    span {
                        padding-left: 5px;
                    }
                }

                p {
                    margin-left: 40px;
                    padding-right: 10px;
                    padding-bottom: 10px;
                }
            }

            .content {
                color: var(--article-content-color);
            }

            .tag-share {
                display: flex;
                overflow-x: auto;
                margin-right: 5px;
                align-items: center;
                padding-bottom: 15px;
                margin-bottom: 20px;
                position: relative;
                border-bottom: 1px solid var(--border-line);
                margin-top: 20px;

                .tagItem {
                    text-decoration: none;
                    display: inline-block;
                    flex-shrink: 0;
                    position: relative;
                    margin-right: 10px;
                    font-size: 12px;
                    background-color: var(--article-copyr--color);
                    color: #909399;
                    border: 1px solid var(--border-line);
                    padding-right: 8px;
                    padding-left: 2px;
                    height: 26px;
                    line-height: 26px;
                    border-radius: 13px;
                    max-width: 125px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    transition: border .25s, color .25s;

                    svg {
                        width: 16px;
                        height: 16px;
                        vertical-align: -4px;
                    }
                }

                .social-share {
                    position: absolute;
                    right: 0;
                    top: 0;

                    .social-share-icon {
                        margin-left: 10px;
                        display: inline-block;
                        width: 32px;
                        height: 32px;
                        font-size: 20px;
                        border-radius: 50%;
                        line-height: 30px;
                        border: 1px solid #666;
                        color: #666;
                        text-align: center;
                        vertical-align: middle;
                        text-decoration: none;

                        i {
                            font-size: 25px;
                        }
                    }

                    .icon-qzone {
                        border-color: #fdbe3d;
                        color: #fdbe3d;

                    }

                    .icon-wechat {
                        color: #7bc549 !important;
                        border-color: #7bc549;
                    }

                    .icon-weibo {
                        color: #ff763b !important;
                        border-color: #ff763b;
                    }

                    .icon-qq {
                        color: #56b6e7 !important;
                        border-color: #56b6e7;
                    }
                }


            }

            .copyright {
                background-color: var(--article-copyr--color);
                width: 500px;
                border-radius: 5px;
                width: 100%;
                min-height: 130px;

                .copyrightItem {
                    padding: 10px 10px;
                    height: 100%;
                    line-height: 26px;
                    margin-bottom: 10px;

                    svg {
                        width: 18px;
                        height: 18px;
                        margin-right: 3px;
                        vertical-align: -4px;
                    }

                    .text {
                        color: #909399;
                        font-size: 14px;
                        margin-left: 8px;
                        text-decoration: none;
                    }

                    .name {
                        color: var(--theme-color);
                        font-weight: 700;
                    }

                    a:hover {
                        color: var(--theme-color);
                    }
                }


            }

            .like-reward {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-top: 30px;

                .like,
                .reward-btn,
                .unlike {
                    display: inline-block;
                    width: 100px;
                    color: #fff !important;
                    text-align: center;
                    line-height: 36px;
                    font-size: .875rem;
                    margin-left: 12px;
                    text-decoration: none;
                    position: relative;
                }

                .like {
                    background: #ec7259;

                    .one {
                        font-size: 14px;
                        font-weight: normal;
                        color: red;
                        position: absolute;
                        top: 0;
                        left: 50%;
                        opacity: 0;
                    }
                }

                .unlike {
                    background: #969696;

                    .one {
                        font-size: 12px;
                        font-weight: normal;
                        color: red;
                        position: absolute;
                        top: 0;
                        left: 50%;
                        opacity: 0;
                    }
                }

                .active .one {
                    -webkit-animation: flutter 1s ease-in-out;
                    animation: flutter 1s ease-in-out;
                    /* ease-in-out：动画以低速开始和结束 */
                }

                /* +1飘动的动画 */
                @keyframes flutter {
                    0% {
                        opacity: 0;
                    }

                    100% {
                        opacity: 1;
                        top: -50px;
                    }
                }

                .reward-btn {
                    position: relative;
                    display: inline-block;
                    width: 100px;
                    background: #49b1f5;
                    margin: 0 1rem;
                    color: #fff !important;
                    text-align: center;
                    line-height: 36px;
                    font-size: .875rem;

                    &:hover {
                        .reward-main {
                            display: block;
                        }
                    }

                    .reward-main {
                        display: none;
                        position: absolute;
                        bottom: 40px;
                        left: -25px;
                        margin: 0;
                        padding: 0 0 15px;
                        width: 100%;
                    }

                    .iconfont {
                        color: #fff !important;
                        font-size: 15px;
                    }

                    .reward-all {
                        display: inline-block;
                        margin: 0 0 0 -110px;
                        padding: 20px 10px 8px !important;
                        width: 320px;
                        border-radius: 4px;
                        background: #f5f5f5;

                        .reward-item {
                            display: inline-block;
                            padding: 0 8px;
                            list-style-type: none;

                            .reward-img {
                                width: 130px;
                                height: 130px;
                                display: block;
                            }

                            .reward-desc {
                                margin: -5px 0;
                                color: #858585;
                                text-align: center;
                            }
                        }

                        &::before {
                            position: absolute;
                            bottom: -10px;
                            left: 0;
                            width: 100%;
                            height: 20px;
                            content: "";
                        }

                        &::after {
                            content: "";
                            position: absolute;
                            right: 0;
                            bottom: 2px;
                            left: 0;
                            margin: 0 auto;
                            width: 0;
                            height: 0;
                            border-top: 13px solid #f5f5f5;
                            border-right: 13px solid transparent;
                            border-left: 13px solid transparent;
                        }
                    }
                }
            }

            .comment-mian {
                .title {
                    font-weight: 700;
                    font-size: 20px;
                    margin-top: 20px;
                    color: var(--theme-color);
                }
            }
        }

        .sidebar {
            display: none;
        }


    }
}

@media screen and (min-width: 1119px) {
    .article-info {
        display: flex;
        justify-content: center;
        position: relative;

        .main {
            background-color: var(--background-color);
            padding: 20px;
            height: 100%;
            width: 55%;
            margin-top: 80px;

            &:hover {
                box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                transition: all .3s;
            }

            .category {
                display: inline-block;
                background-color: #0396ff;
                margin-right: 10px;
                padding: 5px 5px;
                border-radius: 3px;
                cursor: pointer;
                font-size: 13px;
                color: #fff;
                transition: transform .5s;

                &:hover {
                    transform: scale(1.1);
                }
            }

            .article-title {
                font-size: 24px;
                color: var(--article-color);
                text-align: center;
                padding-top: 20px;
                margin-bottom: 15px;
                word-break: break-word;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                font-weight: 500;
            }

            .article-desc {
                height: 50px;
                border-bottom: 1px solid var(--border-line);
                margin-bottom: 15px;
                position: relative;
                display: flex;
                align-items: center;
                padding-bottom: 15px;

                &::after {
                    content: '';
                    position: absolute;
                    bottom: -1.5px;
                    left: 0;
                    width: 80px;
                    height: 3px;
                    border-radius: 1.5px;
                    background: var(--theme-color);
                }

                .article-item {
                    display: flex;

                    img {
                        width: 30px;
                        height: 30px;
                        border-radius: 50%;
                        margin-right: 10px;
                        padding: 3px;
                        -o-object-fit: cover;
                        object-fit: cover;
                        background: var(--border-line);
                        border: 1px solid #dcdfe6;
                    }

                    .meta {
                        display: flex;
                        flex-direction: column;
                        height: 35px;
                        justify-content: space-between;
                        font-size: 12px;

                        .link {
                            font-weight: 500;
                            color: var(--theme-color);
                            text-decoration: none;
                        }

                        .item {
                            color: #909399;
                            line-height: 16px;
                            display: flex;
                            align-items: center;

                            .line,
                            .text {
                                margin: 0 5px
                            }
                        }
                    }
                }


                .time {
                    font-size: 32px;
                    line-height: 42px;
                    color: #606266;
                    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                    position: absolute;
                    right: 0;
                }

            }

            .tips {
                color: #db7c22;
                background-color: var(--article-tips-color);
                border: 1px solid var(--article-tips-border);
                border-radius: 5px;
                margin-bottom: 20px;

                .tipsName {
                    margin-left: 20px;
                    margin-top: 10px;

                    span {
                        padding-left: 5px;
                    }
                }

                p {
                    margin-left: 40px;
                    padding-right: 10px;
                    padding-bottom: 10px;
                }
            }

            .v-md-editor-preview .github-markdown-body p img {
                border-radius: 50px;
            }

            .content {
                color: var(--article-content-color);
            }

            .tag-share {
                display: flex;
                overflow-x: auto;
                margin-right: 5px;
                align-items: center;
                padding-bottom: 15px;
                margin-bottom: 20px;
                position: relative;
                border-bottom: 1px solid var(--border-line);
                margin-top: 20px;

                .tagItem {
                    text-decoration: none;
                    display: inline-block;
                    flex-shrink: 0;
                    position: relative;
                    margin-right: 10px;
                    font-size: 12px;
                    background-color: var(--article-copyr--color);
                    color: #909399;
                    border: 1px solid var(--border-line);
                    padding-right: 8px;
                    padding-left: 2px;
                    height: 26px;
                    line-height: 26px;
                    border-radius: 13px;
                    max-width: 125px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    transition: border .25s, color .25s;

                    svg {
                        width: 16px;
                        height: 16px;
                        vertical-align: -4px;
                    }
                }

                .social-share {
                    position: absolute;
                    right: 0;
                    top: 0;

                    .social-share-icon {
                        margin-left: 10px;
                        display: inline-block;
                        width: 32px;
                        height: 32px;
                        font-size: 20px;
                        border-radius: 50%;
                        line-height: 30px;
                        border: 1px solid #666;
                        color: #666;
                        text-align: center;
                        vertical-align: middle;
                        text-decoration: none;

                        i {
                            font-size: 25px;
                        }
                    }

                    .icon-qzone {
                        border-color: #fdbe3d;
                        color: #fdbe3d;

                        &:hover {
                            background-color: #fdbe3d;

                            i {
                                color: white;
                            }
                        }
                    }

                    .icon-wechat {
                        color: #7bc549 !important;
                        border-color: #7bc549;

                        &:hover {
                            background-color: #7bc549;

                            i {
                                color: white;
                            }
                        }
                    }

                    .icon-weibo {
                        color: #ff763b !important;
                        border-color: #ff763b;

                        &:hover {
                            background-color: #ff763b;

                            i {
                                color: white;
                            }
                        }
                    }

                    .icon-qq {
                        color: #56b6e7 !important;
                        border-color: #56b6e7;

                        &:hover {
                            background-color: #56b6e7;

                            i {
                                color: white;
                            }
                        }
                    }
                }


            }

            .copyright {
                background-color: var(--article-copyr--color);
                width: 500px;
                border-radius: 5px;
                width: 100%;
                min-height: 130px;

                .copyrightItem {
                    padding: 10px 10px;
                    height: 26PX;
                    line-height: 26px;
                    margin-bottom: 10px;

                    svg {
                        width: 18px;
                        height: 18px;
                        margin-right: 3px;
                        vertical-align: -4px;
                    }

                    .text {
                        color: #909399;
                        font-size: 14px;
                        margin-left: 8px;
                        text-decoration: none;
                    }

                    .name {
                        color: var(--theme-color);
                        font-weight: 700;
                    }

                    a:hover {
                        color: var(--theme-color);
                    }
                }


            }

            .like-reward {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-top: 30px;

                .like,
                .reward-btn,
                .unlike {
                    display: inline-block;
                    width: 100px;
                    color: #fff !important;
                    text-align: center;
                    line-height: 36px;
                    font-size: .875rem;
                    margin-left: 12px;
                    text-decoration: none;
                    position: relative;
                }

                .like {
                    background: #ec7259;

                    .one {
                        font-size: 14px;
                        font-weight: normal;
                        color: red;
                        position: absolute;
                        top: 0;
                        left: 50%;
                        opacity: 0;
                    }
                }

                .unlike {
                    background: #969696;

                    .one {
                        font-size: 12px;
                        font-weight: normal;
                        color: red;
                        position: absolute;
                        top: 0;
                        left: 50%;
                        opacity: 0;
                    }
                }

                .active .one {
                    -webkit-animation: flutter 1s ease-in-out;
                    animation: flutter 1s ease-in-out;
                    /* ease-in-out：动画以低速开始和结束 */
                }

                /* +1飘动的动画 */
                @keyframes flutter {
                    0% {
                        opacity: 0;
                    }

                    100% {
                        opacity: 1;
                        top: -50px;
                    }
                }

                .reward-btn {
                    position: relative;
                    display: inline-block;
                    width: 100px;
                    background: #49b1f5;
                    margin: 0 1rem;
                    color: #fff !important;
                    text-align: center;
                    line-height: 36px;
                    font-size: .875rem;

                    &:hover {
                        cursor: pointer;

                        .reward-main {
                            display: block;
                        }
                    }

                    .reward-main {
                        display: none;
                        position: absolute;
                        bottom: 40px;
                        left: 0;
                        margin: 0;
                        padding: 0 0 15px;
                        width: 100%;
                    }

                    .iconfont {
                        color: #fff !important;
                        font-size: 15px;
                    }

                    .reward-all {
                        display: inline-block;
                        margin: 0 0 0 -110px;
                        padding: 20px 10px 8px !important;
                        width: 320px;
                        border-radius: 4px;
                        background: #f5f5f5;

                        .reward-item {
                            display: inline-block;
                            padding: 0 8px;
                            list-style-type: none;

                            .reward-img {
                                width: 130px;
                                height: 130px;
                                display: block;
                            }

                            .reward-desc {
                                margin: -5px 0;
                                color: #858585;
                                text-align: center;
                            }
                        }

                        &::before {
                            position: absolute;
                            bottom: -10px;
                            left: 0;
                            width: 100%;
                            height: 20px;
                            content: "";
                        }

                        &::after {
                            content: "";
                            position: absolute;
                            right: 0;
                            bottom: 2px;
                            left: 0;
                            margin: 0 auto;
                            width: 0;
                            height: 0;
                            border-top: 13px solid #f5f5f5;
                            border-right: 13px solid transparent;
                            border-left: 13px solid transparent;
                        }
                    }
                }
            }

            .comment-mian {
                .title {
                    font-weight: 700;
                    font-size: 20px;
                    margin-top: 20px;
                    color: var(--theme-color);
                }
            }
        }

        .sidebar {
            margin-left: 20px;

            .directory {

                position: sticky;
                top: 3px;
                border-radius: 10px;
                margin-top: 10px;
                width: 300px;

                .directory-item {
                    &:hover {
                        box-shadow: 0 4px 8px 6px rgba(7, 17, 27, .06);
                        transition: all .3s;
                    }

                    background-color: var(--background-color);
                    border-radius: 10px;

                    .title {
                        border-bottom: 1px solid var(--border-line);
                        padding: 10px 10px;
                        color: var(--theme-color);
                        font-weight: 700;

                        span {
                            padding-left: 10px;
                        }
                    }

                    .active {
                        background-color: #fbd2bb;
                    }

                    .structure {
                        cursor: pointer;
                        font-weight: 700;
                        color: var(--article-content-color);
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        padding: 5px 10px;
                        margin-bottom: 10px;

                        &:hover {
                            background-color: #fbd2bb;
                        }
                    }
                }



            }
        }


    }
}
</style>