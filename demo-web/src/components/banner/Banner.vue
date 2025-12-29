<template>
    <div class="carousel_swipe" v-bind:id="'carousel_swipe' + id" @mouseenter.stop="handleStop" @mouseleave.stop="handleGo">
        <!--图片轮播区域-->
        <transition-group :name="'list-' + (direction === 'forward' ? 'in' : 'out')" tag="ul" class="swipe_list">
            <li v-for="(item, index) in images" :key="index + 1" class="list-item" v-show="index === currentIndex">
                <a href="javascript:;" @click="handleInfo(item.id)">
                    <img alt="" :src="item.avatar" @error="item.avatar = img">
                </a>
            </li>
        </transition-group>
        <!--控制点-->
        <div class="swipe_dian">
            <span v-for="(item, index) in images" :class="{ 'active': index === currentIndex }" :key="index"
                @mouseover="handleChange(index)"></span>
        </div>
        <!--左右控制-->
        <div class="swipe_control" v-bind:id="'swipe_control' + id" v-show="control">
            <i class="iconfont icon-zuojiantou prev" v-on:click="handleChange(prevIndex)"></i>
            <i class="iconfont icon-youjiantou next" v-on:click="handleChange(nextIndex)"></i>
        </div>
        <!--底部标题-->
        <div class="swipe_title" v-for="(item, index) in images" v-show="index === currentIndex" :key="index">
            {{ item.title }}
        </div>
        <div class="swipe_bottom"></div>
    </div>
</template>

<script>
    
export default {
    name: 'Header',
    props: {
        images: {
            type: Array,
            default: () => [],
        }
    },
    data() {
        return {
            img: "http://img.shiyit.com/error.jpg",
            id: 0,
            currentIndex: 0,
            timer: "",
            control: false,
            direction: 'forward',
            noticeFalg: true
        }
    },

    created() {
        this.id = Math.random();
    },
    mounted() {
        document.getElementById("carousel_swipe" + this.id).style.width =
            this.imageWidth + "rem";
        document.getElementById("carousel_swipe" + this.id).style.height =
            this.imageHeight + "rem";
        document.getElementById("swipe_control" + this.id).style.top =
            this.imageHeight / 2 - 1 + "rem";
        this.handleGo();


        // : style = "noticeFlag() != null ? '' : 'margin-top:80px'"
    },
    methods: {

        /**
         * 开始轮播
         */
        handleGo() {
            this.control = false;
            this.timer = setInterval(() => {
                this.currentIndex++;
                if (this.currentIndex > this.images.length - 1) {
                    this.currentIndex = 0;
                }
            }, 3000);
        },
        /**
         * 停止轮播
         */
        handleStop() {
            this.control = true;
            clearInterval(this.timer);
            this.timer = null;
        },
        /**
         * 控制左右切换
         * @param index
         */
        handleChange(index) {
            if (index > this.currentIndex) {
                this.direction = 'forward'
            } else {
                this.direction = ''
            }
            this.currentIndex = index;
        },
        handleInfo(id) {
            this.$router.push({ path: '/articleInfo', query: { articleId: id } })
        }
    },
    computed: {
        prevIndex() {
            if (this.currentIndex === 0) {
                return this.images.length - 1;
            } else {
                return this.currentIndex - 1;
            }
        },
        nextIndex() {
            if (this.currentIndex === this.images.length - 1) {
                return 0;
            } else {
                return this.currentIndex + 1;
            }
        }
    },

}
</script>
<style scoped>
@media screen and (max-width: 1118px) {

    /*切换到下一张的变化*/
    .list-in-enter-to {
        transition: all 1s ease;
        transform: translateX(0);
    }

    .list-in-leave-active {
        transition: all 1s ease;
        transform: translateX(-200%);
    }

    .list-in-enter {
        transform: translateX(100%);
    }

    .list-in-leave {
        transform: translateX(0);
    }

    /*切换到上一张的变化*/
    .list-out-leave {
        transform: translateX(0);
    }

    .list-out-leave-active {
        transition: all 1s ease;
        transform: translateX(100%);
    }

    .list-out-enter {
        transform: translateX(-100%);
    }

    .list-out-enter-to {
        transition: all 1s ease;
        transform: translateX(0);
    }

    .carousel_swipe {
        width: 100%;
        background-color: var(--background-color);
        overflow: hidden;
        display: block;
        position: relative;
        height: 350px;
        overflow: hidden;
        margin-top: 60px;
    }

    .swipe_list {
        width: 100%;
        height: 100%;
    }

    .swipe_list li {
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
    }

    .swipe_list li a {
        width: 100%;
        height: 100%;
    }

    .swipe_list li img {
        width: 100%;
        height: 100%;
    }

    .swipe_dian {
        position: absolute;
        z-index: 3;
        bottom: 0;
        height: 60px;
        text-align: center;
        font-size: 0;
        right: 10px;
    }

    .swipe_dian span {
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        margin: 26px 5px;
        background-color: #5f5853;
        cursor: pointer;
    }

    .swipe_dian .active {
        background-color: #ffffff;
    }

    .swipe_title {
        position: absolute;
        line-height: 60px;
        height: 60px;
        width: 76%;
        z-index: 4;
        text-align: left;
        color: white;
        font-size: 18px;
        margin-left: 10px;
        bottom: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap
    }

    .swipe_bottom {
        position: absolute;
        width: 100%;
        height: 60px;
        bottom: 0;
        background-color: #3a2113;
        opacity: 0.5;
        z-index: 2;
    }

    .swipe_control {
        position: absolute;
        z-index: 2;
        top: 160px;
        width: 100%;
        height: 30px;
        display: flex;
        justify-content: space-between;
        cursor: pointer;
        padding: 0 10px;
    }

    .swipe_control .prev,
    .swipe_control .next {
        text-align: center;
        line-height: 30px;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background-color: #190f0b;
        color: white;
    }

    .swipe_control .prev:hover {
        color: #cccccc;
        font-weight: bold;
        opacity: 0.7;
    }

    .swipe_control .next:hover {
        color: #cccccc;
        font-weight: bold;
        opacity: 0.7;
    }
}

