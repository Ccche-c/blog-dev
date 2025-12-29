import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import Search from '@/components/layout/Search.vue'
import { logout } from '@/api'
import { getUser, removeUser } from '@/utils/auth'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

// Mock API
jest.mock('@/api', () => ({
    logout: jest.fn()
}))

// Mock 工具函数
jest.mock('@/utils/auth', () => ({
    getUser: jest.fn(),
    removeUser: jest.fn()
}))

describe('Search.vue - 搜索组件测试', () => {
    let wrapper
    let store
    let vuetify
    let mockToast
    let mockRouter

    // Mock 标签云数据
    const mockTagCloud = [
        { id: 1, name: 'Vue' },
        { id: 2, name: 'JavaScript' },
        { id: 3, name: 'React' },
        { id: 4, name: 'Node.js' }
    ]

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {
                searchDrawer: false,
                tagCloud: mockTagCloud
            }
        })

        // Mock Router
        mockRouter = {
            path: '/',
            query: {},
            push: jest.fn(),
            go: jest.fn()
        }

        // Mock Toast
        mockToast = jest.fn()
        localVue.prototype.$toast = mockToast

        // Mock document.scrollTop
        Object.defineProperty(document, 'scrollTop', {
            writable: true,
            value: 0
        })

        // 重置 mock
        logout.mockClear()
        getUser.mockClear()
        removeUser.mockClear()
        mockToast.mockClear()
        mockRouter.push.mockClear()
        mockRouter.go.mockClear()

        // 设置默认返回值
        logout.mockResolvedValue({ data: {} })
        getUser.mockReturnValue(null)
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.search-container').exists()).toBe(true)
        })

        it('应该渲染遮罩层', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const mask = wrapper.find('.mask')
            expect(mask.exists()).toBe(true)
        })

        it('应该渲染搜索框', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const searchInput = wrapper.find('.searchInput')
            expect(searchInput.exists()).toBe(true)
            expect(searchInput.attributes('placeholder')).toBe('请输入关键字...')
        })

        it('应该渲染搜索按钮', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const commitButton = wrapper.find('.commit')
            expect(commitButton.exists()).toBe(true)
        })

        it('应该渲染标签搜索区域', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const tagWrapper = wrapper.find('.tag-wrapper')
            expect(tagWrapper.exists()).toBe(true)
        })

        it('应该渲染标签列表', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const tagItems = wrapper.findAll('.tag .item')
            expect(tagItems.length).toBe(mockTagCloud.length)
        })
    })

    describe('初始数据', () => {
        it('应该初始化正确的默认数据', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            expect(wrapper.vm.keywords).toBe(null)
            expect(wrapper.vm.tagCloudList).toEqual([])
            expect(wrapper.vm.img).toBe(process.env.VUE_APP_IMG_API)
        })
    })

    describe('搜索功能', () => {
        it('应该能够输入搜索关键词', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: 'Vue' })
            expect(wrapper.vm.keywords).toBe('Vue')
        })

        it('应该能够执行搜索', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: 'Vue' })
            wrapper.vm.search()

            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/search',
                query: { keyword: 'Vue' }
            })
            expect(store.state.searchDrawer).toBe(false)
        })

        it('搜索时应该验证关键词不能为空', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: null })
            wrapper.vm.search()

            expect(mockRouter.push).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalledWith({
                type: "warnning",
                message: "请输入搜索内容"
            })
        })

        it('搜索时应该验证关键词不能为空字符串', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: '' })
            wrapper.vm.search()

            expect(mockRouter.push).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalledWith({
                type: "warnning",
                message: "请输入搜索内容"
            })
        })

        it('搜索后应该关闭搜索框', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: 'Vue' })
            wrapper.vm.search()

            expect(store.state.searchDrawer).toBe(false)
        })
    })

    describe('标签搜索功能', () => {
        it('应该能够点击标签', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const tagItem = mockTagCloud[0]
            wrapper.vm.handleClike(tagItem)

            expect(mockRouter.push).toHaveBeenCalledWith({
                name: "/tags",
                query: { id: tagItem.id, name: tagItem.name }
            })
            expect(store.state.searchDrawer).toBe(false)
        })

        it('点击标签后应该关闭搜索框', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const tagItem = mockTagCloud[0]
            wrapper.vm.handleClike(tagItem)

            expect(store.state.searchDrawer).toBe(false)
        })

        it('应该能够点击不同的标签', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const tagItem1 = mockTagCloud[0]
            wrapper.vm.handleClike(tagItem1)
            expect(mockRouter.push).toHaveBeenCalledWith({
                name: "/tags",
                query: { id: tagItem1.id, name: tagItem1.name }
            })

            mockRouter.push.mockClear()

            const tagItem2 = mockTagCloud[1]
            wrapper.vm.handleClike(tagItem2)
            expect(mockRouter.push).toHaveBeenCalledWith({
                name: "/tags",
                query: { id: tagItem2.id, name: tagItem2.name }
            })
        })
    })

    describe('随机颜色生成', () => {
        it('randomColor 应该生成有效的颜色值', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const color = wrapper.vm.randomColor()
            expect(color).toMatch(/^#[0-9A-F]{6}$/)
        })

        it('randomColor 不应该返回白色', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            // 多次调用确保不会返回白色
            for (let i = 0; i < 10; i++) {
                const color = wrapper.vm.randomColor()
                expect(color).not.toBe('#FFFFFF')
            }
        })

        it('randomColor 不应该返回黑色', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            // 多次调用确保不会返回黑色
            for (let i = 0; i < 10; i++) {
                const color = wrapper.vm.randomColor()
                expect(color).not.toBe('#000000')
            }
        })
    })

    describe('关闭功能', () => {
        it('close 应该关闭搜索框', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.vm.close()

            expect(store.state.searchDrawer).toBe(false)
        })

        it('点击遮罩层应该关闭搜索框', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const mask = wrapper.find('.mask')
            mask.trigger('click')

            expect(store.state.searchDrawer).toBe(false)
        })
    })

    describe('计算属性', () => {
        it('drawer 应该正确绑定store状态', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            expect(wrapper.vm.drawer).toBe(true)

            wrapper.vm.drawer = false
            expect(store.state.searchDrawer).toBe(false)
        })
    })

    describe('生命周期', () => {
        it('created 应该设置 document.scrollTop', () => {
            document.scrollTop = 100
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            // created 钩子会设置 scrollTop 为 0
            expect(document.scrollTop).toBe(0)
        })
    })

    describe('显示/隐藏', () => {
        it('应该根据 drawer 状态显示或隐藏', () => {
            store.state.searchDrawer = true
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            expect(wrapper.find('.search-container').isVisible()).toBe(true)

            store.state.searchDrawer = false
            wrapper.setProps({})
            wrapper.vm.$forceUpdate()

            // v-show 会根据 drawer 状态控制显示
            expect(wrapper.vm.drawer).toBe(false)
        })
    })

    describe('边界情况', () => {
        it('应该处理空标签云', () => {
            const emptyStore = new Vuex.Store({
                state: {
                    searchDrawer: true,
                    tagCloud: []
                }
            })

            wrapper = shallowMount(Search, {
                localVue,
                store: emptyStore,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            const tagItems = wrapper.findAll('.tag .item')
            expect(tagItems.length).toBe(0)
        })

        it('应该处理空搜索关键词', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: '' })
            wrapper.vm.search()

            expect(mockRouter.push).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalled()
        })

        it('应该处理空格搜索关键词', () => {
            wrapper = shallowMount(Search, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter,
                    $router: mockRouter
                },
                stubs: {
                    'i': true
                }
            })

            wrapper.setData({ keywords: '   ' })
            wrapper.vm.search()

            // 空格会被视为空字符串
            expect(mockRouter.push).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalled()
        })
    })
})

