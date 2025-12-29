<template>
    <div class="photo-main">
        <div class="containner">
            <v-card class="titleBox">
                <div class="title">
                    <i class="iconfont icon-tupian"></i> 我的图库
                </div>
                <div class="category">
                    <div class="categoryItem" v-for="(item, index) in photoAlbumList" :key="index">
                        <a :class="index == 0 ? 'active' : 'name'" @click="handleClike(item.id, index)" ref="category">
                            {{ item.name }}</a>
                    </div>
                </div>
            </v-card>
            <div class="photoBox">
                <a href="" v-for="item in photoList" :key="item.id">
                    <img class="" :src="item.url" alt="" />
                    <span class="overlay">

                    </span>
                    <span class="desc">
                        {{ item.info }}
                    </span>

                </a>
            </div>
        </div>

        <Sidebar></Sidebar>
    </div>
</template>
<script>
import { featchPhotoAlbum, featchPhoto } from '@/api'
import Sidebar from '@/components/sidebar/index.vue'
export default {
    components: {
        Sidebar,
    },
    data() {
        return {
            linkList: [],
            photoAlbumList: [],
            pageData: {
                pageNo: 1,
                pageSize: 10,
                photoAlbumId: null
            },
            photoList: []
        }
    },
    created() {
        document.title = "我的图库";
        this.getPhotoAlbum()
        this.getPhotoList()
    },
    methods: {
        getPhotoAlbum() {
            featchPhotoAlbum().then(res => {
                this.photoAlbumList = res.data
                let photoAlbum = {
                    id: null,
                    name: "全部"
                }
                this.photoAlbumList.unshift(photoAlbum)
            })
        },
        getPhotoList() {
            featchPhoto(this.pageData).then(res => {
                this.photoList = res.data.records
            })
        },
        handleClike(phtotAlbumId, index) {
            for (var i = 0; i < this.$refs.category.length; i++) {
                this.$refs.category[i].className = "name"
            }
            this.$refs.category[index].className = "active"
            this.pageData.photoAlbumId = phtotAlbumId;
            this.getPhotoList()
        }

    }
}
</script>
<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .photo-main {
        display: flex;
        justify-content: center;

        .containner {
            margin-top: 60px;
            width: 100%;

            .titleBox {
                background-color: var(--background-color);
                height: auto;
                padding: 20px;

                .title {
                    position: absolute;
                    top: 15px;
                    left: -10px;
                    height: 30px;
                    padding: 0 12px;
                    color: #fff;
                    font-weight: 500;
                    line-height: 30px;
                    background: var(--theme-color);
                    border-radius: 2px 2px 2px 0;
                    box-shadow: 2px 5px 10px rgba(49, 58, 70, .15);
                    user-select: none;

                    &::before {
                        position: absolute;
                        bottom: -10px;
                        left: -10px;
                        border-color: var(--theme-color) transparent transparent;
                        border-style: solid;
                        border-width: 10px;
                        -webkit-transform: rotate(90deg);
                        transform: rotate(90deg);
                        content: "";
                    }
                }

                .category {
                    margin-top: 50px;
                    margin-bottom: 20px;

                    .categoryItem {
                        display: inline-block;
                        height: auto;
                        padding: 5px 5px;
                        cursor: pointer;
                        margin-right: 20px;
                        position: relative;
                        font-size: 14px;

                        .active {

                            color: var(--text-color);
                            font-weight: 700;

                            &::after {
                                content: "";
                                overflow: hidden;
                                display: block;
                                left: 0;
                                bottom: 0;
                                position: absolute;
                                width: 100%;
                                height: 3px;
                                background: var(--theme-color);


                            }
                        }

                        .name {
                            color: var(--text-color);

                            &::after {
                                content: "";
                                overflow: hidden;
                                display: block;
                                left: 0;
                                bottom: 0;
                                position: absolute;
                                width: 100%;
                                height: 2px;
                                background: var(--theme-color);
                                transform: scaleX(0);
                                transition: all 0.5s;
                            }

                            &:hover::after {
                                transform: scaleX(1);
                            }
                        }

                    }
                }
            }

            .photoBox {
                margin-top: 20px;

                a {
                    display: inline-block;
                    overflow: hidden;
                    min-height: 50px;
                    border-radius: 10px;
                    margin: 10px;
                    visibility: visible;
                    width: 120px;
                    height: 200px;
                    min-height: 50px;
                    overflow: hidden;


                    &:hover {
                        box-shadow: 5px 4px 8px 6px rgba(2, 6, 10, 0.06);
                        transition: all .3s;

                        img {
                            transform: scale(1.2);

                        }
                    }

                    img {
                        object-fit: cover;
                        width: 100%;
                        height: 100%;
                        transition: all 0.5s;
                    }
                }
            }
        }
    }
}

