import * as api from '@/api'
import request from '@/utils/request'

// Mock request 工具函数
jest.mock('@/utils/request', () => {
    return jest.fn(() => Promise.resolve({ data: {} }))
})

describe('api/index.js - API 函数测试', () => {
    beforeEach(() => {
        // 清除所有 mock 调用记录
        request.mockClear()
        request.mockResolvedValue({ data: {} })
    })

    describe('文章相关 API', () => {
        it('fetchArticleList 应该正确调用 request', async () => {
            const params = { page: 1, size: 10 }
            await api.fetchArticleList(params)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/list',
                method: 'get',
                params: params
            })
        })

        it('getUserArticles 应该正确调用 request', async () => {
            const params = { page: 1, size: 10 }
            await api.getUserArticles(params)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/article/list',
                method: 'get',
                params: params
            })
        })

        it('searchArticle 应该正确调用 request', async () => {
            const params = { keyword: '测试' }
            await api.searchArticle(params)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/search',
                method: 'get',
                params: params
            })
        })

        it('articleInfo 应该正确调用 request', async () => {
            const id = 123
            await api.articleInfo(id)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/info',
                method: 'get',
                params: { id: id }
            })
        })

        it('articleLike 应该正确调用 request', async () => {
            const id = 123
            await api.articleLike(id)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/like',
                method: 'get',
                params: { articleId: id }
            })
        })

        it('archive 应该正确调用 request', async () => {
            await api.archive()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/archive',
                method: 'get'
            })
        })

        it('newArticle 应该正确调用 request', async () => {
            await api.newArticle()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/upToDateArticle',
                method: 'get'
            })
        })

        it('addOrUpdateArticle 应该正确调用 request', async () => {
            const data = { title: '测试文章', content: '内容' }
            await api.addOrUpdateArticle(data)

            expect(request).toHaveBeenCalledWith({
                url: '/system/article/add',
                method: 'post',
                data: data
            })
        })

        it('getArticleDetail 应该正确调用 request', async () => {
            const id = 123
            await api.getArticleDetail(id)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/info',
                method: 'get',
                params: { id: id }
            })
        })

        it('pubOrShelfArticle 应该正确调用 request', async () => {
            const data = { id: 123, isPublish: 1 }
            await api.pubOrShelfArticle(data)

            expect(request).toHaveBeenCalledWith({
                url: '/system/article/pubOrShelf',
                method: 'post',
                data: data
            })
        })

        it('deleteArticle 应该正确调用 request', async () => {
            const id = 123
            await api.deleteArticle(id)

            expect(request).toHaveBeenCalledWith({
                url: '/system/article/delete',
                method: 'delete',
                params: { id: id }
            })
        })
    })

    describe('标签相关 API', () => {
        it('fetchTagList 应该正确调用 request', async () => {
            await api.fetchTagList()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/tag/',
                method: 'get'
            })
        })
    })

    describe('评论相关 API', () => {
        it('featchComments 应该正确调用 request', async () => {
            const params = { articleId: 123 }
            await api.featchComments(params)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/comment/selectCommentByArticleId',
                method: 'get',
                params: params
            })
        })

        it('postComment 应该正确调用 request', async () => {
            const data = { articleId: 123, content: '评论内容' }
            await api.postComment(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/comment/',
                method: 'post',
                data: data
            })
        })
    })

    describe('链接相关 API', () => {
        it('featchLinks 应该正确调用 request', async () => {
            await api.featchLinks()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/link/selectLinkList',
                method: 'get'
            })
        })

        it('addLink 应该正确调用 request', async () => {
            const data = { name: '链接名称', url: 'https://example.com' }
            await api.addLink(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/link/',
                method: 'post',
                data: data
            })
        })
    })

    describe('首页相关 API', () => {
        it('featchHomeData 应该正确调用 request', async () => {
            await api.featchHomeData()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/',
                method: 'get'
            })
        })
    })

    describe('网站信息相关 API', () => {
        it('getWebSiteInfo 应该正确调用 request', async () => {
            await api.getWebSiteInfo()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/webSiteInfo',
                method: 'get'
            })
        })
    })

    describe('留言相关 API', () => {
        it('listMessage 应该正确调用 request', async () => {
            await api.listMessage()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/message/list',
                method: 'get'
            })
        })

        it('addMessage 应该正确调用 request', async () => {
            const data = { content: '留言内容' }
            await api.addMessage(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/message/',
                method: 'post',
                data: data
            })
        })
    })

    describe('用户相关 API', () => {
        it('emailLogin 应该正确调用 request', async () => {
            const data = { email: 'test@example.com', password: 'password' }
            await api.emailLogin(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/emailLogin',
                method: 'post',
                data: data
            })
        })

        it('qqLogin 应该正确调用 request', async () => {
            const data = { code: 'qq_code' }
            await api.qqLogin(data)

            expect(request).toHaveBeenCalledWith({
                url: 'v1/user/login',
                method: 'post',
                data: data
            })
        })

        it('gitEELogin 应该正确调用 request', async () => {
            const code = 'gitee_code'
            await api.gitEELogin(code)

            expect(request).toHaveBeenCalledWith({
                url: 'v1/user/gitEELogin',
                method: 'get',
                params: { code: code }
            })
        })

        it('weiboLogin 应该正确调用 request', async () => {
            const code = 'weibo_code'
            await api.weiboLogin(code)

            expect(request).toHaveBeenCalledWith({
                url: 'v1/user/weiboLogin',
                method: 'get',
                params: { code: code }
            })
        })

        it('logout 应该正确调用 request', async () => {
            await api.logout()

            expect(request).toHaveBeenCalledWith({
                url: '/logout',
                method: 'get'
            })
        })

        it('getWecahtLoginQr 应该正确调用 request', async () => {
            await api.getWecahtLoginQr()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/wxQr',
                method: 'get'
            })
        })

        it('wxIsLogin 应该正确调用 request', async () => {
            const id = 'temp_user_id'
            await api.wxIsLogin(id)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/wx/is_login',
                method: 'get',
                params: { tempUserId: id }
            })
        })

        it('updateUserInfo 应该正确调用 request', async () => {
            const data = { nickname: '新昵称' }
            await api.updateUserInfo(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/',
                method: 'put',
                data: data
            })
        })

        it('getUserInfo 应该正确调用 request', async () => {
            await api.getUserInfo()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/info',
                method: 'get'
            })
        })

        it('sendEmailCode 应该正确调用 request', async () => {
            const email = 'test@example.com'
            await api.sendEmailCode(email)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/sendEmailCode',
                method: 'get',
                params: { email: email }
            })
        })

        it('emailRegister 应该正确调用 request', async () => {
            const data = { email: 'test@example.com', password: 'password', code: '123456' }
            await api.emailRegister(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/user/register',
                method: 'post',
                data: data
            })
        })
    })

    describe('分类相关 API', () => {
        it('featchCategory 应该正确调用 request', async () => {
            await api.featchCategory()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/category/list',
                method: 'get'
            })
        })
    })

    describe('相册相关 API', () => {
        it('featchPhotoAlbum 应该正确调用 request', async () => {
            await api.featchPhotoAlbum()

            expect(request).toHaveBeenCalledWith({
                url: '/v1/photoAlbum/',
                method: 'get'
            })
        })

        it('featchPhoto 应该正确调用 request', async () => {
            const params = { albumId: 123 }
            await api.featchPhoto(params)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/photo/',
                method: 'get',
                params: params
            })
        })
    })

    describe('支付相关 API', () => {
        it('getPayUrl 应该正确调用 request', async () => {
            const params = { amount: 100 }
            await api.getPayUrl(params)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/sponsor/createOrder',
                method: 'get',
                params: params
            })
        })
    })

    describe('AI 相关 API', () => {
        it('aiChat 应该正确调用 request', async () => {
            const data = { prompt: '生成内容', systemPrompt: '系统提示' }
            await api.aiChat(data)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/ai/chat',
                method: 'post',
                data: data
            })
        })
    })

    describe('参数传递测试', () => {
        it('应该正确传递 GET 请求的 params', async () => {
            const params = { page: 1, size: 20, keyword: '测试' }
            await api.fetchArticleList(params)

            expect(request).toHaveBeenCalledWith(
                expect.objectContaining({
                    params: params
                })
            )
        })

        it('应该正确传递 POST 请求的 data', async () => {
            const data = { title: '标题', content: '内容', tags: ['tag1', 'tag2'] }
            await api.addOrUpdateArticle(data)

            expect(request).toHaveBeenCalledWith(
                expect.objectContaining({
                    data: data
                })
            )
        })

        it('应该正确传递 PUT 请求的 data', async () => {
            const data = { nickname: '新昵称', avatar: 'avatar.jpg' }
            await api.updateUserInfo(data)

            expect(request).toHaveBeenCalledWith(
                expect.objectContaining({
                    data: data
                })
            )
        })

        it('应该正确传递 DELETE 请求的 params', async () => {
            const id = 456
            await api.deleteArticle(id)

            expect(request).toHaveBeenCalledWith(
                expect.objectContaining({
                    params: { id: id }
                })
            )
        })
    })

    describe('边界情况', () => {
        it('应该处理空参数', async () => {
            await api.fetchArticleList({})

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/list',
                method: 'get',
                params: {}
            })
        })

        it('应该处理 null 参数', async () => {
            await api.fetchArticleList(null)

            expect(request).toHaveBeenCalledWith({
                url: '/v1/article/list',
                method: 'get',
                params: null
            })
        })

        it('应该处理空对象 data', async () => {
            await api.addOrUpdateArticle({})

            expect(request).toHaveBeenCalledWith({
                url: '/system/article/add',
                method: 'post',
                data: {}
            })
        })

        it('应该处理复杂对象 data', async () => {
            const complexData = {
                title: '标题',
                content: '内容',
                tags: ['tag1', 'tag2'],
                category: { id: 1, name: '分类' },
                metadata: { author: '作者', date: '2024-01-01' }
            }
            await api.addOrUpdateArticle(complexData)

            expect(request).toHaveBeenCalledWith({
                url: '/system/article/add',
                method: 'post',
                data: complexData
            })
        })
    })
})

