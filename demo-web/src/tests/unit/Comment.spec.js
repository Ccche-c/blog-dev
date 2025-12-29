import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import Comment from '@/components/comment/index.vue'
import { postComment, featchComments } from '@/api'
import { getUser } from '@/utils/auth'
import { browserMatch } from '@/utils/index'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

// Mock API
jest.mock('@/api', () => ({
    postComment: jest.fn(),
    featchComments: jest.fn()
}))

// Mock 工具函数
jest.mock('@/utils/auth', () => ({
    getUser: jest.fn()
}))

jest.mock('@/utils/index', () => ({
    browserMatch: jest.fn()
}))

// Mock emoji.json
jest.mock('@/assets/emoji.json', () => [
    { emoji: '😀', name: 'grinning' },
    { emoji: '😃', name: 'smiley' },
    { emoji: '😄', name: 'smile' }
])

describe('Comment.vue - 未登录状态测试', () => {
    let wrapper
    let store
    let vuetify
    let mockToast

    // Mock 评论数据
    const mockCommentList = [
        {
            id: 1,
            userId: 1,
            nickname: '测试用户1',
            avatar: 'https://example.com/avatar1.jpg',
            content: '这是一条测试评论',
            createTime: '2024-01-01T10:00:00',
            browser: 'chrome',
            browserVersion: 'Chrome 120.0',
            system: 'windows',
            systemVersion: 'Windows 10',
            ipAddress: '中国|北京',
            webSite: 'https://example.com',
            children: [
                {
                    id: 2,
                    userId: 2,
                    nickname: '测试用户2',
                    avatar: 'https://example.com/avatar2.jpg',
                    content: '这是一条回复',
                    replyNickname: '测试用户1',
                    createTime: '2024-01-01T11:00:00',
                    browser: 'firefox',
                    browserVersion: 'Firefox 121.0',
                    system: 'macos',
                    systemVersion: 'macOS 14.0',
                    ipAddress: '中国|上海',
                    webSite: 'https://example2.com'
                }
            ]
        }
    ]

    const mockWebSiteInfo = {
        touristAvatar: 'https://example.com/tourist-avatar.jpg',
        author: '网站作者'
    }

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {
                loginFlag: false,
                webSiteInfo: mockWebSiteInfo
            }
        })

        // Mock Toast
        mockToast = jest.fn()
        localVue.prototype.$toast = mockToast

        // Mock window.location
        delete window.location
        window.location = {
            search: '?articleId=123'
        }

        // Mock BroadcastChannel
        global.BroadcastChannel = jest.fn().mockImplementation(() => ({
            onmessage: null,
            postMessage: jest.fn(),
            close: jest.fn()
        }))

        // Mock browserMatch
        browserMatch.mockReturnValue({
            browser: 'Chrome',
            version: '120.0'
        })

        // 重置 mock
        getUser.mockClear()
        postComment.mockClear()
        featchComments.mockClear()
        mockToast.mockClear()
        browserMatch.mockClear()

        // 设置默认返回值：用户未登录
        getUser.mockReturnValue(null)
        postComment.mockResolvedValue({ data: {} })
        featchComments.mockResolvedValue({
            data: {
                records: mockCommentList,
                total: 10
            }
        })
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
    })

    describe('组件渲染 - 未登录状态', () => {
        it('应该正确渲染组件', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.find('.comment-main').exists()).toBe(true)
        })

        it('应该渲染评论输入框', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.find('.comment-item').exists()).toBe(true)
            expect(wrapper.find('.comment-textarea').exists()).toBe(true)
        })

        it('应该显示游客头像当用户未登录时', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const avatar = wrapper.find('.avatar img')
            expect(avatar.exists()).toBe(true)
            expect(avatar.attributes('src')).toBe(mockWebSiteInfo.touristAvatar)
        })

        it('应该渲染评论列表', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.find('.commentwrap').exists()).toBe(true)
            expect(wrapper.findAll('.ul-item').length).toBeGreaterThan(0)
        })

        it('应该渲染提交按钮', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const submitButton = wrapper.find('.upload-btn')
            expect(submitButton.exists()).toBe(true)
            expect(submitButton.text()).toBe('提交')
        })
    })

    describe('初始数据 - 未登录状态', () => {
        it('应该初始化正确的默认数据', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.vm.user).toBe(null)
            expect(wrapper.vm.commentContent).toBe('')
            expect(wrapper.vm.chooseEmoji).toBe(false)
            expect(wrapper.vm.pageNo).toBe(1)
            expect(wrapper.vm.emojiList).toBeDefined()
        })

        it('应该从URL获取articleId', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.vm.articleId).toBe('123')
        })
    })

    describe('添加评论 - 未登录状态', () => {
        it('未登录时点击提交应该打开登录对话框', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ commentContent: '测试评论内容' })
            wrapper.vm.addComment()

            expect(store.state.loginFlag).toBe(true)
            expect(postComment).not.toHaveBeenCalled()
        })

        it('未登录时即使有评论内容也不应该提交', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ commentContent: '测试评论内容' })
            wrapper.vm.addComment()

            expect(postComment).not.toHaveBeenCalled()
            expect(mockToast).not.toHaveBeenCalled()
        })
    })

    describe('回复评论 - 未登录状态', () => {
        it('未登录时点击回复应该打开登录对话框', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const commentItem = mockCommentList[0]
            wrapper.vm.replyComment(commentItem, commentItem.id, false)

            expect(store.state.loginFlag).toBe(true)
        })

        it('未登录时点击子评论回复应该打开登录对话框', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const childComment = mockCommentList[0].children[0]
            wrapper.vm.replyComment(childComment, mockCommentList[0].id, true)

            expect(store.state.loginFlag).toBe(true)
        })
    })

    describe('评论列表渲染 - 未登录状态', () => {
        it('应该正确渲染评论列表', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const commentItems = wrapper.findAll('.ul-item')
            expect(commentItems.length).toBeGreaterThan(0)
        })

        it('应该显示评论内容', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const commentContent = wrapper.find('.markdown-content p')
            expect(commentContent.exists()).toBe(true)
        })

        it('应该显示评论者昵称', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const author = wrapper.find('.author')
            expect(author.exists()).toBe(true)
        })

        it('应该显示评论时间', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const time = wrapper.find('.comment-time')
            expect(time.exists()).toBe(true)
        })

        it('应该显示"加载更多"按钮当有更多评论时', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ pageNo: 1 })
            const moreBtn = wrapper.find('.more-btn')
            expect(moreBtn.exists()).toBe(true)
            expect(moreBtn.text()).toBe('加载更多...')
        })

        it('不应该显示"加载更多"按钮当没有更多评论时', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 1,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ pageNo: 1 })
            const moreBtn = wrapper.find('.more-btn')
            expect(moreBtn.exists()).toBe(false)
        })
    })

    describe('工具方法测试', () => {
        it('formatDate 应该正确格式化日期', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const date = '2024-01-15T10:30:00'
            const formatted = wrapper.vm.formatDate(date)
            expect(formatted).toBe('2024-01-15')
        })

        it('formatDate 应该支持MM/dd格式', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const date = '2024-01-15T10:30:00'
            const formatted = wrapper.vm.formatDate(date, 'MM/dd')
            expect(formatted).toBe('01/15')
        })

        it('splitIpAddress 应该正确分割IP地址', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            const ipAddress = '中国|北京'
            const result = wrapper.vm.splitIpAddress(ipAddress)
            expect(result).toBe('北京')
        })
    })

    describe('表情功能 - 未登录状态', () => {
        it('应该能够切换表情选择器显示状态', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.vm.chooseEmoji).toBe(false)

            wrapper.vm.chooseEmoji = true
            expect(wrapper.vm.chooseEmoji).toBe(true)
        })

        it('应该能够添加表情到评论内容', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ commentContent: '测试' })
            wrapper.vm.addEmoji({ emoji: '😀' })

            expect(wrapper.vm.commentContent).toBe('测试😀')
        })
    })

    describe('加载更多评论 - 未登录状态', () => {
        it('应该能够加载更多评论', async () => {
            const moreComments = [
                {
                    id: 3,
                    userId: 3,
                    nickname: '测试用户3',
                    content: '更多评论',
                    createTime: '2024-01-01T12:00:00',
                    browser: 'safari',
                    browserVersion: 'Safari 17.0',
                    system: 'ios',
                    systemVersion: 'iOS 17.0',
                    ipAddress: '中国|广东',
                    webSite: 'https://example3.com',
                    children: []
                }
            ]

            featchComments.mockResolvedValue({
                data: {
                    records: moreComments,
                    total: 10
                }
            })

            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: mockCommentList,
                    total: 10,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ pageNo: 1 })
            await wrapper.vm.moreComment()

            expect(featchComments).toHaveBeenCalledWith({
                pageNo: 2,
                pageSize: 5,
                articleId: '123'
            })
            expect(wrapper.vm.pageNo).toBe(2)
        })
    })

    describe('BroadcastChannel - 未登录状态', () => {
        it('应该监听用户登录状态变化', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(global.BroadcastChannel).toHaveBeenCalledWith('my-channel')
        })
    })

    describe('边界情况 - 未登录状态', () => {
        it('应该处理空评论列表', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            expect(wrapper.exists()).toBe(true)
            expect(wrapper.findAll('.ul-item').length).toBe(0)
        })

        it('应该处理评论内容为空的情况', () => {
            wrapper = shallowMount(Comment, {
                localVue,
                store,
                vuetify,
                propsData: {
                    commentList: [],
                    total: 0,
                    articleUserId: 1
                },
                stubs: {
                    'svg-icon': true,
                    'Reply': true
                }
            })

            wrapper.setData({ commentContent: '' })
            wrapper.vm.addComment()

            // 未登录时应该打开登录对话框，而不是显示错误
            expect(store.state.loginFlag).toBe(true)
        })
    })
})

