// Jest 测试环境设置文件
const { config } = require('@vue/test-utils')

// 确保 Vue 只有一个实例
// 这可以防止 "Multiple instances of Vue detected" 错误

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: jest.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // deprecated
        removeListener: jest.fn(), // deprecated
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
    })),
})

// Mock IntersectionObserver
global.IntersectionObserver = class IntersectionObserver {
    constructor() {}
    disconnect() {}
    observe() {}
    takeRecords() {
        return []
    }
    unobserve() {}
}

// Mock ResizeObserver
global.ResizeObserver = class ResizeObserver {
    constructor() {}
    disconnect() {}
    observe() {}
    unobserve() {}
}

// 配置 Vue Test Utils
config.mocks = {
    $router: {
        push: jest.fn(),
        replace: jest.fn(),
        go: jest.fn(),
        back: jest.fn(),
        forward: jest.fn()
    },
    $route: {
        path: '/',
        query: {},
        params: {}
    }
}

