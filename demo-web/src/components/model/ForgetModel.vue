<template>
    <v-row justify="center" data-app="true">
        <v-dialog v-model="forgetFlag" :fullscreen="isMobile" max-width="460">
            <v-card class="login-container" style="border-radius:4px">
                <div @click="close" style="float: right;cursor: pointer;margin-bottom: 20px;">
                    <svg-icon style="width: 20px;height: 20px;" icon-class="close"></svg-icon>
                </div>

                <div class="login-wrapper">
                    <!-- 用户名 -->
                    <v-text-field v-model="username" label="邮箱号" placeholder="请输入您的邮箱号" @keyup.enter="forget"
                        style="width: 100%;" />
                    <!-- 验证码 -->
                    <div class="mt-7 send-wrapper">
                        <v-text-field maxlength="6" v-model="code" label="验证码" placeholder="请输入6位验证码"
                            @keyup.enter="forget" />
                        <v-btn :disabled="flag" text small @click="sendCode">
                            {{ codeMsg }}
                        </v-btn>
                    </div>
                    <!-- 密码 -->
                    <v-text-field v-model="password" class="mt-7" label="密码" placeholder="请输入您的密码" @keyup.enter="forget"
                        type="password" />
                    <!-- 按钮 -->
                    <v-btn class="mt-7 commitBtn" block style="color:#fff" @click="forget">
                        确定
                    </v-btn>
                    <!-- 登录 -->
                    <div class="mt-10 login-tip">
                        已有账号？<a href="javascript:;" @click="openLogin">登录</a>
                    </div>
                </div>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
import { sendEmailCode, updatePassword } from "../../api"
export default {
    data: function () {
        return {
            username: "",
            code: "",
            password: "",
            flag: true,
            codeMsg: "发送",
            time: 60,
            show: false
        };
    },
    methods: {
        openLogin() {
            this.$store.state.forgetFlag = false;
            this.$store.state.loginFlag = true;
        },
        close() {
            this.$store.state.forgetFlag = false;
        },
        sendCode() {
            //发送邮件
            this.countDown();
            sendEmailCode(this.username).then(res => {
                this.$toast({ type: "success", message: res.message });
            }).catch(err => {
                this.$toast({ type: "error", message: err.message });
            });
        },
        countDown() {
            this.flag = true;
            this.timer = setInterval(() => {
                this.time--;
                this.codeMsg = this.time + "s";
                if (this.time <= 0) {
                    clearInterval(this.timer);
                    this.codeMsg = "发送";
                    this.time = 60;
                    this.flag = false;
                }
            }, 1000);
        },
        forget() {
            var reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
            if (!reg.test(this.username)) {
                this.$toast({ type: "error", message: "邮箱格式不正确" });
                return false;
            }
            if (this.code.trim().length !== 6) {
                this.$toast({ type: "error", message: "请输入6位验证码" });
                return false;
            }
            if (this.password.trim().length < 6) {
                this.$toast({ type: "error", message: "密码不能少于6位" });
                return false;
            }
            const user = {
                email: this.username,
                password: this.password,
                code: this.code
            };
            updatePassword(user).then(res => {
                this.$toast({ type: "success", message: res.message });
            }).catch(err => {
                this.$toast({ type: "error", message: err.message });
            });
        }
    },
    computed: {
        forgetFlag: {
            set(value) {
                this.$store.state.forgetFlag = value;
            },
            get() {
                return this.$store.state.forgetFlag;
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
    height: 400px;
    padding: 20px;

    .login-wrapper {
        width: 100%;
        margin: auto;
        margin-top: 30px;

        .send-wrapper {
            display: flex;
            align-items: center;
        }

        .commitBtn {
            background-color: #4cAF50;
            margin-bottom: 30px;
            margin-top: 10px;
        }

        .login-tip {
            a {
                text-decoration: none;
                color: var(--theme-color);
            }
        }
    }
}
</style>
