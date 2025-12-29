<template>
    <div class="sponsor-main">
        <div class="sponsor-warpper">
            <div class="wapper">
                <div class="desc">
                    <h1>赞助排行榜</h1>
                    <p>感谢大家对网站的支持，每一笔赞助都将投放到网站的体验升级中</p>
                    <div class="btn" @click="handleShow" v-show="!isShow">网站很不错，打赏一下</div>
                    <div class="qr" v-show="isShow">
                        <div>
                            <img :src="img" alt="">
                            <p>微信扫码, 赞助<span style="color: red;"> {{ payData.price }} </span>元</p>
                        </div>
                        <div class="price-wapper">
                            <div>
                                <span class="price-item" @click="handleClike(1, 0)">1元</span>
                                <span class="price-item" @click="handleClike(2, 1)">2元</span>
                                <span class="price-item" @click="handleClike(5, 2)">5元</span>
                                <span class="price-item" @click="handleClike(10, 3)">10元</span>
                                <span class="price-item" @click="handleClike(20, 4)">20元</span>
                                <span class="price-item" @click="handleClike(50, 5)">50元</span>
                                <span class="price-item" @click="handleClike(100, 6)">100元</span>
                                <span class="price-item">
                                    <input type="text" placeholder="自定义">
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
                <img class="cover" src="http://img.shiyit.com/cover.png" alt="">
            </div>
            <v-card class="content">
                <div class="item">
                    <span class="index">1</span>
                    <div class="item-warpper">
                        <span class="avatar">
                            <v-avatar color="primary" size="32">
                                <img alt="Avatar" src="http://img.shiyit.com/1645512111007.png">
                            </v-avatar>
                        </span>
                        <span class="user">*。</span>
                        <span class="price">赞助了 20 元</span>
                        <span class="time">2023-05-26</span>
                    </div>
                </div>
                <div class="item">
                    <span class="index">2</span>
                    <div class="item-warpper">
                        <span class="avatar">
                            <v-avatar color="primary" size="32">
                                <img alt="Avatar" src="http://img.shiyit.com/shiyi.png">
                            </v-avatar>
                        </span>
                        <span class="user">*跑</span>
                        <span class="price">赞助了 10 元</span>
                        <span class="time">2023-06-05</span>
                    </div>
                </div>
                <div class="item">
                    <span class="index">3</span>
                    <div class="item-warpper">
                        <span class="avatar">
                            <v-avatar color="primary" size="32">
                                <img alt="Avatar" src="http://img.shiyit.com/1645512111007.png">
                            </v-avatar>
                        </span>
                        <span class="user">拾壹</span>
                        <span class="price">赞助了 0.1 元</span>
                        <span class="time">2023-05-30</span>
                    </div>
                </div>
            </v-card>
        </div>
    </div>
</template>
<script>
import { getPayUrl } from '@/api'
import { getUser } from '@/utils/auth'
export default {
    data() {
        return {
            payData: {
                price: 1,
                payType: 1
            },
            img: null,
            isShow: false,
            user: getUser(),
        }
    },
    methods: {
        getPayUrl() {
            getPayUrl(this.payData).then(res => {
                this.img = res.data.payUrl
            })
        },
        handleClike(val, index) {
            let el = document.getElementsByClassName("price-item")
            Array.prototype.forEach.call(el, function (element) {
                element.className = "price-item"
            });

            el[index].className += " active";
            this.payData.price = val
            this.getPayUrl()
        },
        handleShow() {
            if (!this.user) {
                this.$store.state.loginFlag = true
                return;
            }
            this.isShow = true;
            getPayUrl(this.payData).then(res => {
                this.img = res.data.payUrl
            }).catch(res => {
                this.$toast({ type: "error", message: err.message });
            })
        }
    }
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .sponsor-main {
        display: flex;
        justify-content: center;
        position: relative;

        .sponsor-warpper {
            position: relative;
            margin-top: 60px;
            width: 100%;

            .wapper {
                position: relative;
                background-color: #1ccae5;
                height: 410px;

                .desc {
                    position: absolute;
                    top: 60px;
                    left: 40px;
                    color: #fff;
                    padding: 10px;

                    h1 {
                        margin-bottom: 20px;
                        font-size: 3.5rem;
                    }

                    .btn {
                        position: absolute;
                        top: 200px;
                        left: 10px;
                        width: 190px;
                        height: 35px;
                        background-color: #fff;
                        color: #333;
                        padding: 7px;
                        padding-left: 15px;
                        border-radius: 25px;
                        cursor: pointer;
                    }

                    .qr {
                        position: relative;
                        display: flex;
                        margin-top: 15px;

                        img {
                            width: 200px;
                            height: 200px;
                        }

                        p {
                            position: absolute;
                            top: 175px;
                            color: #333;
                            left: 30px;
                            font-size: 0.9rem;
                        }

                        .price-wapper {
                            float: right;
                            margin-left: 10px;

                            div {
                                display: grid;
                                grid-template-rows: repeat(2, 1fr);
                                grid-template-columns: repeat(2, 1fr);

                                .price-item {
                                    margin-left: 10px;
                                    margin-bottom: 15px;
                                    display: inline-block;
                                    width: 80px;
                                    height: 35px;
                                    border-radius: 5px;
                                    background-color: #fff;
                                    text-align: center;
                                    line-height: 35px;
                                    border: 1px solid #1ccae5;
                                    color: #1ccae5;
                                    cursor: pointer;

                                    input {
                                        width: 100%;
                                        padding-left: 5px;
                                    }
                                }
                            }
                        }

                    }
                }

                .cover {
                    display: none;
                }
            }

            .content {
                margin-top: 10px;
                background-color: var(--background-color);

                .item {
                    text-align: center;
                    width: 100%;
                    margin: 0 auto;
                    display: flex;
                    align-items: center;
                    padding: 20px;

                    .index,
                    .item-warpper {
                        background-color: #1ccae5;
                        color: #fff;

                    }

                    .index {
                        width: 35px;
                        height: 35px;
                        line-height: 35px;
                    }

                    .item-warpper {
                        margin-left: 20px;
                        border-radius: 50px;
                        padding: 5px;
                        display: flex;
                        width: 100%;
                        align-items: center;
                        position: relative;


                        .user {
                            margin: 0 10px;
                        }

                        .price {
                            position: absolute;
                            top: 0;
                            left: 0;
                            bottom: 0;
                            right: 0;
                            line-height: 40px;
                            font-weight: 700;
                        }

                        .time {
                            position: absolute;
                            right: 0;
                            font-size: 13px;
                            margin: 0 10px;
                        }
                    }
                }
            }


        }
    }
}

