<template>
    <div class="sidebar">
        <SiteInfo />
        <Notice />
        <!-- 移除讨论组二维码展示 -->
        <!-- <Group /> -->
        <Countdown />
        <NewlyArticle />
        <TagCloud />
    </div>
</template>
<script>
import SiteInfo from '../site/index.vue'
import Notice from '../notice/index.vue'
import Countdown from '../countdown/index.vue'
import NewlyArticle from '@/view/article/NewlyArticle.vue'
import TagCloud from '../tag/index.vue'
export default {
    components: {
        SiteInfo,
        Notice,
        Countdown,
        NewlyArticle,
        TagCloud
    },
    data() {
        return {

        }
    }

}
</script>
<style lang="scss" scoped>
@media screen and (min-width: 1118px) {
    .sidebar {
        margin-left: 20px;
    }
}


@media screen and (max-width: 1119px) {
    .sidebar {
        display: none;
    }
}
</style>