<template>
    <div class="tag_container" v-if="$route.path != '/tag'">
        <div class="title">
            <i class="iconfont icon-biaoqian"></i>
            <span>标签墙</span>
            <a href="/tag" class="more">更多</a>
        </div>
        <div class="tag">
            <span @click="handleClike(item)" :style="{ backgroundColor: `${randomColor()}` }" class="item"
                v-for="(item, index) in $store.state.tagCloud" :key="index">
                {{ item.name }}
            </span>
        </div>
        <!-- template -->
        <!-- <tag-cloud style="width: 300px;height: 250px;" class="tagBackgroud" radius="100" maxFont="16" rotateAngleXbase="50"
            rotateAngleYbase="50" :data="tagCloudList" @clickTag="clickTagItem"></tag-cloud> -->
    </div>
</template>

<script>
export default {
    data() {
        return {
        }
    },

    methods: {
        handleClike(item) {
            this.$router.push({ name: "/tags", query: { id: item.id, name: item.name } })
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
.tag_container {
    width: 300px;
    height: auto;
    margin-top: 20px;
    border-radius: 10px;

    &:hover {
        box-shadow: 0 4px 8px 6px rgba(7, 17, 27, .06);
        transition: all .3s;
    }

    .title {
        width: 300px;
        height: 45px;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        background-color: var(--background-color);
        border-bottom: 1px solid var(--border-line);
        font-size: 16px;
        font-weight: 700;

        i {
            color: var(--theme-color);
            padding: 0 10px 0 20px;
        }

        span {
            color: var(--box-title_color);
        }

        i,
        a,
        span {
            line-height: 45px;
        }

        .more {
            float: right;
            margin-right: 10px;
            font-size: 13px;
            color: gray;
            text-decoration: none;
        }
    }

    .tag {
        background-color: var(--background-color);
        border-bottom-left-radius: 12px;
        border-bottom-right-radius: 12px;
        padding-top: 20px;
        padding-left: 10px;
        padding-bottom: 10px;
        height: auto;

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
</style>