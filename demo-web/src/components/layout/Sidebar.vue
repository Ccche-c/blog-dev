<template>
    <div class="theme_main">
        <a href="javascript:void(0)" class="joe_action_item theme active" @click="setSkin()">
            <svg-icon v-if="skin == 'deep'" icon-class="shallow"></svg-icon>
            <svg-icon v-else icon-class="deep"></svg-icon>
        </a>

        <a href="javascript:void(0)" class="joe_action_item back2top active" @click="backTop()" v-if="showBtn">
            <svg-icon icon-class="topBar"></svg-icon>
        </a>
    </div>
</template>
<script>
import { setSkin, getSkin } from '@/utils/auth'
export default {
    data() {
        return {
            theme: true,
            showBtn: false, // 回到顶部，默认是false，就是隐藏起来
            skin: getSkin()
        }
    },
    mounted() {
        window.addEventListener("scroll", this.toTop, true);
    },

    methods: {
        setSkin() {
            this.skin = getSkin()
            let val = ''
            if (this.skin == 'shallow') { //浅色模式
                val = 'deep'
            } else {
                val = 'shallow'
            }
            setSkin(val)
            this.skin = val
            this.$setSkin()
        },
        toTop() {
            let that = this;
            let scrollTop =
                window.pageYOffset ||
                document.documentElement.scrollTop ||
                document.body.scrollTop;
            that.scrollTop = scrollTop;
            if (scrollTop > 200) {
                this.showBtn = true;
            } else {
                this.showBtn = false;
            }
        },
        backTop() {
            var timer = setInterval(function () {
                let osTop =
                    document.documentElement.scrollTop || document.body.scrollTop;
                let ispeed = Math.floor(-osTop / 5);
                document.documentElement.scrollTop = document.body.scrollTop =
                    osTop + ispeed;
                this.isTop = true;
                if (osTop === 0) {
                    clearInterval(timer);
                }
            }, 30);
        },
    },
}
</script>

<style lang="scss" scoped>
.joe_action_item.theme.active {
    pointer-events: auto;
    visibility: visible;
    -webkit-transform: scale(1);
    transform: scale(1);
    position: fixed;
    bottom: 220px;
    right: 10px;
}

a {
    display: block;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    position: relative;
}

.joe_action_item svg {
    position: absolute;
    width: 30px;
    height: 30px;
    fill: var(--theme-color);
    top: 10px;
    right: 10px;

}

.joe_action_item.back2top.active {
    pointer-events: auto;
    visibility: visible;
    -webkit-transform: scale(1);
    transform: scale(1);
    position: fixed;
    bottom: 160px;
    right: 10px;
    animation: fade-in 0.3s linear 1;
}

@keyframes fade-in {
    0% {
        transform: scale(0);
    }

    100% {
        transform: scale(1);
    }
}
</style>