<template>
    <div class="comment-main" v-show="showBox" ref="reply">
        <div class="comment-item">
            <div class="box">
                <div class="avatar">
                    <img v-if="user != null" :src="user.avatar" alt="">
                    <img v-else :src="$store.state.webSiteInfo.touristAvatar" alt="">
                </div>
                <div class="ml-3">
                    <div data-v-0089e256="" class="comment-input">
                        <textarea placeholder="留下点什么吧..." v-model="commentContent" class="comment-textarea"></textarea>
                    </div>
                    <div class="emoji-container">
                        <span @click="chooseEmoji = !chooseEmoji" :class="chooseEmoji ? 'emoji-btn-active' : 'emoji-btn'">
                            <i class="iconfont icon-biaoqing"></i>
                        </span>
                        <div style="margin-left: auto;">
                            <button @click="handleCancle" class="cancle-btn v-comment-btn">
                                取消
                            </button>
                            <button @click="addComment" class="upload-btn v-comment-btn">
                                提交
                            </button>
                        </div>

                    </div>
                    <div class="emoji-wrapper" v-show="chooseEmoji">
                        <span class="emoji-item" v-for="(item, index) of emojiList" :key="index" @click="addEmoji(item)">
                            <span class="emoji">{{ item.emoji }}
                            </span>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>

import { postComment } from '@/api'
import { browserMatch } from '@/utils'
import { getUser } from '@/utils/auth'

export default {
    data() {
        return {
            chooseEmoji: false,
            emojiList: null,
            userId: null,
            nickname: null,
            parentId: null,
            index: null,
            commentContent: "",
            replyUserId: null,
            showBox: false,
            user: getUser()
        }
    },
    watch: {
        user(newName) {
            if (typeof (newName) == 'string') {
                this.user = JSON.parse(newName)
            } else {
                this.user = newName
            }
        }
    },
    mounted() {
        const bc = new BroadcastChannel('my-channel');
        bc.onmessage = (event) => {
            const { userValue } = event.data;
            if (userValue !== this.user) { // 如果cookie值有变化，则更新数据
                this.user = JSON.parse(userValue);
            }
        }
    },
    created() {
        this.emojiList = require('@/assets/emoji.json');
    },
    methods: {

        handleCancle() {
            this.showBox = false
        },
        addComment() {
            if (!this.commentContent) {
                this.$toast({ type: "error", message: "请填写评论内容" });
                return;
            }
            let browser = browserMatch()
            const path = window.location.search;
            const arr = path.split("=");
            let comment = {
                articleId: arr[1],
                avatar: "https://sdn.geekzu.org/avatar/eeb4fe09a1aaad7964b055f331f72608?s=256&d=monsterid",
                content: this.commentContent,
                replyUserId: this.replyUserId,
                parentId: this.parentId,
                browser: browser.browser.toLowerCase(),
                browserVersion: browser.browser + " " + browser.version,
            }
            postComment(comment).then(res => {
                this.$emit("reloadReply", this.index);
                this.$toast({ type: "success", message: "评论成功" });
                this.commentContent = ""
                this.showBox = false
            }).catch(err => {
                this.$toast({ type: "error", message: err.message });
            })

        },
        addEmoji(obj) {
            this.commentContent += obj.emoji;
        },
    },
}
</script>
<style lang="scss" scoped>
.comment-main {
    width: 100%;

    .comment-item {
        border: 1px solid rgba(144, 147, 153, .31);
        border-radius: 4px;
        padding: 10px;
        margin: 10px 0 10px;
        background-color: var(--comment-backgroud-color);

        .box {
            display: flex;

            .avatar {
                line-height: normal;
                position: relative;
                vertical-align: middle;

                img {
                    width: 40px;
                    width: 40px;
                    overflow: hidden;
                    border-radius: 50%;
                }
            }

            .ml-3 {
                margin-left: 12px !important;
                width: 100%;

                .comment-input {
                    position: relative;

                    .comment-textarea {
                        font-size: .875rem;
                        color: var(--article-content-color) !important;
                        outline: none;
                        padding: 10px 5px;
                        min-height: 122px;
                        resize: none;
                        width: 100%;
                        border-radius: 4px;
                        background-color: transparent;
                        border-style: none;

                    }
                }

                .emoji-container {
                    display: flex;
                    align-items: center;
                    margin: 10px 0;

                    .emoji-btn,
                    .emoji-btn-active {
                        cursor: pointer;

                        .iconfont {
                            font-size: 1.3rem;
                            color: var(--comment-emoji-color);
                        }
                    }

                    .emoji-btn-active {
                        .iconfont {
                            color: var(--theme-color);
                        }
                    }

                    .v-comment-btn {
                        border: 1px solid var(--border-line);
                        border-radius: 4px;
                        text-align: center;
                        padding: 6px 16px;
                        font-size: 14px;
                        transition: all .3s;
                        outline: none;
                        cursor: pointer;
                    }

                    .cancle-btn {
                        color: #fff;
                        margin-left: 1rem;
                        background-color: gray;
                        position: relative;
                    }


                    @media screen and (max-width: 767px) {
                        .upload-btn {

                            margin-left: auto;
                            color: #fff;
                            background-color: var(--theme-color);
                            position: relative;
                        }
                    }

                    @media screen and (min-width: 768px) {
                        .upload-btn {

                            margin-left: auto;
                            color: #fff;
                            background-color: var(--theme-color);
                            overflow: hidden;
                            transition: all .5s;
                            position: relative;

                            &:hover {
                                background-color: #00C853;
                            }

                            &:hover::before {
                                left: 120%;
                                transition: all .5s;
                            }

                            &::before {
                                content: '';
                                position: absolute;
                                top: 0;
                                left: -40%;
                                width: 20%;
                                height: 100%;
                                background: linear-gradient(90deg, transparent, #fff, transparent);
                                transform: skew(-45deg);
                            }
                        }
                    }
                }

                .emoji-wrapper {
                    max-height: 150px;
                    overflow-y: auto;

                    .emoji-item {
                        cursor: pointer;
                        display: inline-block;

                        .emoji {
                            font-size: 20px;
                            padding: 3px;

                            &:hover {
                                background-color: rgb(221, 221, 221)
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>