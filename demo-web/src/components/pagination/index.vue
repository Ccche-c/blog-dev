<template>
    <div class="pagination">
        <ul class="pager clear" @click="onPage">
            <li :class="{ disabled: page <= 1 }" :aria-disabled="page <= 1" :arial-label="arialLabel(-1)" tabindex="0"
                class="pn prev" data-page="-1" role="button">
                <span class="arrow"></span>
            </li>
            <template v-for="(group, index) in slices">
                <li v-if="group[0] === -1" :key="'g' + index" :data-page="group[1]" :data-jumper="index"
                    :arial-label="arialLabel(group[1])" class="pn jumper">
                    <span class="dots">...</span>
                </li>
                <li v-for="i in group" v-if="group[0] !== -1" :key="'l' + i" :class="{ active: page === i }" :data-page="i"
                    :arial-label="arialLabel(i)" class="pn" role="button">
                    <span>{{ i }}</span>
                </li>
            </template>
            <li :class="{ disabled: page >= pages }" :aria-disabled="page >= pages" :arial-label="arialLabel(0)"
                tabindex="0" class="pn next" data-page="0" role="button">
                <span class="arrow"></span>
            </li>
        </ul>
    </div>
</template>
 
<script>

export default {
    name: 'Pagination',
    props: {
        page: {
            type: Number,
            default: 1,
        },
        total: {
            type: Number,
            default: 0,
        },
        pageSize: {
            type: Number,
            default: 10,
        },
        onPageChange: {
            type: Function,
            default: () => { },
        },

    },
    data() {
        return {
            pages: 0,
            slices: [[1]],
        };
    },
    watch: {
        page() {
            this.updateSlices();
        },
        total() {
            this.updateSlices();
        },

    },
    mounted() {
        this.updateSlices();
    },
    methods: {
        arialLabel(i) {
            if (i === -1) {
                return '上一页';
            }
            if (i === 0) {
                return '下一页';
            }
            return `第${i}页`;
        },
        updateSlices() {
            const pages = this.pages = Math.ceil(this.total / this.pageSize);
            if (pages < 5) {
                this.slices = [Array(pages).fill(1).map((o, i) => i + 1)];
            } else if (this.page < 4) {
                this.slices = [
                    [1, 2, 3],
                    [-1, 4],
                    [pages],
                ];
            } else if (pages - this.page < 3) {
                this.slices = [
                    [1],
                    [-1, 2],
                    [pages - 2, pages - 1, pages],
                ];
            } else {
                this.slices = [
                    [1],
                    [-1, 2],
                    [this.page - 1, this.page, this.page + 1],
                    [-1, this.page + 2],
                    [pages],
                ];
            }
        },
        onPage(e) {
            let target = e.target;
            if (target.tagName === 'SPAN') {
                target = target.parentElement;
            }
            if (target.className.indexOf('disabled') !== -1 || target.tagName !== 'LI') {
                return;
            }

            const page = +target.getAttribute('data-page');
            const jumper = target.getAttribute('data-jumper');
            if (jumper) {
                // this.showJumper(+jumper, target)
                this.onPageChange(page);
            } else {
                this.onPageChange(this.calcNextPage(page));
            }
        },
        calcNextPage(page) {
            return !page ? (this.page + 1) : page < 0 ? (this.page - 1) : page;
        },
    },
};
</script>

<style lang="scss" scoped>
li {
    list-style: none;
}

.pagination {
    font-size: 14px;
    color: #999;
    letter-spacing: 1.8px;
    font-weight: 400;
    line-height: 40px;
    text-align: center;
    margin: 0 auto;
    margin-top: 20px;

    &>.pager {
        display: inline-block;
        white-space: nowrap;
    }

    .pn {
        float: left;
        cursor: pointer;
        width: 40px;
        height: 40px;
        line-height: 40px;
        margin-left: 24px;
        text-align: center;
        background: var(--background-color);
        border-radius: 2px;
        font-size: 18px;
        color: #999999;
        letter-spacing: 1.8px;
        font-weight: 400;
        outline: none;
        box-shadow: 0 0 3px 0 #cacaca;
        border-radius: 5px;

        &:first-child {
            margin-left: 0;
        }

        &:hover:not(.disabled) {
            background-color: var(--theme-color);
            color: var(--background-color);
        }

        &.active.active {
            background-color: var(--theme-color);
            border-color: var(--theme-color);
            color: var(--background-color);
        }

        &>.dots {
            display: block;
            font-weight: bold;
            line-height: 30px;
            padding-bottom: 6px;
        }


        &.prev,
        &.next {
            color: #999999;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        &.prev {
            .arrow {
                width: 8px;
                height: 8px;
                display: block;
                margin-left: 4px;
                border-left: solid 1px currentColor;
                border-top: solid 1px currentColor;
                -webkit-transform: rotate(-45deg);
                transform: rotate(-45deg);
            }
        }

        &.next {
            .arrow {
                width: 8px;
                height: 8px;
                display: block;
                margin-right: 4px;
                border-bottom: solid 1px currentColor;
                border-right: solid 1px currentColor;
                -webkit-transform: rotate(-45deg);
                transform: rotate(-45deg);
            }
        }

        &.next,
        &.prev {
            color: var(--pagination-next-prev) !important;
            background: var(--background-color);
        }

        &.disabled {
            cursor: not-allowed;
            background: var(--background-color);
            color: #999 !important;
        }
    }


}
</style>