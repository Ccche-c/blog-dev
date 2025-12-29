<template>
    <div v-show="false" class="notice-main">
        <div class="content-warpper">
            <span class="content">
                这是新版博客，会存在很多bug，会慢慢修改优化，旧版源码还是原仓库 <a href="#">点我直达</a>
            </span>

            <Button type="submit" class="close" @click="closeNotice()">关闭</Button>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            noticeFlag: window.sessionStorage.getItem("noticeFlag") == null ? 1 : 0,
        }
    },

    methods: {
        closeNotice() {
            window.sessionStorage.setItem("noticeFlag", 0)
            this.noticeFlag = 0
        }
    }
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .notice-main {
        display: flex;
        justify-content: center;
        position: relative;
        top: 60px;
        margin-bottom: 5px;
        width: 100%;
        height: 100%;

        .content-warpper {
            background-color: var(--theme-color);
            width: 100%;
            text-align: center;
            height: 100%;
            line-height: 25px;
            position: relative;
            color: white;
            word-wrap: break-word;

            .close {
                margin-top: 30px;
                cursor: pointer;
                color: black;
                text-align: center;
            }

        }
    }
}

@media screen and (min-width: 1119px) {
    .notice-main {
        display: flex;
        justify-content: center;
        position: relative;
        top: 70px;

        .content-warpper {
            background-color: var(--theme-color);
            width: 100%;
            text-align: center;
            height: 25px;

            position: relative;
            color: white;

            .content {
                line-height: 25px;

                a {
                    text-decoration: none;
                    padding-left: 5px;
                }
            }


            .close {
                float: right;
                margin-right: 30px;
                cursor: pointer;
            }

        }
    }
}
</style>