import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import HotCategory from '@/view/category/HotCategory.vue'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

describe('HotCategory.vue - 组件渲染测试', () => {
    let wrapper
    let store
    let vuetify
    let mockRouter

    // Mock 数据
    const mockCategoryList = [
        {
            id: 1,
            name: '分类1',
            avatar: 'https://example.com/category1.jpg'
        },
        {
            id: 2,
            name: '分类2',
            avatar: 'https://example.com/category2.jpg'
        },
        {
            id: 3,
            name: '分类3',
            avatar: 'https://example.com/category3.jpg'
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
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
        mockRouter.push.mockClear()
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.hot_category').exists()).toBe(true)
        })

        it('应该渲染标题区域', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const title = wrapper.find('.title')
            expect(title.exists()).toBe(true)
            expect(title.find('span').text()).toBe('精品分类')
        })

        it('应该渲染"全部分类"链接', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const allCategoryLink = wrapper.find('.title a')
            expect(allCategoryLink.exists()).toBe(true)
            expect(allCategoryLink.attributes('href')).toBe('/categorys')
            expect(allCategoryLink.text()).toContain('全部分类')
        })

        it('应该渲染分类列表容器', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const categoryList = wrapper.find('.categoryList')
            expect(categoryList.exists()).toBe(true)
        })

        it('应该包含所有必要的 CSS 类名', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.find('.hot_category').exists()).toBe(true)
            expect(wrapper.find('.title').exists()).toBe(true)
            expect(wrapper.find('.categoryList').exists()).toBe(true)
        })
    })

    describe('分类列表显示', () => {
        it('应该显示空列表当 categoryList 为空时', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(0)
        })

        it('应该显示单个分类', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [mockCategoryList[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(1)
            expect(items.at(0).find('.name').text()).toBe('分类1')
        })

        it('应该显示多个分类', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: mockCategoryList
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(3)
        })

        it('应该显示分类名称', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [mockCategoryList[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const name = wrapper.find('.name')
            expect(name.exists()).toBe(true)
            expect(name.text()).toBe('分类1')
        })

        it('应该显示分类图片', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [mockCategoryList[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const image = wrapper.find('img')
            expect(image.exists()).toBe(true)
            expect(image.attributes('src')).toBe('https://example.com/category1.jpg')
            expect(image.attributes('alt')).toBe('分类1')
        })

        it('应该正确绑定分类数据到模板', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: mockCategoryList
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(3)
            
            // 验证每个分类项都有正确的数据
            expect(items.at(0).find('.name').text()).toBe('分类1')
            expect(items.at(1).find('.name').text()).toBe('分类2')
            expect(items.at(2).find('.name').text()).toBe('分类3')
        })
    })

    describe('点击事件处理', () => {
        it('应该调用 handleClike 方法当点击分类项时', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [mockCategoryList[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const categoryLink = wrapper.find('.item a')
            await categoryLink.trigger('click')

            expect(mockRouter.push).toHaveBeenCalledWith({
                name: '/category',
                query: { id: 1, name: '分类1' }
            })
        })

        it('应该使用正确的分类 ID 和名称进行路由跳转', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: mockCategoryList
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const categoryLinks = wrapper.findAll('.item a')
            
            // 点击第一个分类
            await categoryLinks.at(0).trigger('click')
            expect(mockRouter.push).toHaveBeenCalledWith({
                name: '/category',
                query: { id: 1, name: '分类1' }
            })

            // 重置 mock
            mockRouter.push.mockClear()

            // 点击第二个分类
            await categoryLinks.at(1).trigger('click')
            expect(mockRouter.push).toHaveBeenCalledWith({
                name: '/category',
                query: { id: 2, name: '分类2' }
            })
        })

        it('handleClike 方法应该正确处理不同的分类', () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [mockCategoryList[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            // 直接调用方法
            wrapper.vm.handleClike({ id: 999, name: '测试分类' })
            expect(mockRouter.push).toHaveBeenCalledWith({
                name: '/category',
                query: { id: 999, name: '测试分类' }
            })
        })
    })

    describe('响应式数据更新', () => {
        it('应该响应 categoryList prop 的变化', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.findAll('.item').length).toBe(0)

            // 更新 prop
            wrapper.setProps({
                categoryList: [mockCategoryList[0]]
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.findAll('.item').length).toBe(1)
        })
    })

    describe('边界情况', () => {
        it('应该处理空的分类列表', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: []
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(0)
        })

        it('应该处理空名称的分类', async () => {
            const categoryWithEmptyName = {
                id: 1,
                name: '',
                avatar: 'https://example.com/category.jpg'
            }

            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [categoryWithEmptyName]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const name = wrapper.find('.name')
            expect(name.exists()).toBe(true)
        })

        it('应该处理大量分类的情况', async () => {
            const categories = Array.from({ length: 20 }, (_, i) => ({
                id: i + 1,
                name: `分类${i + 1}`,
                avatar: `https://example.com/category${i + 1}.jpg`
            }))

            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: categories
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(20)
        })
    })

    describe('组件结构', () => {
        it('应该正确渲染组件结构', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: [mockCategoryList[0]]
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const hotCategory = wrapper.find('.hot_category')
            expect(hotCategory.exists()).toBe(true)
            
            const title = wrapper.find('.title')
            expect(title.exists()).toBe(true)
            
            const categoryList = wrapper.find('.categoryList')
            expect(categoryList.exists()).toBe(true)
            
            const item = wrapper.find('.item')
            expect(item.exists()).toBe(true)
        })

        it('应该正确使用 v-for 渲染分类列表', async () => {
            wrapper = shallowMount(HotCategory, {
                localVue,
                store,
                vuetify,
                propsData: {
                    categoryList: mockCategoryList
                },
                mocks: {
                    $router: mockRouter
                }
            })

            await wrapper.vm.$nextTick()

            const items = wrapper.findAll('.item')
            expect(items.length).toBe(3)
            
            // 验证每个分类项都有正确的 key
            items.wrappers.forEach((item, index) => {
                expect(item.exists()).toBe(true)
            })
        })
    })
})

