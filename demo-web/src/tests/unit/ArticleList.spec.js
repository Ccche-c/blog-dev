import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import ArticleList from '@/view/article/ArticleList.vue'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)
// 注意：不要在这里使用 Vuetify，而是在每个测试中传入 Vuetify 实例

describe('ArticleList.vue - 组件渲染测试', () => {
    let wrapper
    let store
    let vuetify
    let mockRouter
    let mockWOW

    // 创建 mock router
    mockRouter = {
        push: jest.fn()
    }

    // Mock WOW.js
    mockWOW = {
        WOW: jest.fn().mockImplementation(() => ({
            init: jest.fn()
        }))
    }

    // Mock 文章数据
    const mockArticleList = [
        {
            id: 1,
            title: '测试文章1',
            summary: '这是文章摘要1',
            username: '作者1',
            createTime: '2024-01-01',
            quantity: 100,
            commentCount: 10,
            likeCount: 20,
            avatar: 'https://example.com/image1.jpg',
            isStick: 0,
            isOriginal: 0,
            tagList: [
                { id: 1, name: '标签1' },
                { id: 2, name: '标签2' }
            ]
        },
        {
            id: 2,
            title: '测试文章2',
            summary: '这是文章摘要2',
            username: '作者2',
            createTime: '2024-01-02',
            quantity: 200,
            commentCount: 20,
            likeCount: 30,
            avatar: 'https://example.com/image2.jpg',
            isStick: 1,
            isOriginal: 1,
            tagList: [
                { id: 3, name: '标签3' }
            ]
        }
    ]

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {}
        })

        // 重置 mock
        mockRouter.push.mockClear()
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.artcile_main').exists()).toBe(true)
            expect(wrapper.find('.title').exists()).toBe(true)
            expect(wrapper.find('.mainBox').exists()).toBe(true)
        })

        it('应该显示标题', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const title = wrapper.find('.title span')
            expect(title.exists()).toBe(true)
            expect(title.text()).toBe('最新文章')
        })
    })

    describe('文章列表显示', () => {
        it('应该显示空列表当 articleList 为空时', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItems = wrapper.findAll('.ul_item')
            expect(articleItems.length).toBe(0)
        })

        it('应该显示单个文章', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItems = wrapper.findAll('.ul_item')
            expect(articleItems.length).toBe(1)
            expect(articleItems.at(0).find('.titleName').text()).toContain('测试文章1')
        })

        it('应该显示多个文章', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: mockArticleList
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItems = wrapper.findAll('.ul_item')
            expect(articleItems.length).toBe(2)
        })

        it('应该显示文章标题', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const titleName = wrapper.find('.titleName')
            expect(titleName.exists()).toBe(true)
            expect(titleName.text()).toContain('测试文章1')
        })

        it('应该显示文章摘要', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const info = wrapper.find('.info')
            expect(info.exists()).toBe(true)
            expect(info.text()).toBe('这是文章摘要1')
        })

        it('应该显示作者信息', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const meta = wrapper.find('.meta')
            expect(meta.exists()).toBe(true)
            expect(meta.text()).toContain('作者1')
        })

        it('应该显示阅读数、评论数、点赞数', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const meta = wrapper.find('.meta')
            expect(meta.text()).toContain('100')
            expect(meta.text()).toContain('10')
            expect(meta.text()).toContain('20')
        })

        it('应该显示文章封面图片', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const image = wrapper.find('.article_cover')
            expect(image.exists()).toBe(true)
            expect(image.attributes('src')).toBe('https://example.com/image1.jpg')
            expect(image.attributes('alt')).toBe('测试文章1')
        })

        it('应该显示标签列表', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const tags = wrapper.findAll('.tag_name')
            expect(tags.length).toBe(2)
            expect(tags.at(0).text()).toBe('标签1')
            expect(tags.at(1).text()).toBe('标签2')
        })
    })

    describe('置顶和原创标签', () => {
        it('应该显示置顶标签当 isStick 为 1 时', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[1]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const topTag = wrapper.find('.top')
            expect(topTag.exists()).toBe(true)
            expect(topTag.text()).toBe('置顶')
        })

        it('应该显示原创标签当 isOriginal 为 1 时', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[1]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const originalTag = wrapper.find('.original')
            expect(originalTag.exists()).toBe(true)
            expect(originalTag.text()).toBe('原创')
        })

        it('应该同时显示置顶和原创标签', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[1]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            expect(wrapper.find('.top').exists()).toBe(true)
            expect(wrapper.find('.original').exists()).toBe(true)
        })

        it('不应该显示置顶标签当 isStick 为 0 时', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const topTag = wrapper.find('.top')
            expect(topTag.exists()).toBe(false)
        })

        it('不应该显示原创标签当 isOriginal 为 0 时', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const originalTag = wrapper.find('.original')
            expect(originalTag.exists()).toBe(false)
        })
    })

    describe('点击事件处理', () => {
        it('应该调用 handleClick 方法当点击文章项时', async () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItem = wrapper.find('.main_li')
            await articleItem.trigger('click')

            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 1 }
            })
        })

        it('应该使用正确的文章 ID 进行路由跳转', async () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: mockArticleList
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItems = wrapper.findAll('.main_li')
            
            // 点击第一篇文章
            await articleItems.at(0).trigger('click')
            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 1 }
            })

            // 重置 mock
            mockRouter.push.mockClear()

            // 点击第二篇文章
            await articleItems.at(1).trigger('click')
            expect(mockRouter.push).toHaveBeenCalledWith({
                path: '/articleInfo',
                query: { articleId: 2 }
            })
        })

        it('handleClick 方法应该正确处理不同的 ID', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
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

    describe('图片错误处理', () => {
        it('应该在图片加载错误时替换为默认图片', async () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const image = wrapper.find('.article_cover')
            expect(image.exists()).toBe(true)

            // 触发错误事件
            await image.trigger('error')

            // 等待 Vue 更新
            await wrapper.vm.$nextTick()

            // 检查图片 src 是否被替换为默认图片
            const updatedImage = wrapper.find('.article_cover')
            expect(updatedImage.attributes('src')).toBe(wrapper.vm.img)
        })
    })

    describe('WOW.js 初始化', () => {
        it('应该在 mounted 时初始化 WOW.js', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            // 验证 WOW 被调用
            expect(mockWOW.WOW).toHaveBeenCalled()
            
            // 验证 init 被调用
            const wowInstance = mockWOW.WOW.mock.results[0].value
            expect(wowInstance.init).toHaveBeenCalled()
        })
    })

    describe('日期格式化', () => {
        it('formatDate 应该正确格式化日期', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const date = '2024-01-15T00:00:00'
            const formatted = wrapper.vm.formatDate(date)
            expect(formatted).toMatch(/2024-01-15/)
        })

        it('formatDate 应该正确处理月份和日期补零', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const date = '2024-01-05T00:00:00'
            const formatted = wrapper.vm.formatDate(date)
            expect(formatted).toBe('2024-01-05')
        })
    })

    describe('响应式数据更新', () => {
        it('应该响应 articleList prop 的变化', async () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            expect(wrapper.findAll('.ul_item').length).toBe(0)

            // 更新 prop
            wrapper.setProps({
                articleList: [mockArticleList[0]]
            })

            await wrapper.vm.$nextTick()

            expect(wrapper.findAll('.ul_item').length).toBe(1)
        })
    })

    describe('边界情况', () => {
        it('应该处理空的标签列表', () => {
            const articleWithNoTags = {
                ...mockArticleList[0],
                tagList: []
            }

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [articleWithNoTags]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const tags = wrapper.findAll('.tag_name')
            expect(tags.length).toBe(0)
        })

        it('应该处理 null 或 undefined 的数值', () => {
            const articleWithNullValues = {
                ...mockArticleList[0],
                quantity: null,
                commentCount: null,
                likeCount: null
            }

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [articleWithNullValues]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            // 组件应该能够正常渲染，即使值为 null
            expect(wrapper.exists()).toBe(true)
        })

        it('应该处理大量文章的情况', () => {
            const articles = Array.from({ length: 50 }, (_, i) => ({
                id: i + 1,
                title: `文章${i + 1}`,
                summary: `摘要${i + 1}`,
                username: '作者',
                createTime: '2024-01-01',
                quantity: 100,
                commentCount: 10,
                likeCount: 20,
                avatar: 'https://example.com/image.jpg',
                isStick: 0,
                isOriginal: 0,
                tagList: []
            }))

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: articles
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItems = wrapper.findAll('.ul_item')
            expect(articleItems.length).toBe(50)
        })

        it('应该处理空标题的情况', () => {
            const articleWithEmptyTitle = {
                ...mockArticleList[0],
                title: ''
            }

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [articleWithEmptyTitle]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const titleName = wrapper.find('.titleName')
            expect(titleName.exists()).toBe(true)
        })

        it('应该处理空摘要的情况', () => {
            const articleWithEmptySummary = {
                ...mockArticleList[0],
                summary: ''
            }

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [articleWithEmptySummary]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const info = wrapper.find('.info')
            expect(info.exists()).toBe(true)
        })
    })

    describe('组件结构', () => {
        it('应该包含正确的 CSS 类名', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: []
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            expect(wrapper.find('.artcile_main').exists()).toBe(true)
            expect(wrapper.find('.title').exists()).toBe(true)
            expect(wrapper.find('.mainBox').exists()).toBe(true)
        })

        it('应该正确渲染文章项结构', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItem = wrapper.find('.main_li')
            expect(articleItem.exists()).toBe(true)
            expect(articleItem.find('.thumbnail').exists()).toBe(true)
            expect(articleItem.find('.information').exists()).toBe(true)
        })
    })

    describe('数据绑定', () => {
        it('应该正确绑定文章数据到模板', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const titleName = wrapper.find('.titleName')
            expect(titleName.text()).toContain('测试文章1')

            const info = wrapper.find('.info')
            expect(info.text()).toBe('这是文章摘要1')
        })

        it('应该正确使用 v-for 渲染文章列表', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: mockArticleList
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const articleItems = wrapper.findAll('.ul_item')
            expect(articleItems.length).toBe(2)
            
            // 验证每个文章项都有正确的数据
            expect(articleItems.at(0).find('.titleName').text()).toContain('测试文章1')
            expect(articleItems.at(1).find('.titleName').text()).toContain('测试文章2')
        })

        it('应该正确使用 v-for 渲染标签列表', () => {
            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [mockArticleList[0]]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            const tags = wrapper.findAll('.tag_name')
            expect(tags.length).toBe(2)
            expect(tags.at(0).text()).toBe('标签1')
            expect(tags.at(1).text()).toBe('标签2')
        })
    })

    describe('条件渲染', () => {
        it('应该根据 isStick 条件显示置顶标签', () => {
            const stickArticle = {
                ...mockArticleList[0],
                isStick: 1
            }

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [stickArticle]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            expect(wrapper.find('.top').exists()).toBe(true)
        })

        it('应该根据 isOriginal 条件显示原创标签', () => {
            const originalArticle = {
                ...mockArticleList[0],
                isOriginal: 1
            }

            wrapper = shallowMount(ArticleList, {
                localVue,
                store,
                vuetify,
                propsData: {
                    articleList: [originalArticle]
                },
                mocks: {
                    $router: mockRouter,
                    $wow: mockWOW
                }
            })

            expect(wrapper.find('.original').exists()).toBe(true)
        })
    })
})

