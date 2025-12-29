import { getUser, setUser, removeUser, setSkin, getSkin, setUrl, getUrl, hasAuth } from '@/utils/auth'
import Cookies from 'js-cookie'

// Mock js-cookie
jest.mock('js-cookie', () => ({
    get: jest.fn(),
    set: jest.fn(),
    remove: jest.fn()
}))

describe('auth.js - 工具函数测试', () => {
    let mockSessionStorage

    beforeEach(() => {
        // Mock sessionStorage
        mockSessionStorage = {
            getItem: jest.fn(),
            setItem: jest.fn(),
            removeItem: jest.fn(),
            clear: jest.fn()
        }
        global.window = Object.create(window)
        Object.defineProperty(window, 'sessionStorage', {
            value: mockSessionStorage,
            writable: true
        })

        // 重置 mock
        Cookies.get.mockClear()
        Cookies.set.mockClear()
        Cookies.remove.mockClear()
        mockSessionStorage.getItem.mockClear()
        mockSessionStorage.setItem.mockClear()
    })

    describe('getUser', () => {
        it('应该从 Cookie 获取用户信息', () => {
            const mockUser = { id: 1, nickname: '测试用户' }
            Cookies.get.mockReturnValue(JSON.stringify(mockUser))

            const result = getUser()

            expect(Cookies.get).toHaveBeenCalledWith('user')
            expect(result).toEqual(mockUser)
        })

        it('应该返回 null 当 Cookie 不存在时', () => {
            Cookies.get.mockReturnValue(null)

            const result = getUser()

            expect(result).toBe(null)
        })

        it('应该返回 null 当 Cookie 值为 undefined 时', () => {
            Cookies.get.mockReturnValue(undefined)

            const result = getUser()

            expect(result).toBe(undefined)
        })

        it('应该正确解析 JSON 字符串', () => {
            const userData = { id: 1, nickname: '用户', avatar: 'avatar.jpg' }
            Cookies.get.mockReturnValue(JSON.stringify(userData))

            const result = getUser()

            expect(result).toEqual(userData)
        })

        it('应该处理无效的 JSON 字符串', () => {
            Cookies.get.mockReturnValue('invalid json')

            expect(() => getUser()).toThrow()
        })
    })

    describe('setUser', () => {
        it('应该设置用户信息到 Cookie', () => {
            const userData = { id: 1, nickname: '测试用户' }

            setUser(userData)

            expect(Cookies.set).toHaveBeenCalledWith('user', userData, { expires: 1 / 24 })
        })

        it('应该使用正确的过期时间', () => {
            const userData = { id: 1 }

            setUser(userData)

            expect(Cookies.set).toHaveBeenCalledWith('user', userData, { expires: 1 / 24 })
        })

        it('应该能够设置不同的用户数据', () => {
            const userData1 = { id: 1, nickname: '用户1' }
            const userData2 = { id: 2, nickname: '用户2' }

            setUser(userData1)
            expect(Cookies.set).toHaveBeenCalledWith('user', userData1, { expires: 1 / 24 })

            Cookies.set.mockClear()

            setUser(userData2)
            expect(Cookies.set).toHaveBeenCalledWith('user', userData2, { expires: 1 / 24 })
        })
    })

    describe('removeUser', () => {
        it('应该删除用户 Cookie', () => {
            removeUser()

            expect(Cookies.remove).toHaveBeenCalledWith('user')
        })

        it('应该能够多次调用', () => {
            removeUser()
            removeUser()

            expect(Cookies.remove).toHaveBeenCalledTimes(2)
            expect(Cookies.remove).toHaveBeenCalledWith('user')
        })
    })

    describe('setSkin', () => {
        it('应该设置皮肤到 sessionStorage', () => {
            const skinValue = 'dark'

            setSkin(skinValue)

            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('skin', skinValue)
        })

        it('应该能够设置不同的皮肤值', () => {
            setSkin('light')
            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('skin', 'light')

            mockSessionStorage.setItem.mockClear()

            setSkin('dark')
            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('skin', 'dark')
        })

        it('应该处理空字符串', () => {
            setSkin('')

            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('skin', '')
        })
    })

    describe('getSkin', () => {
        it('应该从 sessionStorage 获取皮肤', () => {
            mockSessionStorage.getItem.mockReturnValue('dark')

            const result = getSkin()

            expect(mockSessionStorage.getItem).toHaveBeenCalledWith('skin')
            expect(result).toBe('dark')
        })

        it('应该返回 null 当皮肤不存在时', () => {
            mockSessionStorage.getItem.mockReturnValue(null)

            const result = getSkin()

            expect(result).toBe(null)
        })

        it('应该返回正确的皮肤值', () => {
            mockSessionStorage.getItem.mockReturnValue('light')

            const result = getSkin()

            expect(result).toBe('light')
        })
    })

    describe('setUrl', () => {
        it('应该设置 URL 到 sessionStorage', () => {
            const urlValue = 'https://example.com'

            setUrl(urlValue)

            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('baseUrl', urlValue)
        })

        it('应该能够设置不同的 URL', () => {
            setUrl('https://example.com')
            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('baseUrl', 'https://example.com')

            mockSessionStorage.setItem.mockClear()

            setUrl('https://test.com')
            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('baseUrl', 'https://test.com')
        })

        it('应该处理空字符串', () => {
            setUrl('')

            expect(mockSessionStorage.setItem).toHaveBeenCalledWith('baseUrl', '')
        })
    })

    describe('getUrl', () => {
        it('应该从 sessionStorage 获取 URL', () => {
            mockSessionStorage.getItem.mockReturnValue('https://example.com')

            const result = getUrl()

            expect(mockSessionStorage.getItem).toHaveBeenCalledWith('baseUrl')
            expect(result).toBe('https://example.com')
        })

        it('应该返回 null 当 URL 不存在时', () => {
            mockSessionStorage.getItem.mockReturnValue(null)

            const result = getUrl()

            expect(result).toBe(null)
        })

        it('应该返回正确的 URL', () => {
            mockSessionStorage.getItem.mockReturnValue('https://test.com')

            const result = getUrl()

            expect(result).toBe('https://test.com')
        })
    })

    describe('hasAuth', () => {
        it('应该返回 true 当权限存在时', () => {
            const perms = ['user:read', 'user:write', 'admin:delete']
            const perm = 'user:read'

            const result = hasAuth(perms, perm)

            expect(result).toBe(true)
        })

        it('应该返回 true 当权限部分匹配时', () => {
            const perms = ['user:read', 'user:write']
            const perm = 'user'

            const result = hasAuth(perms, perm)

            expect(result).toBe(true)
        })

        it('应该返回 false 当权限不存在时', () => {
            const perms = ['user:read', 'user:write']
            const perm = 'admin:delete'

            const result = hasAuth(perms, perm)

            expect(result).toBe(false)
        })

        it('应该返回 false 当权限数组为空时', () => {
            const perms = []
            const perm = 'user:read'

            const result = hasAuth(perms, perm)

            expect(result).toBe(false)
        })

        it('应该处理多个匹配的情况', () => {
            const perms = ['user:read', 'user:write', 'admin:read']
            const perm = 'read'

            const result = hasAuth(perms, perm)

            expect(result).toBe(true)
        })

        it('应该处理空权限字符串', () => {
            const perms = ['user:read', 'user:write']
            const perm = ''

            const result = hasAuth(perms, perm)

            expect(result).toBe(true) // 空字符串会匹配所有
        })

        it('应该处理不匹配的情况', () => {
            const perms = ['user:read', 'user:write']
            const perm = 'admin'

            const result = hasAuth(perms, perm)

            expect(result).toBe(false)
        })
    })

    describe('边界情况', () => {
        it('getUser 应该处理 null 值', () => {
            Cookies.get.mockReturnValue(null)

            const result = getUser()

            expect(result).toBe(null)
        })

        it('setUser 应该处理对象数据', () => {
            const complexData = {
                id: 1,
                nickname: '用户',
                avatar: 'avatar.jpg',
                tags: ['tag1', 'tag2']
            }

            setUser(complexData)

            expect(Cookies.set).toHaveBeenCalledWith('user', complexData, { expires: 1 / 24 })
        })

        it('hasAuth 应该处理 undefined 权限', () => {
            const perms = ['user:read']
            const perm = undefined

            const result = hasAuth(perms, perm)

            expect(result).toBe(false)
        })

        it('hasAuth 应该处理 null 权限', () => {
            const perms = ['user:read']
            const perm = null

            const result = hasAuth(perms, perm)

            expect(result).toBe(false)
        })
    })
})

