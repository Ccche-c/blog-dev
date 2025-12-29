<template>
    <v-row justify="center" data-app="true">
        <v-dialog v-model="linkFlag" :fullscreen="isMobile" max-width="460">
            <v-card class="login-container" style="border-radius:4px">
                <div @click="close" style="float: right;cursor: pointer;margin-bottom: 20px;">
                    <svg-icon style="width: 20px;height: 20px;" icon-class="close"></svg-icon>
                </div>
                <div class="login-wrapper">
                    <!-- 用户名 -->
                    <v-text-field v-model="name" label="网站名称" placeholder="请输入您的网站名称" @keyup.enter="addLink"
                        style="width: 100%;" />
                    <v-text-field v-model="info" label="网站简介" placeholder="请输入您的网站简介" @keyup.enter="addLink" />
                    <v-text-field v-model="url" label="网站地址" placeholder="请输入您的网站地址" @keyup.enter="addLink" />
                    <v-text-field v-model="avatar" label="网站头像" placeholder="请输入您的网站头像" @keyup.enter="addLink" />
                    <v-text-field v-model="email" label="邮箱地址" placeholder="请输入您的邮箱地址" @keyup.enter="addLink" />
                    <!-- 注册按钮 -->
                    <v-btn class="mt-7" block style="color:#fff;background-color: #56b6e7;margin-top: 10px;"
                        @click="addLink">
                        确定
                    </v-btn>
                </div>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
import { addLink } from "../../api";
export default {
    data: function () {
        return {
            name: "",
            avatar: "",
            url: "",
            email: "",
            info: "",
            flag: true,
        };
    },
    methods: {
        addLink() {
            var reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;

            if (this.name == null || this.name == "") {
                this.$toast({ type: "error", message: "请输入网站名称" });
                return false;
            }
            if (this.info == null || this.info == "") {
                this.$toast({ type: "error", message: "请输入网站简介" });
                return false;
            }
            if (this.url == null || this.url == "") {
                this.$toast({ type: "error", message: "请输入网站地址" });
                return false;
            }
            if (this.avatar == null || this.avatar == "") {
                this.$toast({ type: "error", message: "请输入网站头像地址" });
                return false;
            }
            if (!reg.test(this.email)) {
                this.$toast({ type: "error", message: "邮箱格式不正确" });
                return false;
            }
            const link = {
                name: this.name,
                avatar: this.avatar,
                info: this.info,
                email: this.email,
                url: this.url
            }
            addLink(link).then(res => {
                this.close()
                this.$toast({ type: "success", message: "友链申请成功" });

            }).catch(err => {
                this.$toast({ type: "error", message: err.message });
            })
        },
        close() {
            this.$store.state.linkFlag = false;
        },
    },
    computed: {
        linkFlag: {
            set(value) {
                this.$store.state.linkFlag = value;
            },
            get() {
                return this.$store.state.linkFlag;
            }
        },
        isMobile() {
            const clientWidth = document.documentElement.clientWidth;
            if (clientWidth > 960) {
                return false;
            }
            return true;
        }
    },
    watch: {
        username(value) {
            var reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
            if (reg.test(value)) {
                this.flag = false;
            } else {
                this.flag = true;
            }
        }
    }
};
</script>

<style lang="scss" scoped>
.login-container {
    background-color: #fff;
    height: auto;
    padding: 20px;

    .login-wrapper {
        width: 100%;
        margin: auto;
        margin-top: 30px;

        .send-wrapper {
            display: flex;
            align-items: center;
        }

        .login-tip {
            margin-top: 20px;

            span {
                color: var(--theme-color);
                cursor: pointer;
            }
        }
    }
}
</style>