@media screen and (min-width: 1119px) {
    .photo-main {
        display: flex;
        justify-content: center;

        .containner {
            width: 55%;
            margin-top: 80px;

            .titleBox {
                background-color: var(--background-color);
                border-radius: 12px;
                height: auto;
                padding: 20px;

                &:hover {
                    box-shadow: 5px 4px 8px 6px rgba(2, 6, 10, 0.06);
                    transition: all .3s;
                }

                .title {
                    position: absolute;
                    top: 15px;
                    left: -10px;
                    height: 30px;
                    padding: 0 12px;
                    color: #fff;
                    font-weight: 500;
                    line-height: 30px;
                    background: var(--theme-color);
                    border-radius: 2px 2px 2px 0;
                    box-shadow: 2px 5px 10px rgba(49, 58, 70, .15);
                    user-select: none;

                    &::before {
                        position: absolute;
                        bottom: -10px;
                        left: -10px;
                        border-color: var(--theme-color) transparent transparent;
                        border-style: solid;
                        border-width: 10px;
                        -webkit-transform: rotate(90deg);
                        transform: rotate(90deg);
                        content: "";
                    }
                }

                .category {
                    margin-top: 50px;
                    margin-bottom: 20px;

                    .categoryItem {
                        display: inline-block;
                        height: auto;
                        padding: 5px 5px;
                        cursor: pointer;
                        margin-right: 20px;
                        position: relative;
                        font-size: 14px;

                        .active {

                            color: var(--text-color);
                            font-weight: 700;

                            &::after {
                                content: "";
                                overflow: hidden;
                                display: block;
                                left: 0;
                                bottom: 0;
                                position: absolute;
                                width: 100%;
                                height: 3px;
                                background: var(--theme-color);


                            }
                        }

                        .name {
                            color: var(--text-color);

                            &::after {
                                content: "";
                                overflow: hidden;
                                display: block;
                                left: 0;
                                bottom: 0;
                                position: absolute;
                                width: 100%;
                                height: 2px;
                                background: var(--theme-color);
                                transform: scaleX(0);
                                transition: all 0.5s;
                            }

                            &:hover::after {
                                transform: scaleX(1);
                            }
                        }

                    }
                }
            }

            .photoBox {
                margin-top: 20px;

                a {
                    display: inline-block;
                    overflow: hidden;
                    min-height: 50px;
                    border-radius: 10px;
                    margin: 10px;
                    visibility: visible;
                    width: 140px;
                    height: 200px;
                    min-height: 50px;
                    overflow: hidden;
                    position: relative;

                    .overlay {
                        background: rgba(0, 0, 0, .3);
                        position: absolute;
                        top: 0;
                        right: 0;
                        bottom: 0;
                        left: 0;
                        opacity: 0;
                    }

                    .desc {
                        z-index: 2;
                        position: absolute;
                        top: 0;
                        right: 0;
                        bottom: 0;
                        left: 0;
                        padding: 5px;
                        opacity: 0;
                        text-align: center;
                        color: var(--baise);

                    }

                    &:hover {
                        box-shadow: 5px 4px 8px 6px rgba(2, 6, 10, 0.06);
                        transition: all .3s;

                        .desc {
                            opacity: 1;
                            transition: all 0.5s;
                        }

                        .overlay {
                            opacity: 1;
                        }

                        img {
                            transform: scale(1.2);

                        }
                    }

                    img {
                        object-fit: cover;
                        width: 100%;
                        height: 100%;
                        transition: all 0.5s;
                    }
                }
            }
        }
    }
}
</style>