@media screen and (min-width: 1119px) {

    /*切换到下一张的变化*/
    .list-in-enter-to {
        transition: all 1s ease;
        transform: translateX(0);
    }

    .list-in-leave-active {
        transition: all 1s ease;
        transform: translateX(-200%);
    }

    .list-in-enter {
        transform: translateX(100%);
    }

    .list-in-leave {
        transform: translateX(0);
    }

    /*切换到上一张的变化*/
    .list-out-leave {
        transform: translateX(0);
    }

    .list-out-leave-active {
        transition: all 1s ease;
        transform: translateX(100%);
    }

    .list-out-enter {
        transform: translateX(-100%);
    }

    .list-out-enter-to {
        transition: all 1s ease;
        transform: translateX(0);
    }

    .carousel_swipe {
        width: 100%;
        background-color: var(--background-color);
        overflow: hidden;
        display: block;
        position: relative;
        height: 450px;
        overflow: hidden;
        margin-top: 80px;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
    }

    .swipe_list {
        width: 100%;
        height: 100%;
    }

    .swipe_list li {
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
    }

    .swipe_list li a {
        width: 100%;
        height: 100%;
    }

    .swipe_list li img {
        width: 100%;
        height: 100%;
    }

    .swipe_dian {
        position: absolute;
        z-index: 3;
        bottom: 0;
        height: 60px;
        text-align: center;
        font-size: 0;
        right: 10px;
    }

    .swipe_dian span {
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        margin: 26px 5px;
        background-color: #5f5853;
        cursor: pointer;
    }

    .swipe_dian .active {
        background-color: #ffffff;
    }

    .swipe_title {
        position: absolute;
        line-height: 60px;
        height: 60px;
        width: 76%;
        z-index: 4;
        text-align: left;
        color: white;
        font-size: 18px;
        margin-left: 10px;
        bottom: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap
    }

    .swipe_bottom {
        position: absolute;
        width: 100%;
        height: 60px;
        bottom: 0;
        background-color: #3a2113;
        opacity: 0.5;
        z-index: 2;
    }

    .swipe_control {
        position: absolute;
        z-index: 2;
        top: 200px;
        width: 100%;
        height: 30px;
        display: flex;
        justify-content: space-between;
        cursor: pointer;
        padding: 0 10px;
    }

    .swipe_control .prev,
    .swipe_control .next {
        text-align: center;
        line-height: 30px;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background-color: rgba(0, 0, 0, .38);
        color: white;
    }

    .swipe_control .prev:hover {
        color: #cccccc;
        font-weight: bold;
        opacity: 0.7;
    }

    .swipe_control .next:hover {
        color: #cccccc;
        font-weight: bold;
        opacity: 0.7;
    }

}
</style>