import request from '@/utils/request'

export function fetchArticleList(params) {
    return request({
        url: '/v1/article/list',
        method: 'get',
        params: params
    })
}
// 获取当前用户的文章列表
export function getUserArticles(params) {
    return request({
        url: '/v1/user/article/list',
        method: 'get',
        params: params
    })
}
export function searchArticle(params) {
    return request({
        url: '/v1/article/search',
        method: 'get',
        params: params
    })
}

export function articleInfo(id) {
    return request({
        url: '/v1/article/info',
        method: 'get',
        params: {
            id: id
        }
    })
}
export function articleLike(id) {
    return request({
        url: '/v1/article/like',
        method: 'get',
        params: {
            articleId: id
        }
    })
}
export function archive() {
    return request({
        url: '/v1/article/archive',
        method: 'get',
    })
}

export function fetchTagList() {
    return request({
        url: '/v1/tag/',
        method: 'get',

    })
}

export function featchComments(params) {
    return request({
        url: '/v1/comment/selectCommentByArticleId',
        method: 'get',
        params: params
    })
}
export function newArticle() {
    return request({
        url: '/v1/upToDateArticle',
        method: 'get',
    })
}
export function featchLinks() {
    return request({
        url: '/v1/link/selectLinkList',
        method: 'get',
    })
}
export function addLink(data) {
    return request({
        url: '/v1/link/',
        method: 'post',
        data
    })
}
export function postComment(data) {
    return request({
        url: '/v1/comment/',
        method: 'post',
        data
    })
}

export function featchHomeData() {
    return request({
        url: '/v1/',
        method: 'get',
    })
}
export function getWebSiteInfo() {
    return request({
        url: '/v1/webSiteInfo',
        method: 'get',
    })
}
export function listMessage() {
    return request({
        url: '/v1/message/list',
        method: 'get',
    })
}
export function addMessage(data) {
    return request({
        url: '/v1/message/',
        method: 'post',
        data
    })
}

export function emailLogin(data) {
    return request({
        url: '/v1/user/emailLogin',
        method: 'post',
        data
    })
}
//qq登录
export function qqLogin(data) {
    return request({
        url: 'v1/user/login',
        method: 'post',
        data
    })
}
//gitee登录
export function gitEELogin(code) {
    return request({
        url: 'v1/user/gitEELogin',
        method: 'get',
        params: {
            code: code
        }
    })
}
//微博登录
export function weiboLogin(code) {
    return request({
        url: 'v1/user/weiboLogin',
        method: 'get',
        params: {
            code: code
        }
    })
}
export function logout() {
    return request({
        url: '/logout',
        method: 'get'
    })
}
export function getWecahtLoginQr() {
    return request({
        url: '/v1/user/wxQr',
        method: 'get'
    })
}
export function wxIsLogin(id) {
    return request({
        url: '/v1/user/wx/is_login',
        method: 'get',
        params: {
            tempUserId: id
        }
    })
}
export function updateUserInfo(data) {
    return request({
        url: '/v1/user/',
        method: 'put',
        data
    })
}
export function getUserInfo() {
    return request({
        url: '/v1/user/info',
        method: 'get'
    })
}
export function sendEmailCode(email) {
    return request({
        url: '/v1/user/sendEmailCode',
        method: 'get',
        params: {
            email: email
        }
    })
}
// 邮箱注册
export function emailRegister(data) {
    return request({
        url: '/v1/user/register',
        method: 'post',
        data
    })
}
export function featchCategory() {
    return request({
        url: '/v1/category/list',
        method: 'get'
    })
}

export function featchPhotoAlbum() {
    return request({
        url: '/v1/photoAlbum/',
        method: 'get'
    })
}

export function featchPhoto(params) {
    return request({
        url: '/v1/photo/',
        method: 'get',
        params: params
    })
}
export function getPayUrl(params) {
    return request({
        url: '/v1/sponsor/createOrder',
        method: 'get',
        params: params
    })
}
// 添加/编辑文章
export function addOrUpdateArticle(data) {
    return request({
        url: '/system/article/add',
        method: 'post',
        data
    })
}
// 获取文章详情（用于编辑）
export function getArticleDetail(id) {
    return request({
        url: '/v1/article/info',
        method: 'get',
        params: {
            id: id
        }
    })
}
// 发布/下架文章（共用接口）
// 参数：{ id: 文章ID, isPublish: 0=下架, 1=发布 }
export function pubOrShelfArticle(data) {
    return request({
        url: '/system/article/pubOrShelf',
        method: 'post',
        data: data
    })
}
// 删除文章
export function deleteArticle(id) {
    return request({
        url: '/system/article/delete',
        method: 'delete',
        params: {
            id: id
        }
    })
}
// AI推送精品内容
export function aiChat(data) {
    return request({
        url: '/v1/ai/chat',
        method: 'post',
        data: data
    })
}