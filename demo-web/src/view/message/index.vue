<template>
    <div>
        <!-- banner -->
        <div class="message-banner" :style="cover">
            <!-- 弹幕输入框 -->
            <div class="message-container">
                <h1 class="message-title">留言板</h1>
                <div class="animated fadeInUp message-input-wrapper">
                    <input v-model="content" @click="show = true" @keyup.enter="addToList" placeholder="说点什么吧" />
                    <button class="ml-3 animated bounceInLeft" @click="addToList" v-show="show">
                        发送
                    </button>
                </div>
            </div>
            <!-- 弹幕列表 -->
            <div class="barrage-container">
                <vue-baberrage :barrageList="barrageList">
                    <template v-slot:default="slotProps">
                        <span class="barrage-items">
                            <img :src="slotProps.item.avatar" width="30" height="30" style="border-radius:50%" />
                            <span class="ml-2">{{ slotProps.item.nickname }} :</span>
                            <span class="ml-2">{{ slotProps.item.content }}</span>
                        </span>
                    </template>
                </vue-baberrage>
            </div>
        </div>
    </div>
</template>

<script>
import { listMessage, addMessage } from "@/api";
import { getUser } from '@/utils/auth'
export default {

    mounted() {
        document.title = "留言板";
        this.listMessage();
    },
    data() {
        return {
            show: false,
            img: process.env.VUE_APP_IMG_API,
            content: "",
            count: null,
            timer: null,
            barrageList: []
        };
    },
    methods: {
        addToList() {
            if (this.count) {
                this.$toast({ type: "error", message: "30秒后才能再次留言" });
                return false;
            }
            if (this.content.trim() === "") {
                this.$toast({ type: "error", message: "留言不能为空" });
                return false;
            }
            var message = {
                avatar: getUser() ? getUser().avatar : $store.state.webSiteInfo.touristAvatar,
                status: 1,
                nickname: getUser() ? getUser().nickname : "游客",
                content: this.content,
                time: Math.floor(Math.random() * (10 - 7)) + 7
            };
            this.barrageList.push(message);
            this.content = "";
            addMessage(message).then(res => {
                this.$toast({ type: "success", message: "留言成功" })
            });
            const TIME_COUNT = 30;
            if (!this.timer) {
                this.count = TIME_COUNT;
                this.timer = setInterval(() => {
                    if (this.count > 0 && this.count <= 30) {
                        this.count--;
                    } else {
                        clearInterval(this.timer);
                        this.timer = null;
                    }
                }, 1000);
            }
        },
        listMessage() {
            listMessage().then(res => {
                this.barrageList = res.data;
            });
        }
    },
    computed: {
        cover() {
            var cover = "https://hao5.qhimg.com/t01965d18c320d35608.jpg?1880*1142";
            return "background: url(" + cover + ") center center / cover no-repeat";
        }
    }
};
</script>

<style scoped>
.message-mian {
    height: 100vh;
}

.message-banner {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #49b1f5;
    animation: header-effect 1s;
}

.message-title {
    color: #eee;
    animation: title-scale 1s;
}

.message-container {
    position: absolute;
    width: 360px;
    top: 35%;
    left: 0;
    right: 0;
    text-align: center;
    z-index: 5;
    margin: 0 auto;
    color: #fff;
}

.message-input-wrapper {
    display: flex;
    justify-content: center;
    height: 2.5rem;
    margin-top: 2rem;
}

.message-input-wrapper input {
    outline: none;
    width: 70%;
    border-radius: 20px;
    height: 100%;
    padding: 0 1.25rem;
    color: #eee;
    border: #fff 1px solid;
}

.message-input-wrapper input::-webkit-input-placeholder {
    color: #eeee;
}

.message-input-wrapper button {
    outline: none;
    border-radius: 20px;
    height: 100%;
    padding: 0 1.25rem;
    border: #fff 1px solid;
}

.barrage-container {
    position: absolute;
    top: 50px;
    left: 0;
    right: 0;
    bottom: 0;
    height: calc(100% -50px);
    width: 100%;
}

.barrage-items {
    background: rgb(0, 0, 0, 0.7);
    border-radius: 100px;
    color: #fff;
    padding: 5px 10px 5px 5px;
    align-items: center;
    display: flex;
    margin-top: 10PX;
}
</style>