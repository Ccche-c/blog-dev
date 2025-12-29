<template>
    <v-row justify="center" data-app="true">
        <v-dialog v-model="wechatFlag" max-width="460">
            <v-card class="login-container" style="border-radius:4px">
                <div class="header">
                    <span class="title">微信扫码登录</span>
                    <span @click="close">
                        <svg-icon @click="close" icon-class="close"></svg-icon>
                    </span>
                </div>

                <div class="login-wrapper">
                    <img class="wxQr" :src="wxUserInfo.qrUrl" alt="">
                </div>
                <div class="login" @click="opLogin">
                    返回登录
                </div>
            </v-card>
        </v-dialog>
    </v-row>
</template>
<script>
import { getWecahtLoginQr, wxIsLogin } from "@/api";
export default {
    data() {
        return {
            code: null,
            timer: null,
            wxUserInfo: {
                qrUrl: "http://img.shiyit.com/error.jpg"
            },
        }
    },

    computed: {
        wechatFlag: {
            set(value) {
                this.$store.state.wechatFlag = value;
            },
            get() {
                let flag = this.$store.state.wechatFlag
                if (flag) {
                    this.getWxQr()
                } else {
                    clearInterval(this.timer);
                }
                return flag;
            }
        },
    },

    methods: {
        opLogin() {
            this.$store.state.wechatFlag = false;
            this.$store.state.loginFlag = true;
        },
        close() {
            clearInterval(this.timer);
            this.$store.state.wechatFlag = false;
        },
        getWxQr() {
            getWecahtLoginQr().then(res => {
                this.wxUserInfo = res.data
                this.timer = setInterval(() => {
                    // 轮询判断用户是否已经登录
                    wxIsLogin(this.wxUserInfo.tempUserId).then(res => {
                        this.$store.commit("login", res.data);
                        this.$store.commit("closeModel");
                        clearInterval(this.timer);
                        if (res.data.email == null) {
                            this.$toast({
                                type: "warnning",
                                message: "请绑定邮箱以便及时收到回复"
                            });
                        } else {
                            this.$toast({ type: "success", message: "登录成功" });
                        }
                    })
                }, 1000);
            })
        },

    }
}
</script>

<style lang="scss" scoped>
.login-container {
    background-color: #fff;
    height: 450px;
    padding: 20px;

    .header {
        text-align: center;
        border-bottom: 1px solid var(--border-line);
        padding-bottom: 10px;

        .title {
            font-size: 1.2rem;
            font-weight: 700;
        }

        svg {
            width: 20px;
            height: 20px;
            float: right;
            cursor: pointer;
            margin-bottom: 20px;

        }
    }

    .login-wrapper {
        text-align: center;
        margin-top: 10px;

        .wxQr {
            width: 300px;
            height: 300px;
        }

        .code-warpper {
            margin-top: 10px;

            .iconfont {
                margin-left: 5px;
                font-weight: 700;
                font-size: 1rem;
                cursor: pointer;
            }

            .code {
                color: red;
                font-weight: 700;
            }
        }
    }

    .login {
        margin-top: 20px;
        cursor: pointer;
        color: var(--theme-color);

        &:hover {
            color: red;
        }
    }
}
</style>