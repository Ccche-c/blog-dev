import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import Login from '@/components/model/Login.vue'
import { emailLogin } from '@/api'
import { setUrl } from '@/utils/auth'

// 创建本地 Vue 实例
const localVue = createLocalVue()
localVue.use(Vuex)

// Mock API
jest.mock('@/api', () => ({
    emailLogin: jest.fn()
}))

// Mock 工具函数
jest.mock('@/utils/auth', () => ({
    setUrl: jest.fn()
}))

describe('Login.vue - 登录组件测试', () => {
    let wrapper
    let store
    let vuetify
    let mockToast
    let mockRouter

    // 测试账号
    const TEST_EMAIL = '3085755420@qq.com'
    const TEST_PASSWORD = '123456'

    // Mock 用户数据
    const mockUserData = {
        id: 1,
        nickname: '测试用户',
        email: TEST_EMAIL,
        token: 'mock-token-123'
    }

    // Mock 配置
    const mockConfig = {
        QQ_CLIENT_ID: 'test-qq-client-id',
        QQ_REDIRECT_URL: 'http://localhost:8080/qq-callback',
        GITEE_CLIENT_ID: 'test-gitee-client-id',
        GITEE_REDIRECT_URL: 'http://localhost:8080/gitee-callback',
        WEIBO_CLIENT_ID: 'test-weibo-client-id',
        WEIBO_REDIRECT_URL: 'http://localhost:8080/weibo-callback'
    }

    beforeEach(() => {
        // 创建 Vuetify 实例
        vuetify = new Vuetify()

        // 创建 Vuex store
        store = new Vuex.Store({
            state: {
                loginFlag: false,
                registerFlag: false,
                forgetFlag: false,
                wechatFlag: false
            },
            mutations: {
                login(state, userData) {
                    state.user = userData
                },
                closeModel(state) {
                    state.loginFlag = false
                    state.registerFlag = false
                    state.forgetFlag = false
                    state.wechatFlag = false
                }
            }
        })

        // Mock Router
        mockRouter = {
            path: '/',
            query: {},
            push: jest.fn()
        }

        // Mock Toast
        mockToast = jest.fn()
        localVue.prototype.$toast = mockToast

        // Mock config (通过 Vue.prototype.config 设置)
        localVue.prototype.config = mockConfig

        // Mock window.open
        global.window.open = jest.fn()

        // Mock navigator.userAgent
        Object.defineProperty(global.navigator, 'userAgent', {
            writable: true,
            value: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        })

        // Mock QC.Login (QQ登录)
        global.QC = {
            Login: {
                showPopup: jest.fn()
            }
        }

        // 重置 mock
        emailLogin.mockClear()
        setUrl.mockClear()
        mockToast.mockClear()
        global.window.open.mockClear()

        // 设置默认返回值
        emailLogin.mockResolvedValue({
            data: mockUserData
        })
    })

    afterEach(() => {
        if (wrapper) {
            wrapper.destroy()
        }
    })

    describe('组件渲染', () => {
        it('应该正确渲染组件', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            expect(wrapper.exists()).toBe(true)
        })

        it('应该渲染登录表单', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            expect(wrapper.find('.login-wrapper').exists()).toBe(true)
        })

        it('应该包含邮箱输入框', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            const emailField = wrapper.find('v-text-field-stub[label="邮箱号"]')
            expect(emailField.exists()).toBe(true)
        })

        it('应该包含密码输入框', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            const passwordField = wrapper.find('v-text-field-stub[label="密码"]')
            expect(passwordField.exists()).toBe(true)
        })

        it('应该包含登录按钮', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            const loginButton = wrapper.find('v-btn-stub')
            expect(loginButton.exists()).toBe(true)
        })
    })

    describe('表单数据', () => {
        it('应该初始化空表单', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            expect(wrapper.vm.email).toBe(null)
            expect(wrapper.vm.password).toBe(null)
        })

        it('应该能够设置邮箱', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({ email: TEST_EMAIL })
            expect(wrapper.vm.email).toBe(TEST_EMAIL)
        })

        it('应该能够设置密码', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({ password: TEST_PASSWORD })
            expect(wrapper.vm.password).toBe(TEST_PASSWORD)
        })
    })

    describe('登录功能', () => {
        it('应该使用测试账号成功登录', async () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: TEST_EMAIL,
                password: TEST_PASSWORD
            })

            await wrapper.vm.login()

            expect(emailLogin).toHaveBeenCalledWith({
                email: TEST_EMAIL,
                password: TEST_PASSWORD
            })
            expect(store.state.user).toEqual(mockUserData)
            expect(mockToast).toHaveBeenCalledWith({
                type: "success",
                message: "登录成功"
            })
        })

        it('登录成功后应该清空表单', async () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: TEST_EMAIL,
                password: TEST_PASSWORD
            })

            await wrapper.vm.login()

            expect(wrapper.vm.email).toBe(null)
            expect(wrapper.vm.password).toBe(null)
        })

        it('登录成功后应该关闭对话框', async () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: TEST_EMAIL,
                password: TEST_PASSWORD
            })

            await wrapper.vm.login()

            expect(store.state.loginFlag).toBe(false)
        })

        it('登录失败时应该显示错误消息', async () => {
            const errorMessage = '登录失败：邮箱或密码错误'
            emailLogin.mockRejectedValue(new Error(errorMessage))

            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: TEST_EMAIL,
                password: TEST_PASSWORD
            })

            await wrapper.vm.login()

            expect(mockToast).toHaveBeenCalledWith({
                type: "error",
                message: errorMessage
            })
        })
    })

    describe('表单验证', () => {
        it('应该验证邮箱不能为空', async () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: null,
                password: TEST_PASSWORD
            })

            await wrapper.vm.login()

            expect(emailLogin).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalledWith({
                type: "error",
                message: "邮箱格式不正确"
            })
        })

        it('应该验证密码不能为空', async () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: TEST_EMAIL,
                password: ''
            })

            await wrapper.vm.login()

            expect(emailLogin).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalledWith({
                type: "error",
                message: "密码不能为空"
            })
        })

        it('应该验证密码不能为空格', async () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.setData({
                email: TEST_EMAIL,
                password: '   '
            })

            await wrapper.vm.login()

            expect(emailLogin).not.toHaveBeenCalled()
            expect(mockToast).toHaveBeenCalledWith({
                type: "error",
                message: "密码不能为空"
            })
        })
    })

    describe('对话框操作', () => {
        it('close 应该关闭登录对话框', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.vm.close()

            expect(store.state.loginFlag).toBe(false)
        })

        it('openRegister 应该打开注册对话框', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.vm.openRegister()

            expect(store.state.loginFlag).toBe(false)
            expect(store.state.registerFlag).toBe(true)
        })

        it('openForget 应该打开忘记密码对话框', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.vm.openForget()

            expect(store.state.loginFlag).toBe(false)
            expect(store.state.forgetFlag).toBe(true)
        })

        it('openWecaht 应该打开微信登录对话框', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.vm.openWecaht()

            expect(store.state.loginFlag).toBe(false)
            expect(store.state.wechatFlag).toBe(true)
        })
    })

    describe('社交登录', () => {
        beforeEach(() => {
            // 设置组件配置
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })
            wrapper.vm.config = mockConfig
        })

        it('qqLogin 应该在PC端打开QQ登录', () => {
            Object.defineProperty(global.navigator, 'userAgent', {
                writable: true,
                value: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'
            })

            wrapper.vm.qqLogin()

            expect(setUrl).toHaveBeenCalled()
            expect(global.window.open).toHaveBeenCalled()
            const openUrl = global.window.open.mock.calls[0][0]
            expect(openUrl).toContain('graph.qq.com')
            expect(openUrl).toContain(mockConfig.QQ_CLIENT_ID)
        })

        it('qqLogin 应该在移动端使用QC.Login', () => {
            Object.defineProperty(global.navigator, 'userAgent', {
                writable: true,
                value: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)'
            })

            wrapper.vm.qqLogin()

            expect(setUrl).toHaveBeenCalled()
            expect(global.QC.Login.showPopup).toHaveBeenCalledWith({
                appId: mockConfig.QQ_CLIENT_ID,
                redirectURI: mockConfig.QQ_REDIRECT_URL
            })
        })

        it('giteeLogin 应该打开Gitee登录', () => {
            wrapper.vm.giteeLogin()

            expect(setUrl).toHaveBeenCalled()
            expect(global.window.open).toHaveBeenCalled()
            const openUrl = global.window.open.mock.calls[0][0]
            expect(openUrl).toContain('gitee.com/oauth/authorize')
            expect(openUrl).toContain(mockConfig.GITEE_CLIENT_ID)
        })

        it('weiboLogin 应该打开微博登录', () => {
            wrapper.vm.weiboLogin()

            expect(setUrl).toHaveBeenCalled()
            expect(global.window.open).toHaveBeenCalled()
            const openUrl = global.window.open.mock.calls[0][0]
            expect(openUrl).toContain('api.weibo.com/oauth2/authorize')
            expect(openUrl).toContain(mockConfig.WEIBO_CLIENT_ID)
        })
    })

    describe('settingUrl 方法', () => {
        it('应该在文章详情页保存articleId', () => {
            mockRouter.path = '/articleInfo'
            mockRouter.query = { articleId: '123' }

            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.vm.settingUrl()

            expect(setUrl).toHaveBeenCalledWith('articleId=123')
        })

        it('应该保存当前路径', () => {
            mockRouter.path = '/category'
            mockRouter.query = {}

            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            wrapper.vm.settingUrl()

            expect(setUrl).toHaveBeenCalledWith('/category')
        })
    })

    describe('计算属性', () => {
        it('loginFlag 应该正确绑定store状态', () => {
            store.state.loginFlag = true
            wrapper = shallowMount(Login, {
                localVue,
                store,
                vuetify,
                mocks: {
                    $route: mockRouter
                },
                stubs: {
                    'v-dialog': true,
                    'v-card': true,
                    'v-row': true,
                    'v-text-field': true,
                    'v-btn': true,
                    'svg-icon': true
                }
            })

            expect(wrapper.vm.loginFlag).toBe(true)

            wrapper.vm.loginFlag = false
            expect(store.state.loginFlag).toBe(false)
        })
    })
})

