<template>
    <div class="artcile_main">

        <div class="title">
            <span>最新文章</span>
        </div>
        <div class="mainBox">
            <ul class="ul_item" v-for="item in articleList">
                <li class="main_li wow bounceIn" @click="handleClick(item.id)" data-wow-duration="2s"
                    data-wow-iteration="1">
                    <div href="" class="thumbnail">
                        <img class="article_cover" :src="item.avatar" :alt="item.title" @error="item.avatar = img" />
                        <i class="iconfont icon-tupian"></i>
                        <span class="time">{{ item.createTime }}</span>
                    </div>
                    <div class="information">
                        <div class="titleName">
                            <span v-if="item.isStick == 1" class="top">置顶</span>
                            <span v-if="item.isOriginal == 1" class="original">原创</span>
                            {{ item.title }}
                        </div>
                        <div class="info">
                            {{ item.summary }}
                        </div>
                        <div class="meta">
                            <ul class="items">
                                <li>{{ item.username }}</li>
                                <li>{{ item.createTime }}</li>
                                <li><i class="iconfont icon-yuedu"></i>{{ item.quantity }}</li>
                                <li><i class="iconfont icon-pinglun"></i>{{ item.commentCount }}</li>
                                <li><i class="iconfont icon-dianzan"></i>{{ item.likeCount }}</li>
                            </ul>
                            <ul class="items">
                                <li v-for="tag in item.tagList">
                                    <i class="iconfont icon-biaoqian"></i>
                                    <span class="tag_name">{{ tag.name }}</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </li>

            </ul>
        </div>
    </div>
