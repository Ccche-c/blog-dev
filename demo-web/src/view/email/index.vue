<template>
    <div class="email-main">
        <v-card class="warpper">
            <div class="introduce">
                时光邮局介绍:顾名思义就是跨越时间的邮件。在这里你可以编写一封邮件，未来的某一天你的邮箱将收到这封邮件
            </div>
            <div class="title">
                <div class="left">
                    <span class="btn active">
                        最新
                    </span>
                    <span class="btn">
                        已发送
                    </span>
                    <span class="btn">
                        未发送
                    </span>
                </div>

                <a href="" class="send">
                    写一封信
                </a>
            </div>
            <div class="content-warpper">
                <ul class="content-items">
                    <li class="item" v-for="(item, index) in list" :key="index">
                        <div class="user">
                            <span class="left">
                                投递人: {{ item.nickname }} <span>{{ item.emal }}</span>
                            </span>
                            <span class="rigth">
                                {{ item.startTime }} 寄往 {{ item.endTime }}
                                <span v-if="item.status" style="color: #009688;">【发送成功】</span>
                                <span v-else style="color: #F44336;">【尚未发送】</span>
                            </span>
                        </div>
                        <div class="content">
                            {{ item.content }}
                        </div>
                    </li>

                </ul>
            </div>

            <!-- 分页按钮 -->
            <pagination v-if="pageTotal > 0" :page="pageData.pageNo" :page-size="pageData.pageSize" :total="total"
                :on-page-change="onPage" class="pagi page-content" />
        </v-card>

    </div>
</template>
<script>
import pagination from '@/components/pagination/index.vue'
export default {
    components: {
        pagination
    },
    data() {
        return {
            pageData: {
                pageNo: 1,
                pageSize: 6,
            },
            pageTotal: 1,
            list: [
                {
                    id: 1,
                    nickname: "许**",
                    email: "12*****@qq.com",
                    startTime: "2023-05-29 16:31",
                    endTime: "2023-06-01 16:36",
                    content: "爱你，不管怎么说，明天更爱你",
                    status: 0
                },
                {
                    id: 2,
                    nickname: "成**",
                    email: "13*****@qq.com",
                    startTime: "2023-06-01 16:35",
                    endTime: "2023-06-05 16:49",
                    content: "爱你，不管怎么说，明天更爱你",
                    status: 1
                }
            ]
        }
    },
    created() {
        document.title = "时光邮局"
    },
    computed: {
        total() {
            return this.pageTotal || 0;
        },
    },
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .email-main {
        display: flex;
        justify-content: center;
        position: relative;

        .warpper {
            border-radius: 12px;
            width: 100%;
            height: 100%;
            padding: 10px;
            margin-top: 80px;
            border-radius: 12px;

            .introduce {
                color: red;
                margin-bottom: 20px;
                margin-top: 10px;
                border-bottom: 1px solid var(--border-line);
                padding-bottom: 20px;
            }

            .title {
                border-bottom: 1px solid var(--border-line);
                padding-bottom: 10px;

                .left {
                    border: 1px solid #ccc;
                    display: inline-block;
                    border-radius: 3px;
                }

                .btn {
                    display: inline-block;
                    font-size: 14px;
                    height: 30px;
                    line-height: 30px;
                    padding: 0 15px;
                    border-right: 1px solid #ccc;
                    background-color: var(--background-color);
                    color: var(--text-color);
                    cursor: pointer;

                    &:last-child {
                        border-right: 0
                    }

                    &:hover {
                        background-color: var(--theme-color);
                        color: var(--baise);
                    }
                }

                .send {
                    float: right;
                    text-decoration: none;
                    color: var(--theme-color);
                }

                .active {
                    background-color: var(--theme-color);
                    color: var(--baise);
                }
            }

            .content-warpper {

                .content-items {
                    list-style: none;
                    padding: 10px;

                    .item {
                        border-bottom: 1px solid var(--border-line);
                        margin-bottom: 20px;

                        .user {
                            display: flex;
                            position: relative;

                            .left {
                                cursor: pointer;
                                color: var(--theme-color);
                            }

                            .rigth {
                                position: absolute;
                                top: 0;
                                right: 0;
                                color: var(--text-color);
                            }
                        }

                        .content {
                            margin-bottom: 10px;
                            margin-top: 20px;
                            color: var(--text-color);
                        }


                    }
                }
            }
        }


    }
}

@media screen and (min-width: 1119px) {
    .email-main {
        display: flex;
        justify-content: center;
        position: relative;

        .warpper {
            border-radius: 12px;
            width: 80%;
            height: 100%;
            padding: 10px;
            margin-top: 80px;
            border-radius: 12px;
            background-color: var(--background-color);

            &:hover {
                box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                transition: all .3s;
            }

            .introduce {
                color: red;
                margin-bottom: 20px;
                margin-top: 10px;
                border-bottom: 1px solid var(--border-line);
                padding-bottom: 20px;
            }

            .title {
                border-bottom: 1px solid var(--border-line);
                padding-bottom: 10px;

                .left {
                    border: 1px solid #ccc;
                    display: inline-block;
                    border-radius: 3px;
                }

                .btn {
                    display: inline-block;
                    font-size: 14px;
                    height: 30px;
                    line-height: 30px;
                    padding: 0 15px;
                    border-right: 1px solid #ccc;
                    background-color: var(--background-color);
                    color: var(--text-color);
                    cursor: pointer;

                    &:last-child {
                        border-right: 0
                    }

                    &:hover {
                        background-color: var(--theme-color);
                        color: var(--baise);
                    }
                }

                .send {
                    float: right;
                    text-decoration: none;
                    color: var(--theme-color);
                }

                .active {
                    background-color: var(--theme-color);
                    color: var(--baise);
                }
            }

            .content-warpper {

                .content-items {
                    list-style: none;
                    padding: 10px;

                    .item {
                        border-bottom: 1px solid var(--border-line);
                        margin-bottom: 20px;

                        .user {
                            display: flex;
                            position: relative;

                            .left {
                                cursor: pointer;
                                color: var(--theme-color);
                            }

                            .rigth {
                                position: absolute;
                                top: 0;
                                right: 0;
                                color: var(--text-color);
                            }
                        }

                        .content {
                            margin-bottom: 10px;
                            margin-top: 20px;
                            color: var(--text-color);
                        }


                    }
                }
            }
        }


    }
}
</style>