<template>
    <v-row justify="center" data-app="true">
        <v-dialog v-model="registerFlag" :fullscreen="isMobile" max-width="460">
            <v-card class="login-container" style="border-radius:4px">
                <div @click="close" style="float: right;cursor: pointer;margin-bottom: 20px;">
                    <svg-icon style="width: 20px;height: 20px;" icon-class="close"></svg-icon>
                </div>
                <div class="login-wrapper">
                    <!-- 用户名 -->
                    <v-text-field 
                        style="width: 100%;" 
                        v-model="username" 
                        label="邮箱号" 
                        placeholder="请输入您的邮箱号"
                        @keyup.enter="register"
                        :disabled="registerLoading"
                    />
                    <v-text-field 
                        v-model="nickname" 
                        label="昵称" 
                        placeholder="请输入您的昵称" 
                        @keyup.enter="register"
                        :disabled="registerLoading"
                    />
                    <!-- 验证码 -->
                    <div class="mt-7 send-wrapper">
                        <v-text-field 
                            maxlength="6" 
                            v-model="code" 
                            label="验证码" 
                            placeholder="请输入6位验证码"
                            @keyup.enter="register"
                            :disabled="registerLoading"
                        />
                        <v-btn text small :disabled="flag || registerLoading" @click="sendCode">
                            {{ codeMsg }}
                        </v-btn>
                    </div>
                    <!-- 密码 -->
                    <v-text-field 
                        v-model="password" 
                        class="mt-7" 
                        label="密码" 
                        placeholder="请输入您的密码"
                        type="password"
                        @keyup.enter="register"
                        :disabled="registerLoading"
                    />
                    <!-- 注册按钮 -->
                    <v-btn 
                        class="mt-7" 
                        block 
                        style="color:#fff;background-color: red;margin-top: 10px;" 
                        @click="register"
                        :loading="registerLoading"
                        :disabled="registerLoading"
                        :color="registerLoading ? 'grey' : 'red'"
                    >
                        <span v-if="!registerLoading">注册</span>
                        <span v-else>注册中...</span>
                    </v-btn>
                    <!-- 登录 -->
                    <div class="mt-10 login-tip">
                        已有账号？<span @click="openLogin">登录</span>
                    </div>
                </div>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
import { sendEmailCode, emailRegister, emailLogin } from "@/api";
export default {
    data: function () {
        return {
            username: "",
            code: "",
            password: "",
            nickname: "",
            flag: true,
            codeMsg: "发送",
            time: 60,
            registerLoading: false, // 注册加载状态
        };
    },
    methods: {
        openLogin() {
            this.$store.state.registerFlag = false;
            this.$store.state.loginFlag = true;
        },
        close() {
            this.$store.state.registerFlag = false;
        },
        sendCode() {
            console.log(this.username)
            sendEmailCode(this.username).then(res => {

                //发送邮件
                this.countDown();
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
        register() {
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
            if (this.nickname.trim().length < 2) {
                this.$toast({ type: "error", message: "昵称不能少于2位" });
                return false;
            }
            
            // 开始加载
            this.registerLoading = true;
            
            const user = {
                email: this.username,
                password: this.password,
                nickname: this.nickname,
                code: this.code
            };
            
            emailRegister(user).then(res => {
                // 注册成功后自动登录
                return emailLogin({ email: user.email, password: user.password });
            }).then(res => {
                // 注册并登录成功
                this.username = "";
                this.password = "";
                this.nickname = "";
                this.code = ""; // 清空验证码
                this.$store.commit("login", res.data);
                this.$store.commit("closeModel");
                this.$toast({ type: "success", message: "注册并登录成功" });
            }).catch(err => {
                // 发生错误
                this.$toast({ type: "error", message: err.message || "注册失败，请重试" });
            }).finally(() => {
                // 无论成功或失败，都要关闭加载状态
                this.registerLoading = false;
            });
        }
    },
    computed: {
        registerFlag: {
            set(value) {
                this.$store.state.registerFlag = value;
            },
            get() {
                return this.$store.state.registerFlag;
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
    height: 460px;
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