@media screen and (min-width: 1119px) {
    .sponsor-main {
        display: flex;
        justify-content: center;
        position: relative;

        .sponsor-warpper {
            position: relative;
            margin-top: 80px;
            width: 80%;

            .wapper,
            .content {
                border-radius: 12px;
            }

            .wapper {
                position: relative;
                background-color: #1ccae5;
                height: 410px;

                &:hover {
                    box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                    transition: all .3s;
                }

                .desc {
                    position: absolute;
                    top: 60px;
                    left: 80px;
                    color: #fff;
                    padding: 10px;

                    h1 {
                        margin-bottom: 20px;
                        font-size: 3.5rem;
                    }

                    .btn {
                        position: absolute;
                        top: 200px;
                        left: 10px;
                        width: 190px;
                        height: 35px;
                        background-color: #fff;
                        color: #333;
                        padding: 7px;
                        padding-left: 15px;
                        border-radius: 25px;
                        cursor: pointer;
                    }

                    .qr {
                        position: relative;
                        display: flex;
                        margin-top: 15px;
                        z-index: 10;

                        img {
                            width: 200px;
                            height: 200px;
                        }

                        p {
                            position: absolute;
                            top: 175px;
                            color: #333;
                            left: 30px;
                            font-size: 0.9rem;
                        }

                        .price-wapper {
                            float: right;
                            margin-left: 10px;

                            div {
                                display: grid;
                                grid-template-rows: repeat(2, 1fr);
                                grid-template-columns: repeat(2, 1fr);



                                .price-item {
                                    margin-left: 10px;
                                    margin-bottom: 15px;
                                    display: inline-block;
                                    width: 80px;
                                    height: 35px;
                                    border-radius: 5px;
                                    background-color: #fff;
                                    text-align: center;
                                    line-height: 35px;
                                    border: 1px solid #1ccae5;
                                    color: #1ccae5;
                                    cursor: pointer;

                                    &:hover {
                                        background-color: var(--theme-color);
                                        color: #fff;
                                    }

                                    input {
                                        width: 100%;
                                        padding-left: 5px;
                                        cursor: pointer;
                                    }
                                }

                                .active {
                                    background-color: var(--theme-color);
                                    color: #fff;
                                }
                            }
                        }

                    }
                }

                .cover {
                    position: absolute;
                    right: 5px;
                    bottom: 0;
                    height: 370px;
                }
            }

            .content {
                margin-top: 20px;
                background-color: var(--background-color);

                &:hover {
                    box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                    transition: all .3s;
                }

                .item {
                    text-align: center;
                    width: 60%;
                    margin: 0 auto;
                    display: flex;
                    align-items: center;
                    padding: 20px;

                    .index,
                    .item-warpper {
                        background-color: #1ccae5;
                        color: #fff;

                    }

                    .index {
                        width: 35px;
                        height: 35px;
                        line-height: 35px;
                    }

                    .item-warpper {
                        margin-left: 20px;
                        border-radius: 50px;
                        padding: 5px;
                        display: flex;
                        width: 100%;
                        align-items: center;
                        position: relative;


                        .user {
                            margin: 0 10px;
                        }

                        .price {
                            position: absolute;
                            top: 0;
                            left: 0;
                            bottom: 0;
                            right: 0;
                            line-height: 40px;
                            font-weight: 700;
                        }

                        .time {
                            position: absolute;
                            right: 0;
                            font-size: 13px;
                            margin: 0 10px;
                        }
                    }
                }
            }


        }
    }
}
</style>