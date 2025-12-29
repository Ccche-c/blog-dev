<template>
    <div class="about-box">
        <v-card class="box1">
            <h1 class="title">关于本站</h1>

            <v-md-preview class="content" :text="$store.state.webSiteInfo.aboutMe" ref="preview" />
        </v-card>
        <Sidebar />
    </div>
</template>
<script>
import Sidebar from '@/components/sidebar/index.vue'
export default {
    components: {
        Sidebar
    },
    created() {
        document.title = "关于本站";
    },
    data() {
        return {
        }
    },

}
</script>

<style lang="scss" scoped>
@media screen and (max-width: 1118px) {
    .about-box {
        display: flex;

        .box1 {
            background-color: var(--background-color);
            margin-top: 60px;
            padding: 20px;
            height: 100%;
            width: 100%;

            .title {
                font-size: 24px;
                color: var(--article-color);
                text-align: center;
                padding-top: 20px;
                margin-bottom: 15px;
                word-break: break-word;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                font-weight: 500;
            }

            .content {
                color: var(--article-color);
            }
        }
    }
}

@media screen and (min-width: 1119px) {
    .about-box {
        display: flex;
        justify-content: center;
        position: relative;

        .box1 {
            background-color: var(--background-color);
            border-radius: 12px;
            padding: 20px;
            height: 100%;
            width: 55%;
            margin-top: 80px;

            &:hover {
                box-shadow: 5px 4px 8px 6px rgba(7, 17, 27, .06);
                transition: all .3s;
            }

            .title {
                font-size: 24px;
                color: var(--article-color);
                text-align: center;
                padding-top: 20px;
                margin-bottom: 15px;
                word-break: break-word;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
                font-weight: 500;
            }

            .content {
                color: var(--article-color);
            }
        }
    }
}
</style>