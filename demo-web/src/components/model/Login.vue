<template>
    <v-row justify="center" data-app="true">
        <v-dialog v-model="loginFlag" max-width="460">
            <v-card class="login-container" style="border-radius:4px">
                <div @click="close" style="float: right;cursor: pointer;margin-bottom: 20px;">
                    <svg-icon style="width: 20px;height: 20px;" icon-class="close"></svg-icon>
                </div>

                <div class="login-wrapper">
                    <!-- 用户名 -->
                    <v-text-field v-model="email" label="邮箱号" :disabled="false" placeholder="请输入您的邮箱号" @keyup.enter="login"
                        style="width: 100%;" />
                    <!-- 密码 -->
                    <v-text-field v-model="password" label="密码" :disabled="false" placeholder="请输入您的密码" @keyup.enter="login"
                        type="password" />
                    <!-- 按钮 -->
                    <v-btn class="mt-7 btn" block :disabled="false" style="color:#fff" @click="login">
                        登录
                    </v-btn>
                    <!-- 注册和找回密码 -->
                    <div class="mt-10 login-tip">
                        <span @click="openRegister">立即注册</span>
                        <span @click="openForget" class="float-right">忘记密码?</span>
                    </div>
                    <div>
                        <div class="social-login-title">社交账号登录</div>
                        <div class="social-login-wrapper">

                            <a @click="qqLogin">
                                <svg-icon icon-class="qq" />
                            </a>

                            <a @click="giteeLogin">
                                <svg-icon icon-class="gitee" />
                            </a>
                            <a @click="weiboLogin">
                                <svg-icon icon-class="weibo" />
                            </a>
                            <a href="javascript:;" @click="openWecaht">
                                <svg-icon icon-class="wechat" />
                            </a>
                        </div>
                    </div>
                </div>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
import { emailLogin } from "@/api";
import { setUrl } from '@/utils/auth'

export default {
    data: function () {
        return {
            email: null,
            password: null
        };
    },
    mounted() {

    },
    computed: {
        loginFlag: {
            set(value) {
                this.$store.state.loginFlag = value;
            },
            get() {
                return this.$store.state.loginFlag;
            }
        },
    },
    methods: {
        openWecaht() {
            this.$store.state.loginFlag = false;
            this.$store.state.wechatFlag = true;
        },
        openRegister() {
            this.$store.state.loginFlag = false;
            this.$store.state.registerFlag = true;
        },
        openForget() {
            this.$store.state.loginFlag = false;
            this.$store.state.forgetFlag = true;
        },
        close() {
            this.$store.state.loginFlag = false;
        },
        login() {
            if (!this.email) {
                this.$toast({ type: "error", message: "邮箱格式不正确" });
                return false;
            }
            if (this.password.trim().length === 0) {
                this.$toast({ type: "error", message: "密码不能为空" });
                return false;
            }
            //发送登录请求
            emailLogin({ email: this.email, password: this.password }).then(res => {
                this.email = null;
                this.password = null;
                this.$store.commit("login", res.data);
                this.$store.commit("closeModel");
                this.$toast({ type: "success", message: "登录成功" });
            }).catch(err => {
                this.$toast({ type: "error", message: err.message });
            });
        },
        qqLogin() {
            //保留当前路径
            this.settingUrl()
            if (
                navigator.userAgent.match(
                    /(iPhone|iPod|Android|ios|iOS|iPad|Backerry|WebOS|Symbian|Windows Phone|Phone)/i
                )
            ) {
                // eslint-disable-next-line no-undef
                QC.Login.showPopup({
                    appId: this.config.QQ_CLIENT_ID,
                    redirectURI: this.config.QQ_REDIRECT_URL
                });
            } else {
                window.open(
                    "https://graph.qq.com/oauth2.0/show?which=Login&display=pc&client_id=" +
                    + this.config.QQ_CLIENT_ID +
                    "&response_type=token&scope=all&redirect_uri=" +
                    this.config.QQ_REDIRECT_URL,
                    "_self"
                );
            }
        },
        giteeLogin() {
            //保留当前路径
            this.settingUrl()
            window.open(
                "https://gitee.com/oauth/authorize?client_id=" +
                this.config.GITEE_CLIENT_ID +
                "&response_type=code&redirect_uri=" +
                this.config.GITEE_REDIRECT_URL,
                "_self"
            );
        },
        weiboLogin() {
            //保留当前路径
            this.settingUrl()
            window.open(
                "https://api.weibo.com/oauth2/authorize?client_id=" +
                this.config.WEIBO_CLIENT_ID +
                "&response_type=code&redirect_uri=" +
                this.config.WEIBO_REDIRECT_URL,
                "_self"
            );
        },
        settingUrl() {
            if (this.$route.path == "/articleInfo") {
                setUrl("articleId=" + this.$route.query.articleId)
            } else {
                setUrl(this.$route.path)
            }
        }
    }
};
</script>

<style lang="scss" scoped>
.login-container {
    background-color: #fff;
    height: 450px;
    padding: 20px;

    .login-wrapper {
        width: 100%;
        margin: auto;
        margin-top: 30px;

        .btn {
            margin-top: 30px;
            background-color: #2196F3;
        }

        .login-tip {
            margin-top: 30px;

            span {
                cursor: pointer;

                &:hover {
                    color: var(--theme-color);
                }
            }

            .float-right {
                float: right;
            }
        }

        .social-login-title {
            margin-top: 1.5rem;
            color: #b5b5b5;
            font-size: 0.75rem;
            text-align: center;

            &::before {
                content: "";
                display: inline-block;
                background-color: #d8d8d8;
                width: 60px;
                height: 1px;
                margin: 0 12px;
                vertical-align: middle;
            }

            &::after {
                content: "";
                display: inline-block;
                background-color: #d8d8d8;
                width: 60px;
                height: 1px;
                margin: 0 12px;
                vertical-align: middle;
            }
        }

        .social-login-wrapper {
            margin-top: 1rem;
            font-size: 2rem;
            text-align: center;

            a {
                text-decoration: none;
                margin-left: 20px;
                cursor: pointer;

                svg {
                    width: 30px;
                    height: 30px;
                }
            }
        }
    }
}
</style>
