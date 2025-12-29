module.exports = {
    // 测试环境
    testEnvironment: 'jsdom',
    
    // 模块文件扩展名
    moduleFileExtensions: [
        'js',
        'json',
        'vue'
    ],
    
    // 转换配置
    transform: {
        '^.+\\.vue$': 'vue-jest',
        '^.+\\.jsx?$': 'babel-jest'
    },
    
    // 处理静态资源
    moduleNameMapper: {
        '^@/(.*)$': '<rootDir>/src/$1',
        '\\.(css|less|scss|sass)$': '<rootDir>/src/tests/mocks/styleMock.js',
        '\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$': '<rootDir>/src/tests/mocks/fileMock.js'
    },
    
    // 快照序列化器（可选）
    // snapshotSerializers: [
    //     'jest-serializer-vue'
    // ],
    
    // 测试文件匹配模式
    testMatch: [
        '**/tests/unit/**/*.spec.(js|jsx|ts|tsx)|**/__tests__/*.(js|jsx|ts|tsx)'
    ],
    
    // 测试 URL
    testURL: 'http://localhost/',
    
    // 收集覆盖率（默认关闭，只在需要时开启）
    collectCoverage: false,
    
    // 覆盖率收集目录
    coverageDirectory: 'coverage',
    
    // 覆盖率报告格式
    coverageReporters: [
        'text',
        'text-summary',
        'html',
        'lcov',
        'json'
    ],
    
    // 需要收集覆盖率的文件（只收集测试的文件，避免其他文件转换错误）
    collectCoverageFrom: [
        'src/view/article/NewlyArticle.vue',
        'src/view/article/ArticleList.vue',
        '!src/main.js',
        '!src/router/**',
        '!src/store/**',
        '!**/node_modules/**',
        '!**/tests/**',
        '!**/coverage/**',
        '!src/components/**',
        '!src/utils/**',
        '!src/api/**',
        '!src/view/category/**',
        // 在排除规则之后，重新包含需要测试的组件
        'src/components/banner/Banner.vue',
        'src/components/pagination/index.vue',
        'src/view/category/HotCategory.vue',
        'src/utils/auth.js',
        'src/api/index.js',
        'src/components/model/Login.vue',
        'src/components/comment/index.vue',
        'src/components/layout/Search.vue',
        'src/view/search/index.vue'
    ],
    
    // 覆盖率阈值（可选，根据需要调整）
    coverageThreshold: {
        global: {
            branches: 70,
            functions: 70,
            lines: 70,
            statements: 70
        }
    },
    
    // 设置文件
    setupFilesAfterEnv: ['<rootDir>/src/tests/setup.js'],
    
    // 忽略模式
    testPathIgnorePatterns: [
        '/node_modules/'
    ],
    
    // 转换忽略模式
    transformIgnorePatterns: [
        '/node_modules/(?!(vuetify)/)'
    ],
    
    // 全局设置
    globals: {
        'vue-jest': {
            // 禁用 babel，使用 vue-template-compiler 直接编译
            babelConfig: false
        }
    }
}

