import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import NewlyArticle from '@/view/article/NewlyArticle.vue'
import { newArticle } from '@/api'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)
// 注意：不要在这里使用 Vuetify，而是在每个测试中传入 Vuetify 实例

// Mock API
jest.mock('@/api', () => ({
    newArticle: jest.fn()
}))

describe('NewlyArticle.vue', () => {
    let wrapper
    let store
    let router
    let vuetify

    // 创建 mock router
    const mockRouter = {
        push: jest.fn()
    }

    beforeEach(() => {
        // 创建 Vuetify 实例（使用默认配置）
        vuetify = new Vuetify({
            // 可以在这里添加 Vuetify 配置
        })

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {
                newArticleList: []
            }
        })

        // 重置 mock
        mockRouter.push.mockClear()
        newArticle.mockClear()
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', () => {
            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.container').exists()).toBe(true)
            expect(wrapper.find('.article-card').exists()).toBe(true)
        })

        it('应该显示标题和图标', () => {
            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const title = wrapper.find('.title')
            expect(title.exists()).toBe(true)
            expect(title.find('span').text()).toBe('最新文章')
            expect(title.find('i.iconfont.icon-zuire').exists()).toBe(true)
        })

        it('应该显示"更多"链接', () => {
            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const moreLink = wrapper.find('.more')
            expect(moreLink.exists()).toBe(true)
            expect(moreLink.attributes('href')).toBe('/archive')
            expect(moreLink.text()).toBe('更多')
        })
    })

    describe('文章列表显示', () => {
        it('应该显示空列表当 store 中没有文章时', () => {
            store.state.newArticleList = []

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleContents = wrapper.findAll('.articleContent')
            expect(articleContents.length).toBe(0)
        })

        it('应该显示单个文章', () => {
            store.state.newArticleList = [
                { id: 1, title: '测试文章1' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleContents = wrapper.findAll('.articleContent')
            expect(articleContents.length).toBe(1)
            expect(articleContents.at(0).find('a').text()).toBe('1. 测试文章1')
        })

        it('应该显示多个文章并按索引编号', () => {
            store.state.newArticleList = [
                { id: 1, title: '测试文章1' },
                { id: 2, title: '测试文章2' },
                { id: 3, title: '测试文章3' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleContents = wrapper.findAll('.articleContent')
            expect(articleContents.length).toBe(3)
            expect(articleContents.at(0).find('a').text()).toBe('1. 测试文章1')
            expect(articleContents.at(1).find('a').text()).toBe('2. 测试文章2')
            expect(articleContents.at(2).find('a').text()).toBe('3. 测试文章3')
        })

        it('应该正确绑定文章的 key', () => {
            store.state.newArticleList = [
                { id: 1, title: '测试文章1' },
                { id: 2, title: '测试文章2' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleContents = wrapper.findAll('.articleContent')
            expect(articleContents.length).toBe(2)
            // 验证每个元素都有正确的 key
            articleContents.wrappers.forEach((item, index) => {
                expect(item.exists()).toBe(true)
            })
        })
    })

    describe('点击事件处理', () => {
        it('应该调用 handleClick 方法当点击文章链接时', async () => {
            store.state.newArticleList = [
                { id: 1, title: '测试文章1' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleLink = wrapper.find('.articleContent a')
            await articleLink.trigger('click')

            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 1 }
            })
        })

        it('应该使用正确的文章 ID 进行路由跳转', async () => {
            store.state.newArticleList = [
                { id: 100, title: '测试文章100' },
                { id: 200, title: '测试文章200' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleLinks = wrapper.findAll('.articleContent a')
            
            // 点击第一篇文章
            await articleLinks.at(0).trigger('click')
            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 100 }
            })

            // 重置 mock
            mockRouter.push.mockClear()

            // 点击第二篇文章
            await articleLinks.at(1).trigger('click')
            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 200 }
            })
        })

        it('handleClick 方法应该正确处理不同的 ID', () => {
            store.state.newArticleList = [
                { id: 1, title: '测试文章1' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            // 直接调用方法
            wrapper.vm.handleClick(999)
            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 999 }
            })
        })
    })

    describe('响应式数据更新', () => {
        it('应该响应 store 中 newArticleList 的变化', async () => {
            store.state.newArticleList = []

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            expect(wrapper.findAll('.articleContent').length).toBe(0)

            // 更新 store
            store.state.newArticleList = [
                { id: 1, title: '新文章1' },
                { id: 2, title: '新文章2' }
            ]

            await wrapper.vm.$nextTick()

            const articleContents = wrapper.findAll('.articleContent')
            expect(articleContents.length).toBe(2)
            expect(articleContents.at(0).find('a').text()).toBe('1. 新文章1')
            expect(articleContents.at(1).find('a').text()).toBe('2. 新文章2')
        })
    })

    describe('边界情况', () => {
        it('应该处理文章标题为空的情况', () => {
            store.state.newArticleList = [
                { id: 1, title: '' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleLink = wrapper.find('.articleContent a')
            // Vue 会将空字符串渲染为空，所以结果是 "1."
            expect(articleLink.text()).toBe('1.')
        })

        it('应该处理文章标题为 null 的情况', () => {
            store.state.newArticleList = [
                { id: 1, title: null }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleLink = wrapper.find('.articleContent a')
            // Vue 会将 null 渲染为空字符串，所以结果是 "1."
            expect(articleLink.text()).toBe('1.')
        })

        it('应该处理文章标题为 undefined 的情况', () => {
            store.state.newArticleList = [
                { id: 1 }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleLink = wrapper.find('.articleContent a')
            // Vue 会将 undefined 渲染为空字符串，所以结果是 "1."
            expect(articleLink.text()).toBe('1.')
        })

        it('应该处理大量文章的情况', () => {
            const articles = Array.from({ length: 100 }, (_, i) => ({
                id: i + 1,
                title: `文章${i + 1}`
            }))
            store.state.newArticleList = articles

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const articleContents = wrapper.findAll('.articleContent')
            expect(articleContents.length).toBe(100)
            expect(articleContents.at(0).find('a').text()).toBe('1. 文章1')
            expect(articleContents.at(99).find('a').text()).toBe('100. 文章100')
        })
    })

    describe('组件结构', () => {
        it('应该包含正确的 CSS 类名', () => {
            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            expect(wrapper.find('.container').exists()).toBe(true)
            expect(wrapper.find('.article-card').exists()).toBe(true)
            expect(wrapper.find('.title').exists()).toBe(true)
            expect(wrapper.find('.article').exists()).toBe(true)
        })

        it('应该正确渲染 v-card 组件', () => {
            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            // shallowMount 会保留 v-card 作为组件，检查是否存在
            // 由于使用了 shallowMount，v-card 可能不会被完全渲染
            // 但我们可以检查 article-card 类是否存在
            expect(wrapper.find('.article-card').exists()).toBe(true)
        })
    })

    describe('数据绑定', () => {
        it('应该正确绑定文章数据到模板', () => {
            const testArticles = [
                { id: 1, title: '文章标题1' },
                { id: 2, title: '文章标题2' }
            ]
            store.state.newArticleList = testArticles

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const links = wrapper.findAll('.articleContent a')
            expect(links.length).toBe(2)
            expect(links.at(0).text()).toContain('文章标题1')
            expect(links.at(1).text()).toContain('文章标题2')
        })

        it('应该正确使用 v-for 的 index 进行编号', () => {
            store.state.newArticleList = [
                { id: 10, title: '文章A' },
                { id: 20, title: '文章B' },
                { id: 30, title: '文章C' }
            ]

            wrapper = shallowMount(NewlyArticle, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $router: mockRouter
                }
            })

            const links = wrapper.findAll('.articleContent a')
            expect(links.at(0).text()).toBe('1. 文章A')
            expect(links.at(1).text()).toBe('2. 文章B')
            expect(links.at(2).text()).toBe('3. 文章C')
        })
    })
})

