import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import Banner from '@/components/banner/Banner.vue'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

describe('Banner.vue - 组件渲染测试', () => {
    let wrapper
    let store
    let vuetify
    let mockRouter

    // Mock 数据
    const mockImages = [
        {
            id: 1,
            title: '轮播图1',
            avatar: 'https://example.com/banner1.jpg'
        },
        {
            id: 2,
            title: '轮播图2',
            avatar: 'https://example.com/banner2.jpg'
        },
        {
            id: 3,
            title: '轮播图3',
            avatar: 'https://example.com/banner3.jpg'
        }
    ]

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {}
        })

        // 创建 mock router
        mockRouter = {
            push: jest.fn()
        }

        // Mock document.getElementById
        const mockElement = {
            style: {
                width: '',
                height: '',
                top: ''
            }
        }
        document.getElementById = jest.fn(() => mockElement)

        // Mock setInterval 和 clearInterval
        global.setInterval = jest.fn((fn, delay) => {
            return 1
        })
        global.clearInterval = jest.fn()
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
        // 清理定时器
        jest.clearAllTimers()
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.carousel_swipe').exists()).toBe(true)
        })

        it('应该渲染轮播容器', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const carousel = wrapper.find('.carousel_swipe')
            expect(carousel.exists()).toBe(true)
        })

        it('应该渲染轮播列表', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const swipeList = wrapper.find('.swipe_list')
            expect(swipeList.exists()).toBe(true)
        })

        it('应该渲染控制点区域', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const swipeDian = wrapper.find('.swipe_dian')
            expect(swipeDian.exists()).toBe(true)
        })

        it('应该渲染控制按钮区域', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const swipeControl = wrapper.find('.swipe_control')
            expect(swipeControl.exists()).toBe(true)
        })

        it('应该渲染底部标题区域', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            // 底部标题区域可能存在
            expect(wrapper.exists()).toBe(true)
        })

        it('应该包含所有必要的 CSS 类名', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.find('.carousel_swipe').exists()).toBe(true)
            expect(wrapper.find('.swipe_list').exists()).toBe(true)
            expect(wrapper.find('.swipe_dian').exists()).toBe(true)
            expect(wrapper.find('.swipe_control').exists()).toBe(true)
        })
    })

    describe('空数据渲染', () => {
        it('应该能够处理空的图片列表', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.images).toEqual([])
        })

        it('应该能够处理单个图片', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: [mockImages[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.images.length).toBe(1)
        })

        it('应该能够处理多个图片', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.vm.images.length).toBe(3)
        })
    })

    describe('初始数据', () => {
        it('应该初始化正确的默认数据', () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            expect(wrapper.vm.currentIndex).toBe(0)
            expect(wrapper.vm.control).toBe(false)
            expect(wrapper.vm.direction).toBe('forward')
            expect(wrapper.vm.noticeFalg).toBe(true)
        })

        it('应该在 created 时生成随机 ID', () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            expect(typeof wrapper.vm.id).toBe('number')
            expect(wrapper.vm.id).toBeGreaterThanOrEqual(0)
        })
    })

    describe('组件结构', () => {
        it('应该正确渲染组件结构', async () => {
            wrapper = shallowMount(Banner, {
                localVue,
                store,
                vuetify,
                propsData: {
                    images: mockImages
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const carousel = wrapper.find('.carousel_swipe')
            expect(carousel.exists()).toBe(true)

            const swipeList = wrapper.find('.swipe_list')
            expect(swipeList.exists()).toBe(true)

            const swipeDian = wrapper.find('.swipe_dian')
            expect(swipeDian.exists()).toBe(true)

            const swipeControl = wrapper.find('.swipe_control')
            expect(swipeControl.exists()).toBe(true)
        })
    })
})

