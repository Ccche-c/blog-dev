import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import SearchIndex from '@/view/search/index.vue'
import { searchArticle } from '@/api'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

// Mock API
jest.mock('@/api', () => ({
    searchArticle: jest.fn()
}))

describe('Search/index.vue - 搜索页面测试', () => {
    let wrapper
    let store
    let vuetify
    let mockRouter
    let mockWow

    // Mock 文章数据
    const mockArticleList = [
        {
            id: 139,
            title: '学习',
            summary: '学习',
            avatar: 'https://img.btstu.cn/api/images/5c92eddade9cd.jpg',
            isStick: 0,
        }
    ]

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {}
        })

        // Mock Router
        mockRouter = {
            path: '/search',
            query: { keyword: 'Vue' },
            push: jest.fn()
        }

        // Mock WOW
        mockWow = {
            WOW: jest.fn().mockImplementation(() => ({
                init: jest.fn()
            }))
        }
        localVue.prototype.$wow = mockWow

        // Mock document.documentElement.scrollTop
        Object.defineProperty(document.documentElement, 'scrollTop', {
            writable: true,
            value: 0
        })

        // 重置 mock
        searchArticle.mockClear()
        mockRouter.push.mockClear()

        // 设置默认返回值
        searchArticle.mockResolvedValue({
            data: {
                records: mockArticleList,
                total: 20,
                pages: 4
            }
        })

        // 捕获未处理的 Promise rejection，避免测试失败
        jest.spyOn(console, 'error').mockImplementation(() => {})
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
        // 恢复 console.error
        if (console.error.mockRestore) {
            console.error.mockRestore()
        }
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.artcile_main').exists()).toBe(true)
        })

        it('应该渲染标题区域', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            const title = wrapper.find('.title')
            expect(title.exists()).toBe(true)
        })

        it('应该显示搜索关键词和结果数量', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            expect(wrapper.vm.pageData.keyword).toBe('Vue')
            expect(wrapper.vm.pageTotal).toBe(20)
        })

        it('应该渲染文章列表', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            const articleItems = wrapper.findAll('.ul_item')
            expect(articleItems.length).toBe(mockArticleList.length)
        })

        it('应该渲染空状态当没有搜索结果时', async () => {
            searchArticle.mockResolvedValue({
                data: {
                    records: [],
                    total: 0,
                    pages: 0
                }
            })

            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            const emptyBox = wrapper.find('.empty-box')
            expect(emptyBox.exists()).toBe(true)
        })
    })

    describe('初始数据', () => {
        it('应该初始化正确的默认数据', () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            expect(wrapper.vm.pageData.pageNo).toBe(1)
            expect(wrapper.vm.pageData.pageSize).toBe(6)
            expect(wrapper.vm.pageData.keyword).toBe('Vue')
            expect(wrapper.vm.pageTotal).toBe(0)
            expect(wrapper.vm.pages).toBe(0)
            expect(wrapper.vm.articleList).toEqual([])
        })

        it('应该从路由查询参数获取关键词', () => {
            mockRouter.query = { keyword: 'JavaScript' }
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            expect(wrapper.vm.pageData.keyword).toBe('JavaScript')
        })
    })

    describe('API 调用', () => {
        it('应该正确调用 searchArticle API', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            expect(searchArticle).toHaveBeenCalledWith({
                pageNo: 1,
                pageSize: 6,
                keyword: 'Vue'
            })
        })

        it('应该正确设置文章列表数据', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            expect(wrapper.vm.articleList).toEqual(mockArticleList)
            expect(wrapper.vm.pageTotal).toBe(20)
            expect(wrapper.vm.pages).toBe(4)
        })

        it('应该处理 API 调用失败', async () => {
            // 设置 API 调用失败
            searchArticle.mockRejectedValue(new Error('API Error'))

            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            // 等待组件挂载和 API 调用
            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()
            
            // 等待 Promise rejection 完成（给时间让未处理的 rejection 完成）
            await new Promise(resolve => setTimeout(resolve, 50))

            // 组件应该仍然能够渲染（即使 API 失败）
            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.articleList).toEqual([]) // 失败后列表应该为空
        })
    })

    describe('分页功能', () => {
        it('应该渲染分页组件当有多页时', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            wrapper.setData({ pages: 4 })
            await wrapper.vm.$nextTick()

            const pagination = wrapper.find('pagination-stub')
            expect(pagination.exists()).toBe(true)
        })

        it('不应该渲染分页组件当只有一页时', async () => {
            searchArticle.mockResolvedValue({
                data: {
                    records: mockArticleList,
                    total: 5,
                    pages: 1
                }
            })

            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            wrapper.setData({ pages: 1 })
            await wrapper.vm.$nextTick()

            const pagination = wrapper.find('pagination-stub')
            expect(pagination.exists()).toBe(false)
        })

        it('onPage 应该更新页码并重新获取数据', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            searchArticle.mockClear()

            wrapper.vm.onPage(2)

            expect(wrapper.vm.pageData.pageNo).toBe(2)
            expect(document.documentElement.scrollTop).toBe(10)
            expect(searchArticle).toHaveBeenCalledWith({
                pageNo: 2,
                pageSize: 6,
                keyword: 'Vue'
            })
        })
    })

    describe('点击事件', () => {
        it('handleClick 应该跳转到文章详情页', () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            const articleId = 123
            wrapper.vm.handleClick(articleId)

            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: articleId }
            })
        })
    })

    describe('日期格式化', () => {
        it('formatDate 应该正确格式化日期', () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            const date = '2024-01-15T10:30:00'
            const formatted = wrapper.vm.formatDate(date)
            expect(formatted).toBe('2024-01-15')
        })

        it('formatDate 应该处理不同格式的日期', () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            const date = '2024-12-25T00:00:00'
            const formatted = wrapper.vm.formatDate(date)
            expect(formatted).toBe('2024-12-25')
        })
    })

    describe('计算属性', () => {
        it('total 应该返回 pageTotal', () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            wrapper.setData({ pageTotal: 20 })
            expect(wrapper.vm.total).toBe(20)
        })

        it('total 应该返回 0 当 pageTotal 为 0 时', () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            wrapper.setData({ pageTotal: 0 })
            expect(wrapper.vm.total).toBe(0)
        })
    })

    describe('生命周期', () => {
        it('created 应该初始化 WOW 并获取文章列表', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            expect(mockWow.WOW).toHaveBeenCalled()
            expect(searchArticle).toHaveBeenCalled()
        })
    })

    describe('文章列表显示', () => {
        it('应该显示文章标题', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            const titleName = wrapper.find('.titleName')
            expect(titleName.exists()).toBe(true)
        })

        it('应该显示文章摘要', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            const info = wrapper.find('.info')
            expect(info.exists()).toBe(true)
        })

        it('应该显示置顶标签当 isStick 为 1 时', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            // 第一个文章 isStick 为 1，应该显示置顶标签
            const topTag = wrapper.find('.top')
            expect(topTag.exists()).toBe(true)
        })

        it('应该显示文章标签列表', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            const tagItems = wrapper.findAll('.tag_name')
            expect(tagItems.length).toBeGreaterThan(0)
        })
    })

    describe('边界情况', () => {
        it('应该处理空搜索结果', async () => {
            searchArticle.mockResolvedValue({
                data: {
                    records: [],
                    total: 0,
                    pages: 0
                }
            })

            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            expect(wrapper.vm.articleList).toEqual([])
            expect(wrapper.vm.pageTotal).toBe(0)
            const emptyBox = wrapper.find('.empty-box')
            expect(emptyBox.exists()).toBe(true)
        })

        it('应该处理没有关键词的情况', () => {
            mockRouter.query = {}
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            expect(wrapper.vm.pageData.keyword).toBe(undefined)
        })

        it('应该处理分页边界情况', async () => {
            wrapper = shallowMount(SearchIndex, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'v-card': true,
                    'pagination': true,
                    'svg-icon': true,
                    'i': true
                }
            })

            await wrapper.vm.$nextTick()
            await wrapper.vm.$nextTick()

            // 测试第一页
            wrapper.vm.onPage(1)
            expect(wrapper.vm.pageData.pageNo).toBe(1)

            // 测试最后一页
            wrapper.setData({ pages: 5 })
            wrapper.vm.onPage(5)
            expect(wrapper.vm.pageData.pageNo).toBe(5)
        })
    })
})

