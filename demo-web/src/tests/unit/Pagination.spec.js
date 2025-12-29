import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import Pagination from '@/components/pagination/index.vue'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

describe('Pagination.vue - 组件渲染测试', () => {
    let wrapper
    let store
    let vuetify
    let mockOnPageChange

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {}
        })

        // Mock onPageChange 回调函数
        mockOnPageChange = jest.fn()
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
        mockOnPageChange.mockClear()
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 0,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.pagination').exists()).toBe(true)
        })

        it('应该渲染分页容器', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 0,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const pagination = wrapper.find('.pagination')
            expect(pagination.exists()).toBe(true)
        })

        it('应该渲染分页列表', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const pager = wrapper.find('.pager')
            expect(pager.exists()).toBe(true)
        })

        it('应该渲染上一页按钮', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 2,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const prevButton = wrapper.find('.prev')
            expect(prevButton.exists()).toBe(true)
        })

        it('应该渲染下一页按钮', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const nextButton = wrapper.find('.next')
            expect(nextButton.exists()).toBe(true)
        })

        it('应该包含所有必要的 CSS 类名', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.find('.pagination').exists()).toBe(true)
            expect(wrapper.find('.pager').exists()).toBe(true)
        })
    })

    describe('分页按钮显示', () => {
        it('应该显示页码按钮当有数据时', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const pageButtons = wrapper.findAll('.pn')
            expect(pageButtons.length).toBeGreaterThan(0)
        })

        it('应该显示省略号当页数较多时', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 5,
                    total: 100,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const jumper = wrapper.find('.jumper')
            expect(jumper.exists()).toBe(true)
        })

        it('应该标记当前页为 active', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 3,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const activeButton = wrapper.find('.active')
            expect(activeButton.exists()).toBe(true)
        })

        it('应该禁用上一页按钮当在第一页时', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const prevButton = wrapper.find('.prev')
            expect(prevButton.classes()).toContain('disabled')
        })

        it('应该禁用下一页按钮当在最后一页时', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 5,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const nextButton = wrapper.find('.next')
            expect(nextButton.classes()).toContain('disabled')
        })
    })

    describe('空数据渲染', () => {
        it('应该能够处理总数为 0 的情况', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 0,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.pages).toBe(0)
        })

        it('应该能够处理单个页面的情况', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 5,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.pages).toBe(1)
        })

        it('应该能够处理多个页面的情况', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 100,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.pages).toBe(10)
        })
    })

    describe('初始数据', () => {
        it('应该初始化正确的默认数据', () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 0,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            expect(wrapper.vm.pages).toBe(0)
            expect(wrapper.vm.slices).toEqual([[1]])
        })

        it('应该使用默认的 page 值', () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    total: 0,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            expect(wrapper.vm.page).toBe(1)
        })

        it('应该使用默认的 pageSize 值', () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 0,
                    onPageChange: mockOnPageChange
                }
            })

            expect(wrapper.vm.pageSize).toBe(10)
        })
    })

    describe('组件结构', () => {
        it('应该正确渲染组件结构', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const pagination = wrapper.find('.pagination')
            expect(pagination.exists()).toBe(true)

            const pager = wrapper.find('.pager')
            expect(pager.exists()).toBe(true)
        })

        it('应该正确使用 v-for 渲染页码', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 30,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const pageButtons = wrapper.findAll('.pn')
            expect(pageButtons.length).toBeGreaterThan(0)
        })
    })

    describe('响应式数据更新', () => {
        it('应该响应 page prop 的变化', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            const initialActive = wrapper.find('.active')
            expect(initialActive.exists()).toBe(true)

            // 更新 page
            wrapper.setProps({
                page: 2
            })

            await wrapper.vm.$nextTick()

            // 应该更新 slices
            expect(wrapper.vm.page).toBe(2)
        })

        it('应该响应 total prop 的变化', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.vm.pages).toBe(5)

            // 更新 total
            wrapper.setProps({
                total: 100
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.vm.pages).toBe(10)
        })
    })

    describe('边界情况', () => {
        it('应该处理 page 为 0 的情况', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 0,
                    total: 50,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
        })

        it('应该处理 pageSize 为 0 的情况', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 1,
                    total: 50,
                    pageSize: 0,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
        })

        it('应该处理大量页面的情况', async () => {
            wrapper = shallowMount(Pagination, {
                localVue,
                store,
                vuetify,
                propsData: {
                    page: 50,
                    total: 1000,
                    pageSize: 10,
                    onPageChange: mockOnPageChange
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.pages).toBe(100)
        })
    })
})

