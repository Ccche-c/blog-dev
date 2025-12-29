<template>
    <div class="hot_category">
        <div class="title">
            <span>精品分类</span>
            <a href="/categorys"><i class="iconfont icon-fenlei"></i>全部分类</a>
        </div>
        <div class="categoryList">
            <div class="item" v-for="item in categoryList" :key="item.id">
                <a href="javascript:;" @click="handleClike(item)">
                    <img :src="item.avatar" :alt="item.name" />
                    <span class="name">{{ item.name }}</span>
                </a>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    name: 'HotCategory',
    props: {
        categoryList: {
            type: Array,
            default: () => [],
        }
    },
    methods: {
        handleClike(item) {
            this.$router.push({ name: "/category", query: { id: item.id, name: item.name } })
        }
    }
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .hot_category {
        width: 100%;
        height: auto;
        background-color: var(--background-color);

        .title {
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

            a {
                display: inline-block;
                text-decoration: none;
                color: grey;
                font-size: 14px;
                line-height: 45px;
                position: absolute;
                right: 0;
                margin-right: 10px;

                &:hover {
                    color: var(--theme-color);
                }

                i {
                    line-height: 45px;
                    padding-right: 2px;
                }

            }

        }

        .categoryList {
            margin-top: 10px;
            width: 100%;
            display: flex;

            div {
                height: 122px;
                display: inline-block;
                position: relative;
                margin: 0 5px;
                width: 100%;
            }

            .item {
                overflow: hidden;
                border-radius: 10px;
                width: 100%;

                img {
                    width: 100%;
                    height: 100%;
                    border-radius: 10px;
                    transition: all 0.5s;
                }

                .name {
                    width: 100%;
                    position: absolute;
                    color: var(--baise);
                    background: rgba(0, 0, 0, .4);
                    font-size: 14px;
                    display: block;
                    text-align: center;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    line-height: 122px;
                    border-radius: 10px;
                }

                &:hover {
                    img {
                        transform: scale(1.2);
                    }

                    span {
                        color: #49b1f5;
                    }
                }
            }

        }
    }
}

@media screen and (min-width: 1119px) {
    .hot_category {
        width: 100%;
        height: 179px;
        background-color: var(--background-color);

        .title {
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

            a {
                display: inline-block;
                text-decoration: none;
                color: grey;
                font-size: 14px;
                line-height: 45px;
                position: absolute;
                right: 0;
                margin-right: 10px;

                &:hover {
                    color: var(--theme-color);
                }

                i {
                    line-height: 45px;
                    padding-right: 2px;
                }

            }

        }

        .categoryList {
            margin-left: 5px;
            margin-top: 10px;
            display: flex;
            justify-content: space-around;

            div {
                height: 122px;
                display: inline-block;
                position: relative;
                margin: 0 5px;

            }

            .item {
                overflow: hidden;
                border-radius: 10px;
                width: 190px;
                margin-left: 10px;

                img {
                    height: 100%;
                    width: 100%;
                    border-radius: 10px;
                    transition: all 0.5s;
                }

                .name {
                    position: absolute;
                    color: var(--baise);
                    background: rgba(0, 0, 0, .4);
                    font-size: 14px;
                    display: block;
                    text-align: center;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    line-height: 122px;
                    border-radius: 10px;
                }

                &:hover {
                    img {
                        transform: scale(1.2);
                    }

                    span {
                        color: #49b1f5;
                    }
                }
            }

        }
    }
}
</style>