</template>
<script>
export default {
    name: 'articleList',
    props: {
        articleList: {
            type: Array,
            default: () => [],
        }
    },
    data() {
        return {
            // 使用base64编码的默认占位图，避免依赖外部资源
            img: "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjEwIiBoZWlnaHQ9IjE0MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjEwIiBoZWlnaHQ9IjE0MCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjE0IiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Zu+54mH5aSx6LSlPC90ZXh0Pjwvc3ZnPg=="
        }
    },
    mounted() {
        new this.$wow.WOW().init()
    },
    methods: {
        formatDate: function (value, args) {
            var dt = new Date(value);
            let year = dt.getFullYear();
            let month = (dt.getMonth() + 1).toString().padStart(2, '0');
            let date = dt.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${date}`;
        },
        handleClick(id) {
            this.$router.push({ path: '/articleInfo', query: { articleId: id } })
        },

    }
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .artcile_main {
        background-color: var(--background-color);
        border-bottom-left-radius: 12px;
        border-bottom-right-radius: 12px;

        .title {
            width: 100%;
            height: 45px;
            padding-left: 10px;
            color: var(--theme-color);
            position: relative;
            border-bottom: 1px solid var(--border-line);

            span {
                display: inline-block;
                font-size: 14px;
                line-height: 45px;
                font-weight: 700;
            }
        }

        .mainBox {
            width: 100%;

            .ul_item {
                width: 100%;
                padding-left: 5px;
                cursor: pointer;
                border-bottom: 1px solid var(--border-line);

                &:last-child {
                    border: 0;
                }

                .main_li {
                    list-style: none;
                    display: flex;
                    position: relative;
                    padding-bottom: 18px;
                    margin-top: 18px;
                    width: 100%;


                    .thumbnail {
                        flex-shrink: 0;
                        position: relative;
                        width: 150px;
                        height: 120px;
                        margin-right: 15px;
                        overflow: hidden;
                        text-decoration: none;

                        .article_cover {
                            border-radius: 4px;
                            object-fit: cover;
                            width: 100%;
                            height: 100%;
                        }

                        .iconfont {
                            position: absolute;
                            top: 2px;
                            left: 5px;
                            color: var(--background-color);
                            font-size: 18px;
                        }

                        .time {
                            display: none;
                        }
                    }

                    .information {
                        display: flex;
                        -webkit-box-orient: vertical;
                        -webkit-box-direction: normal;
                        -ms-flex-direction: column;
                        flex-direction: column;
                        -webkit-box-flex: 1;
                        -ms-flex: 1;
                        flex: 1;
                        min-width: 0;
                        width: 100%;

                        .titleName {
                            margin-bottom: 10px;
                            color: var(--article-color);
                            font-size: 18px;
                            line-height: 24px;
                            max-height: 48px;
                            -webkit-transition: color .35s;
                            transition: color .35s;
                            text-decoration: none;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                            width: 100%;

                            .top,
                            .original {
                                display: none;
                            }
                        }

                        .info {
                            color: var(--text-color);
                            line-height: 22px;
                            max-height: 44px;
                            opacity: .85;
                            text-decoration: none;
                            width: 100%;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                        }

                        .meta {
                            -webkit-box-pack: justify;
                            -ms-flex-pack: justify;
                            justify-content: space-between;
                            margin-top: auto;
                            color: var(--text-color);
                            font-size: 13px;
                            -webkit-box-align: center;
                            -ms-flex-align: center;
                            align-items: center;
                            width: 100%;

                            .items {
                                -webkit-box-pack: start;
                                -ms-flex-pack: start;
                                justify-content: flex-start;
                                -ms-flex-negative: 0;
                                flex-shrink: 0;
                                -webkit-box-align: center;
                                -ms-flex-align: center;
                                align-items: center;
                                padding: 0 0;
                                width: 100%;

                                li {
                                    list-style: none;
                                    display: inline-block;

                                    &:after {
                                        content: "/";
                                        color: #c0c4cc;
                                        padding: 0 5px;
                                    }

                                    &:last-child:after {
                                        content: "";

                                    }

                                    i {
                                        padding: 0 3px;
                                        font-size: 13px;
                                    }

                                    .tag_name {
                                        color: var(--text-color);
                                        text-decoration: none;
                                        width: 100%;

                                        &:hover {
                                            cursor: pointer;
                                            color: var(--theme-color);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}

@media screen and (min-width: 1119px) {
    .artcile_main {
        background-color: var(--background-color);
        border-bottom-left-radius: 12px;
        border-bottom-right-radius: 12px;

        .title {
            width: 100%;
            height: 45px;
            color: var(--theme-color);
            position: relative;
            border-bottom: 1px solid var(--border-line);
            margin-left: 15px;

            span {
                display: inline-block;
                font-size: 14px;
                line-height: 45px;
                font-weight: 700;
            }
        }

        .ul_item {
            padding-left: 20px;
            cursor: pointer;
            border-bottom: 1px solid var(--border-line);

            &:last-child {
                border: 0;
            }

            &:hover {
                .article_cover {
                    transform: scale(1.2);
                }

                .time {
                    transform: translateX(-110%) translateY(5%);
                    transition: all 0.5s;
                }

                .main_li .information .titleName,
                .main_li .information .info {
                    color: var(--theme-color);
                }

                .main_li::before {
                    transform: scaleX(1);
                }

            }

            .main_li {
                list-style: none;

                display: flex;
                position: relative;
                padding-bottom: 18px;
                margin-top: 18px;



                &::before {
                    content: "";
                    width: 4px;
                    height: 30px;
                    background-color: var(--theme-color);
                    position: absolute;
                    left: -15px;
                    border-radius: 50px;
                    transform: scaleX(0);
                    transition: all 0.5s;
                }

                .thumbnail {
                    flex-shrink: 0;
                    position: relative;
                    width: 210px;
                    height: 140px;
                    margin-right: 15px;
                    overflow: hidden;
                    text-decoration: none;

                    .article_cover {
                        border-radius: 4px;
                        object-fit: cover;
                        width: 100%;
                        height: 100%;
                        transition: all 0.3s;
                    }

                    .iconfont {
                        position: absolute;
                        top: 2px;
                        left: 5px;
                        color: var(--background-color);
                        font-size: 18px;
                    }

                    .time {
                        display: block;
                        padding: 5px 5px;
                        border-radius: 20px;
                        position: absolute;
                        font-size: 12px;
                        top: 3px;
                        right: -38%;
                        background-color: var(--theme-color);
                        color: var(--background-color);
                    }
                }

                .information {
                    display: flex;
                    -webkit-box-orient: vertical;
                    -webkit-box-direction: normal;
                    -ms-flex-direction: column;
                    flex-direction: column;
                    -webkit-box-flex: 1;
                    -ms-flex: 1;
                    flex: 1;
                    min-width: 0;

                    .titleName {
                        margin-bottom: 10px;
                        color: var(--article-color);
                        font-size: 18px;
                        line-height: 24px;
                        max-height: 48px;
                        -webkit-transition: color .35s;
                        transition: color .35s;
                        text-decoration: none;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        width: 100%;

                        .top,
                        .original {
                            height: 20px;
                            padding: 0 6px;
                            margin-right: 5px;
                            line-height: 20px;
                            font-size: 12px;
                            white-space: nowrap;
                            vertical-align: 2px;
                            color: var(--background-color);

                            border-radius: 2px 6px;
                            display: inline-block;
                        }

                        .top {
                            background-image: -webkit-linear-gradient(0deg, #3ca5f6 0, #a86af9 100%);
                        }

                        .original {
                            background-image: -webkit-linear-gradient(0deg, #26A69A 0, #00897B 100%);
                        }
                    }

                    .info {
                        color: var(--text-color);
                        line-height: 22px;
                        max-height: 44px;
                        opacity: .85;
                        text-decoration: none;
                        width: 100%;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }

                    .meta {
                        -webkit-box-pack: justify;
                        -ms-flex-pack: justify;
                        justify-content: space-between;
                        margin-top: auto;
                        color: var(--text-color);
                        font-size: 13px;
                        display: flex;
                        -webkit-box-align: center;
                        -ms-flex-align: center;
                        align-items: center;

                        .items {
                            -webkit-box-pack: start;
                            -ms-flex-pack: start;
                            justify-content: flex-start;
                            -ms-flex-negative: 0;
                            flex-shrink: 0;
                            display: flex;
                            -webkit-box-align: center;
                            -ms-flex-align: center;
                            align-items: center;
                            padding: 0 0;

                            li {
                                list-style: none;
                                display: inline-block;

                                &:after {
                                    content: "/";
                                    color: #c0c4cc;
                                    padding: 0 5px;
                                }

                                &:last-child:after {
                                    content: "";

                                }

                                i {
                                    padding: 0 3px;
                                    font-size: 13px;
                                }

                                .tag_name {
                                    color: var(--text-color);
                                    text-decoration: none;

                                    &:hover {
                                        cursor: pointer;
                                        color: var(--theme-color);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
</style>