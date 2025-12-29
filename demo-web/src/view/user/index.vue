<template>
    <div class="user-main">
        <div class="content">
            <!-- 用户信息卡片 -->
            <v-card class="user-card">
                <div class="user-header">
                    <img class="avatar-img" :src="user.avatar || defaultAvatar" @error="handleAvatarError" />
                    <div class="user-info">
                        <h2 class="nickname">{{ user.nickname || '未设置昵称' }}</h2>
                        <p class="intro" v-if="user.intro">{{ user.intro }}</p>
                        <p class="intro" v-else>这个人很懒，还没有设置个人简介</p>
                        <div class="user-meta">
                            <span v-if="user.email" class="meta-item">
                                <i class="iconfont icon-youxiang"></i> {{ user.email }}
                    </span>
                            <a v-if="user.webSite" :href="user.webSite" target="_blank" class="meta-item">
                                <i class="iconfont icon-link"></i> 个人站点
                            </a>
                </div>
                    </div>
                </div>
            </v-card>

            <!-- 功能标签页 -->
            <v-card class="tabs-card">
                <div class="tabs-header">
                    <button 
                        v-for="(tab, index) in tabs" 
                        :key="index"
                        :class="['tab-btn', { active: currentTab === index }]"
                        @click="switchTab(index)"
                    >
                        <i :class="tab.icon"></i>
                        <span>{{ tab.label }}</span>
                        <span v-if="tab.count !== undefined" class="count-badge">{{ tab.count }}</span>
                    </button>
            </div>

                <!-- 我的文章 -->
                <div v-show="currentTab === 0" class="tab-content">
                    <!-- 加载状态 -->
                    <div v-if="loadingArticles" class="loading-state">
                        <div class="loading-spinner"></div>
                        <p>正在加载文章...</p>
                    </div>
                    <!-- 文章列表 -->
                    <div v-else-if="myArticles.length > 0" class="articles-container">
                        <ul class="article-list">
                            <li 
                                v-for="article in myArticles" 
                                :key="article.id"
                                class="article-item"
                                @click="goToArticle(article.id)"
                            >
                                <div class="article-thumbnail">
                                    <img 
                                        :src="article.avatar || defaultArticleImg" 
                                        :alt="article.title"
                                        @error="handleArticleImgError"
                                    />
                                    <span v-if="article.isStick == 1" class="badge top">置顶</span>
                                    <span v-if="article.isOriginal == 1" class="badge original">原创</span>
                                    <span v-if="article.isPublish === 0 || article.isPublish === null" class="badge draft">未发布</span>
                                </div>
                                <div class="article-info">
                                    <div class="article-header">
                                        <h3 class="article-title">{{ article.title }}</h3>
                                        <div class="article-actions" @click.stop>
                                            <button 
                                                :class="['action-btn', (article.isPublish === 1) ? 'unpublish-btn' : 'publish-btn']"
                                                @click="pubOrShelfArticle(article.id, article.title, article.isPublish)"
                                                :title="(article.isPublish === 1) ? '下架文章' : '发布文章'"
                                            >
                                                <i :class="(article.isPublish === 1) ? 'iconfont icon-xiajia' : 'iconfont icon-fabu'"></i>
                                                {{ (article.isPublish === 1) ? '下架' : '发布' }}
                                            </button>
                                            <button 
                                                class="action-btn edit-btn" 
                                                @click="editArticle(article.id)"
                                                title="编辑文章"
                                            >
                                                <i class="iconfont icon-bianji"></i>
                                                编辑
                                            </button>
                                            <button 
                                                class="action-btn delete-btn" 
                                                @click="deleteArticle(article.id, article.title)"
                                                title="删除文章"
                                            >
                                                <i class="iconfont icon-shanchu"></i>
                                                删除
                                            </button>
                                        </div>
                                    </div>
                                    <p class="article-summary">{{ article.summary }}</p>
                                    <div class="article-meta">
                                        <span class="meta-item">
                                            <i class="iconfont icon-yuedu"></i> {{ article.quantity || 0 }} 阅读
                                        </span>
                                        <span class="meta-item">
                                            <i class="iconfont icon-pinglun"></i> {{ article.commentCount || 0 }} 评论
                                        </span>
                                        <span class="meta-item">
                                            <i class="iconfont icon-dianzan"></i> {{ article.likeCount || 0 }} 点赞
                                        </span>
                                        <span class="meta-item date">{{ formatDate(article.createTime) }}</span>
                                    </div>
                                    <div class="article-tags" v-if="article.tagList && article.tagList.length > 0">
                                        <span 
                                            v-for="tag in article.tagList" 
                                            :key="tag.id"
                                            class="tag"
                                        >
                                            <i class="iconfont icon-biaoqian"></i> {{ tag.name }}
                                        </span>
                                    </div>
                                </div>
                            </li>
                        </ul>
                        <!-- 分页 -->
                        <div class="pagination-wrapper" v-if="articlePageTotal > articlePageSize">
                            <Pagination 
                                :page="articlePageNo" 
                                :page-size="articlePageSize" 
                                :total="articlePageTotal"
                                :on-page-change="onArticlePageChange"
                            />
                        </div>
                    </div>
                    <!-- 空状态 -->
                    <div v-else class="empty-state">
                <svg-icon icon-class="empty"></svg-icon>
                        <p>还没有发表过文章呢，快去写一篇吧~</p>
                </div>
            </div>

                <!-- 点赞的文章 -->
                <div v-show="currentTab === 1" class="tab-content">
                    <div class="empty-state">
                        <svg-icon icon-class="empty"></svg-icon>
                        <p>看来站点暂时还没有你喜欢的文章呢</p>
                </div>
                </div>

                <!-- 发布/编辑文章 -->
                <div v-show="currentTab === 2" class="tab-content article-editor">
                    <div class="editor-form">
                        <!-- 上方基本信息区 -->
                        <div class="editor-header">
                            <div class="form-row">
                                <div class="form-item full-width">
                                    <label>文章标题 <span class="required">*</span></label>
                                    <input 
                                        type="text" 
                                        v-model="articleForm.title" 
                                        placeholder="请输入文章标题"
                                        maxlength="100"
                                    />
            </div>
        </div>

                            <div class="form-row">
                                <div class="form-item">
                                    <label>文章分类</label>
                                    <select v-model="articleForm.categoryName" class="form-select">
                                        <option value="">请选择分类</option>
                                        <option v-for="cat in categories" :key="cat.id" :value="cat.name">
                                            {{ cat.name }}
                                        </option>
                                    </select>
                                </div>
                                <div class="form-item">
                                    <label>文章标签</label>
                                    <div class="tags-selector">
                                        <div class="selected-tags">
                                            <span 
                                                v-for="(tag, index) in selectedTags" 
                                                :key="index"
                                                class="tag-chip"
                                                @click="removeTag(index)"
                                            >
                                                {{ tag }}
                                                <i class="iconfont icon-close"></i>
                                            </span>
                                        </div>
                                        <select v-model="tempTag" @change="addTag" class="form-select">
                                            <option value="">选择标签</option>
                                            <option v-for="tag in allTags" :key="tag.id" :value="tag.name">
                                                {{ tag.name }}
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-item full-width">
                                    <label>文章简介</label>
                                    <textarea 
                                        v-model="articleForm.summary" 
                                        placeholder="请输入文章简介"
                                        rows="3"
                                        maxlength="200"
                                    ></textarea>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-item full-width">
                                    <label>封面图片URL</label>
                                    <input 
                                        type="text" 
                                        v-model="articleForm.avatar" 
                                        placeholder="请输入封面图片URL"
                                    />
                                </div>
                            </div>
                        </div>

                        <!-- 右侧选项区 -->
                        <div class="editor-sidebar">
                            <div class="sidebar-section">
                                <h3 class="section-title">发布设置</h3>
                                <div class="form-item checkbox-item">
                                    <label class="checkbox-label">
                                        <input type="checkbox" v-model.number="articleForm.isPublish" :true-value="1" :false-value="0" />
                                        <span>立即发布</span>
                                    </label>
                                </div>
                                <!-- <div class="form-item checkbox-item">
                                    <label class="checkbox-label">
                                        <input type="checkbox" v-model.number="articleForm.isStick" :true-value="1" :false-value="0" />
                                        <span>置顶</span>
                                    </label>
                                </div> -->
                                <div class="form-item checkbox-item">
                                    <label class="checkbox-label">
                                        <input type="checkbox" v-model.number="articleForm.isSecret" :true-value="1" :false-value="0" />
                                        <span>私密文章</span>
                                    </label>
                                </div>
                                <!-- <div class="form-item checkbox-item">
                                    <label class="checkbox-label">
                                        <input type="checkbox" v-model.number="articleForm.isCarousel" :true-value="1" :false-value="0" />
                                        <span>首页轮播</span>
                                    </label>
                                </div> -->
                            </div>

                            <div class="sidebar-section">
                                <h3 class="section-title">文章属性</h3>
                                <div class="form-item checkbox-item">
                                    <label class="checkbox-label">
                                        <input type="checkbox" v-model.number="articleForm.isOriginal" :true-value="1" :false-value="0" />
                                        <span>原创文章</span>
                                    </label>
                                </div>
                                <div class="form-item" v-if="articleForm.isOriginal === 0">
                                    <label>转载地址</label>
                                    <input 
                                        type="text" 
                                        v-model="articleForm.originalUrl" 
                                        placeholder="请输入转载地址"
                                    />
                                </div>
                            </div>

                            <!-- <div class="sidebar-section">
                                <h3 class="section-title">SEO设置</h3>
                                <div class="form-item">
                                    <label>SEO关键词</label>
                                    <input 
                                        type="text" 
                                        v-model="articleForm.keywords" 
                                        placeholder="多个关键词用逗号分隔"
                                    />
                                </div>
                            </div>

                            <div class="sidebar-section">
                                <h3 class="section-title">备注</h3>
                                <div class="form-item">
                                    <label>备注说明</label>
                                    <textarea 
                                        v-model="articleForm.remark" 
                                        placeholder="备注信息（可选）"
                                        rows="3"
                                    ></textarea>
                                </div>
                </div> -->
                        </div>
                    </div>

                    <!-- 下方中央 Markdown 编辑区 -->
                    <div class="editor-content">
                        <div class="form-item full-width">
                            <div class="editor-header-row">
                                <label>Markdown内容 <span class="required">*</span></label>
                                <button 
                                    class="ai-push-btn" 
                                    @click="openAIPush"
                                    :disabled="aiLoading"
                                    :title="aiLoading ? 'AI生成中...' : 'AI推送精品内容'"
                                >
                                    <i class="iconfont icon-bianji"></i>
                                    {{ aiLoading ? '生成中...' : 'AI推送' }}
                                </button>
                            </div>
                            <div class="markdown-editor-wrapper">
                                <v-md-editor 
                                    v-model="articleForm.contentMd" 
                                    height="600px"
                                    mode="edit"
                                ></v-md-editor>
                </div>
            </div>
        </div>

                    <!-- 底部操作按钮 -->
                    <div class="form-actions">
                        <v-btn 
                            @click="saveArticle" 
                            class="submit-btn"
                            :loading="submitting"
                            :disabled="submitting"
                        >
                            <span v-if="!submitting">{{ editingArticleId ? '更新文章' : '确定' }}</span>
                            <span v-else>保存中...</span>
                        </v-btn>
                        <v-btn 
                            @click="resetArticleForm" 
                            class="cancel-btn"
                            :disabled="submitting"
                        >
                            重置
                        </v-btn>
                    </div>
                </div>

                <!-- AI推送对话框 -->
                <v-dialog v-model="showAIModal" max-width="600" persistent class="ai-dialog-wrapper">
                    <v-card class="ai-card-wrapper">
                        <v-card-title class="ai-modal-header">
                            <span>AI推送精品内容</span>
                            <button class="close-btn" @click="closeAIModal">
                                <i class="iconfont icon-close"></i>
                            </button>
                        </v-card-title>
                        <v-card-text class="ai-modal-content">
                            <div class="form-item">
                                <label>内容描述 (message) <span class="required">*</span></label>
                                <p class="form-tip">请描述您想要生成的文章内容，例如："写一篇关于Vue.js的入门教程"</p>
                                <textarea 
                                    v-model="aiPrompt" 
                                    placeholder="请输入您想要生成的内容描述..."
                                    rows="5"
                                    :disabled="aiLoading"
                                ></textarea>
                            </div>
                            <div class="form-item">
                                <label>系统提示 (systemPrompt) <span class="optional">可选</span></label>
                                <p class="form-tip">系统级别的提示词，用于指导AI的生成风格和方向</p>
                                <textarea 
                                    v-model="aiSystemPrompt" 
                                    placeholder="请输入系统提示词（可选）..."
                                    rows="3"
                                    :disabled="aiLoading"
                                ></textarea>
                            </div>
                            <div class="form-item">
                                <label>温度参数 (temperature)</label>
                                <p class="form-tip">控制生成内容的随机性，范围0-1，0表示更确定性的输出，1表示更随机的输出</p>
                                <input 
                                    type="number" 
                                    v-model.number="aiTemperature" 
                                    min="0" 
                                    max="1" 
                                    step="0.1"
                                    placeholder="0"
                                    :disabled="aiLoading"
                                />
                            </div>
                        </v-card-text>
                        <v-card-actions class="ai-modal-actions">
                            <v-btn 
                                @click="closeAIModal" 
                                :disabled="aiLoading"
                                class="cancel-btn"
                            >
                                取消
                            </v-btn>
                            <v-btn 
                                @click="pushAIContent" 
                                :loading="aiLoading"
                                :disabled="aiLoading || !aiPrompt || aiPrompt.trim() === ''"
                                class="submit-btn"
                            >
                                {{ aiLoading ? '生成中...' : '生成内容' }}
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>

                <!-- 确认对话框 -->
                <v-dialog v-model="showConfirmDialog" max-width="500" persistent>
                    <v-card class="confirm-dialog-card">
                        <v-card-title class="confirm-dialog-title">
                            <i class="iconfont icon-tixingshixin" style="color: #F57C00; margin-right: 8px;"></i>
                            {{ confirmTitle }}
                        </v-card-title>
                        <v-card-text class="confirm-dialog-content">
                            {{ confirmMessage }}
                        </v-card-text>
                        <v-card-actions class="confirm-dialog-actions">
                            <v-spacer></v-spacer>
                            <v-btn 
                                text 
                                @click="showConfirmDialog = false"
                                class="cancel-btn"
                            >
                                取消
                            </v-btn>
                            <v-btn 
                                @click="handleConfirm"
                                class="confirm-btn"
                            >
                                确定
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>

                <!-- 修改资料 -->
                <div v-show="currentTab === 3" class="tab-content">
                    <div class="edit-form">
                        <div class="form-item">
                            <label>个人昵称</label>
                            <input 
                                type="text" 
                                v-model="editUser.nickname" 
                                placeholder="请输入昵称"
                                maxlength="20"
                            />
                        </div>
                        <div class="form-item">
                            <label>个人简介</label>
                            <textarea 
                                v-model="editUser.intro" 
                                placeholder="介绍一下自己吧~"
                                rows="4"
                                maxlength="200"
                            ></textarea>
                        </div>
                        <div class="form-item">
                            <label>个人站点</label>
                            <input 
                                type="text" 
                                v-model="editUser.webSite" 
                                placeholder="https://example.com"
                            />
                        </div>
                        <div class="form-item">
                            <label>个人邮箱</label>
                            <input 
                                type="email" 
                                v-model="editUser.email" 
                                placeholder="example@email.com"
                                disabled
                            />
                            <span class="form-tip">邮箱不可修改</span>
                        </div>
                        <div class="form-actions">
                            <v-btn 
                                @click="updateUserInfo" 
                                class="submit-btn"
                                :loading="updating"
                                :disabled="updating"
                            >
                                <span v-if="!updating">保存修改</span>
                                <span v-else>保存中...</span>
                            </v-btn>
                        </div>
                    </div>
                </div>
            </v-card>
        </div>
    </div>
</template>

<script>
import { updateUserInfo, getUserInfo, fetchArticleList, getUserArticles, addOrUpdateArticle, getArticleDetail, featchCategory, fetchTagList, pubOrShelfArticle as pubOrShelfArticleApi, deleteArticle, aiChat } from '@/api'
import { getUser } from '@/utils/auth'
import Pagination from '@/components/pagination/index.vue'
import VMdEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import hljs from 'highlight.js'

// 配置 markdown 编辑器主题
VMdEditor.use(githubTheme, {
    Hljs: hljs,
})

export default {
    name: 'UserCenter',
    components: {
        Pagination,
        VMdEditor
    },
    data() {
        return {
            user: {},
            editUser: {},
            currentTab: 0, // 0: 我的文章, 1: 点赞的文章, 2: 发布文章, 3: 修改资料
            updating: false,
            defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2RkZCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjE4IiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5aSx6LSlPC90ZXh0Pjwvc3ZnPg==',
            defaultArticleImg: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjEwIiBoZWlnaHQ9IjE0MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjEwIiBoZWlnaHQ9IjE0MCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjE0IiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Zu+54mH5aSx6LSlPC90ZXh0Pjwvc3ZnPg==',
            // 我的文章相关
            myArticles: [],
            articlePageNo: 1,
            articlePageSize: 10,
            articlePageTotal: 0,
            loadingArticles: false,
            tabs: [
                { label: '我的文章', icon: 'iconfont icon-wenzhang', count: null },
                { label: '点赞的文章', icon: 'iconfont icon-dianzan', count: null },
                { label: '创作', icon: 'iconfont icon-bianji', count: null },
                { label: '修改资料', icon: 'iconfont icon-gerenzhongxin', count: null }
            ],
            // 文章编辑相关
            articleForm: {
                id: 0,
                title: '',
                summary: '',
                content: '',
                contentMd: '',
                avatar: '',
                categoryName: '',
                isCarousel: 0,
                isOriginal: 0,
                isPublish: 0,
                isSecret: 0,
                isStick: 0,
                keywords: '',
                originalUrl: '',
                remark: '',
                tags: [],
                userId: 0
            },
            categories: [],
            allTags: [],
            selectedTags: [],
            submitting: false,
            editingArticleId: null,
            tempTag: '',
            // AI推送相关
            aiLoading: false,
            aiPrompt: '',
            aiSystemPrompt: '',
            aiTemperature: 0,
            showAIModal: false,
            // 确认对话框相关
            showConfirmDialog: false,
            confirmTitle: '',
            confirmMessage: '',
            confirmCallback: null,
            confirmAction: '' // 'publish' 或 'delete'
        }
    },
    created() {
        this.loadUserInfo()
        this.loadCategories()
        this.loadTags()
    },
    methods: {
        // 加载用户信息
        loadUserInfo() {
        getUserInfo().then(res => {
                // /v1/user/info 接口返回的用户信息
            this.user = res.data
            this.editUser = JSON.parse(JSON.stringify(res.data))
                console.log('从 /v1/user/info 获取的用户信息:', this.user)
                // 加载我的文章
                this.loadMyArticles()
        }).catch(err => {
                console.error('获取用户信息失败:', err)
                this.$toast({ type: "error", message: "获取用户信息失败" })
                this.$router.go(-1)
            })
        },
        // 加载我的文章列表
        loadMyArticles() {
            // 检查用户昵称是否存在
            if (!this.user.nickname) {
                console.warn('用户昵称为空，无法加载文章列表')
                this.myArticles = []
                this.articlePageTotal = 0
                this.tabs[0].count = 0
                return
            }
            
            this.loadingArticles = true
            const params = {
                pageNo: this.articlePageNo,
                pageSize: this.articlePageSize,
                // 如果后端支持，可以传递昵称参数
                nickname: this.user.nickname
                // 注意：不传递 isPublish 参数，以获取所有状态的文章（包括已发布和未发布的）
            }
            
            // 优先尝试使用专门的用户文章API
            getUserArticles(params).then(res => {
                // 获取所有文章，包括已发布（isPublish=1）和未发布（isPublish=0）的
                const allArticles = res.data.records || []
                // 确保显示所有状态的文章，不过滤 isPublish
                this.myArticles = allArticles
                this.articlePageTotal = res.data.total || 0
                // 更新标签页的文章数量
                this.tabs[0].count = this.articlePageTotal
                console.log('我的文章列表（包含所有状态）:', this.myArticles)
            }).catch(err => {
                // 如果专门的API不存在，则获取所有文章并在前端使用昵称过滤
                console.warn('用户文章API不存在，使用前端昵称过滤方式:', err)
                this.loadMyArticlesWithFilter(params)
            }).finally(() => {
                this.loadingArticles = false
            })
        },
        // 通过前端过滤获取我的文章（备选方案）- 使用用户昵称过滤
        loadMyArticlesWithFilter(params) {
            // 确保不限制 isPublish，获取所有状态的文章
            const fetchParams = {
                ...params
                // 不传递 isPublish 参数，以获取所有状态的文章
            }
            
            fetchArticleList(fetchParams).then(res => {
                const allArticles = res.data.records || []
                const userNickname = this.user.nickname
                
                console.log('=== 文章过滤调试信息（使用昵称过滤）===')
                console.log('1. 所有文章列表:', allArticles)
                console.log('2. 当前登录用户信息 (来自 /v1/user/info):', this.user)
                console.log('3. 用户昵称 (用于过滤):', userNickname)
                
                if (!userNickname) {
                    console.error('❌ 用户昵称为空，无法过滤文章')
                    this.$toast({ type: "error", message: "用户昵称为空，无法获取文章列表" })
                    this.myArticles = []
                    this.articlePageTotal = 0
                    this.tabs[0].count = 0
                    return
                }
                
                // 在前端过滤出当前用户的文章 - 使用昵称匹配
                const filteredArticles = allArticles.filter(article => {
                    // 打印第一篇文章的结构，帮助调试
                    if (allArticles.indexOf(article) === 0) {
                        console.log('4. 文章对象结构示例:', article)
                        console.log('5. 文章中的作者相关字段:')
                        console.log('   - article.username:', article.username)
                        console.log('   - article.author:', article.author)
                        console.log('   - article.user?.nickname:', article.user?.nickname)
                        console.log('   - article.user?.username:', article.user?.username)
                    }
                    
                    // 使用昵称匹配文章作者
                    // 尝试多种可能的字段名来匹配用户昵称
                    const isMatch = 
                        article.username === userNickname ||
                        article.author === userNickname ||
                        (article.user && article.user.nickname === userNickname) ||
                        (article.user && article.user.username === userNickname)
                    
                    if (allArticles.indexOf(article) === 0) {
                        console.log('6. 匹配结果:', isMatch)
                        console.log('   匹配条件:')
                        console.log('   - article.username === userNickname:', article.username === userNickname)
                        console.log('   - article.author === userNickname:', article.author === userNickname)
                        console.log('   - article.user?.nickname === userNickname:', article.user?.nickname === userNickname)
                        console.log('   - article.user?.username === userNickname:', article.user?.username === userNickname)
                    }
                    
                    return isMatch
                })
                
                console.log('7. 过滤后的文章数量:', filteredArticles.length)
                console.log('8. 过滤后的文章列表:', filteredArticles)
                console.log('=== 调试信息结束 ===')
                
                this.myArticles = filteredArticles
                // 注意：总数可能不准确，因为这是前端过滤的结果
                // 理想情况下应该使用后端过滤的总数
                this.articlePageTotal = filteredArticles.length
                this.tabs[0].count = filteredArticles.length
                
                // 如果当前页没有文章，提示用户
                if (filteredArticles.length === 0 && allArticles.length > 0) {
                    console.warn('⚠️ 当前页有文章，但没有找到属于当前用户的文章')
                    console.warn('用户昵称:', userNickname)
                    console.warn('请检查文章对象中的作者字段是否与用户昵称匹配')
                }
            }).catch(err => {
                console.error('获取文章列表失败:', err)
                this.$toast({ type: "error", message: "获取文章列表失败" })
            })
        },
        // 切换标签页
        switchTab(index) {
            this.currentTab = index
            // 切换到我的文章时，如果没有加载过，则加载
            if (index === 0 && this.myArticles.length === 0 && !this.loadingArticles) {
                this.loadMyArticles()
            }
            // 切换到发布文章时，重置表单
            if (index === 2 && !this.editingArticleId) {
                this.resetArticleForm()
            }
        },
        // 文章分页
        onArticlePageChange(page) {
            this.articlePageNo = page
            document.documentElement.scrollTop = 0
            this.loadMyArticles()
        },
        // 跳转到文章详情
        goToArticle(articleId) {
            this.$router.push({ 
                path: '/articleInfo', 
                query: { articleId: articleId } 
            })
        },
        // 更新用户信息
        updateUserInfo() {
            if (!this.editUser.nickname || this.editUser.nickname.trim() === '') {
                this.$toast({ type: "error", message: "昵称不能为空" })
                return
            }
            this.updating = true
            updateUserInfo(this.editUser).then(res => {
                this.user = JSON.parse(JSON.stringify(this.editUser))
                this.$toast({ type: "success", message: res.message || "修改成功" })
            }).catch(err => {
                this.$toast({ type: "error", message: err.message || "修改失败" })
            }).finally(() => {
                this.updating = false
            })
        },
        // 格式化日期
        formatDate(date) {
            if (!date) return ''
            const d = new Date(date)
            const year = d.getFullYear()
            const month = String(d.getMonth() + 1).padStart(2, '0')
            const day = String(d.getDate()).padStart(2, '0')
            return `${year}-${month}-${day}`
        },
        // 头像加载错误处理
        handleAvatarError(e) {
            e.target.src = this.defaultAvatar
        },
        // 文章图片加载错误处理
        handleArticleImgError(e) {
            e.target.src = this.defaultArticleImg
        },
        // 加载分类列表
        loadCategories() {
            featchCategory().then(res => {
                this.categories = res.data || []
            }).catch(err => {
                console.error('获取分类失败:', err)
            })
        },
        // 加载标签列表
        loadTags() {
            fetchTagList().then(res => {
                this.allTags = res.data || []
            }).catch(err => {
                console.error('获取标签失败:', err)
            })
        },
        // 添加标签
        addTag() {
            if (this.tempTag && !this.selectedTags.includes(this.tempTag)) {
                this.selectedTags.push(this.tempTag)
                this.articleForm.tags = this.selectedTags.map(name => ({ name }))
                this.tempTag = ''
            }
        },
        // 移除标签
        removeTag(index) {
            this.selectedTags.splice(index, 1)
            this.articleForm.tags = this.selectedTags.map(name => ({ name }))
        },
        // 编辑文章
        editArticle(articleId) {
            this.editingArticleId = articleId
            this.currentTab = 2 // 切换到发布文章标签页
            
            getArticleDetail(articleId).then(res => {
                const article = res.data
                this.articleForm = {
                    id: article.id || 0,
                    title: article.title || '',
                    summary: article.summary || '',
                    content: article.content || '',
                    contentMd: article.contentMd || '',
                    avatar: article.avatar || '',
                    categoryName: article.category?.name || '',
                    isCarousel: article.isCarousel || 0,
                    isOriginal: article.isOriginal || 0,
                    isPublish: article.isPublish || 0,
                    isSecret: article.isSecret || 0,
                    isStick: article.isStick || 0,
                    keywords: article.keywords || '',
                    originalUrl: article.originalUrl || '',
                    remark: article.remark || '',
                    tags: article.tagList || [],
                    userId: this.user.id || 0
                }
                // 设置选中的标签
                this.selectedTags = (article.tagList || []).map(tag => tag.name || tag)
                this.articleForm.tags = this.selectedTags.map(name => ({ name }))
            }).catch(err => {
                console.error('获取文章详情失败:', err)
                this.$toast({ type: "error", message: "获取文章详情失败" })
            })
        },
        // 保存文章
        saveArticle() {
            // 验证必填字段
            if (!this.articleForm.title || this.articleForm.title.trim() === '') {
                this.$toast({ type: "error", message: "请输入文章标题" })
                return
            }
            if (!this.articleForm.contentMd || this.articleForm.contentMd.trim() === '') {
                this.$toast({ type: "error", message: "请输入文章内容" })
                return
            }

            // 准备提交的数据
            const submitData = {
                id: this.articleForm.id || 0,
                title: this.articleForm.title.trim(),
                summary: this.articleForm.summary || '',
                content: this.articleForm.contentMd || '', // 如果后端需要HTML内容，可以从contentMd转换
                contentMd: this.articleForm.contentMd || '',
                avatar: this.articleForm.avatar || '',
                categoryName: this.articleForm.categoryName || '',
                isCarousel: this.articleForm.isCarousel || 0,
                isOriginal: this.articleForm.isOriginal || 0,
                isPublish: this.articleForm.isPublish || 0,
                isSecret: this.articleForm.isSecret || 0,
                isStick: this.articleForm.isStick || 0,
                keywords: this.articleForm.keywords || '',
                originalUrl: this.articleForm.originalUrl || '',
                remark: this.articleForm.remark || '',
                tags: this.selectedTags, // 后端期望字符串数组，不是对象数组
                userId: this.user.id || 0
            }

            console.log('提交文章数据:', submitData)

            this.submitting = true
            addOrUpdateArticle(submitData).then(res => {
                console.log('保存文章成功:', res)
                this.$toast({ type: "success", message: res.message || "保存成功" })
                // 重置表单
                this.resetArticleForm()
                // 刷新文章列表
                if (this.currentTab === 0) {
                    this.loadMyArticles()
                } else {
                    // 切换到我的文章标签页
                    this.currentTab = 0
                    this.loadMyArticles()
                }
            }).catch(err => {
                console.error('保存文章失败:', err)
                console.error('错误详情:', err.response || err.message)
                // 更详细的错误提示
                let errorMessage = "保存失败"
                if (err.response) {
                    // 优先使用后端返回的错误信息
                    const responseData = err.response.data
                    if (responseData && responseData.message) {
                        errorMessage = responseData.message
                    } else if (responseData && responseData.msg) {
                        errorMessage = responseData.msg
                    } else if (err.message && err.message.includes('JSON parse error')) {
                        // JSON解析错误，可能是数据格式问题
                        errorMessage = "数据格式错误，请检查输入内容"
                    } else {
                        errorMessage = `服务器错误: ${err.response.status}`
                    }
                } else if (err.request) {
                    errorMessage = "网络错误，请检查网络连接或后端服务是否正常"
                } else {
                    errorMessage = err.message || "保存失败"
                }
                
                // 特殊处理演示账号错误
                if (errorMessage.includes('演示账号') || errorMessage.includes('不允许操作')) {
                    this.$toast({ 
                        type: "warning", 
                        message: "演示账号不允许此操作，请使用正式账号登录" 
                    })
                } else {
                    this.$toast({ type: "error", message: errorMessage })
                }
            }).finally(() => {
                this.submitting = false
            })
        },
        // 重置文章表单
        resetArticleForm() {
            this.articleForm = {
                id: 0,
                title: '',
                summary: '',
                content: '',
                contentMd: '',
                avatar: '',
                categoryName: '',
                isCarousel: 0,
                isOriginal: 0,
                isPublish: 0,
                isSecret: 0,
                isStick: 0,
                keywords: '',
                originalUrl: '',
                remark: '',
                tags: [],
                userId: this.user.id || 0
            }
            this.selectedTags = []
            this.tempTag = ''
            this.editingArticleId = null
        },
        // 发布/下架文章（共用方法）
        // isPublish: 0=下架, 1=发布
        pubOrShelfArticle(articleId, articleTitle, isPublish) {
            // 判断文章当前状态：1=已发布，0=已下架
            const currentStatus = isPublish === 1 ? 1 : 0
            const isPublished = currentStatus === 1
            const action = isPublished ? '下架' : '发布'
            
            // 显示确认对话框
            this.confirmTitle = isPublished ? '下架文章' : '发布文章'
            this.confirmMessage = isPublished
                ? `确定要下架文章《${articleTitle}》吗？下架后文章将不再公开显示。`
                : `确定要发布文章《${articleTitle}》吗？发布后文章将公开显示。`
            this.confirmAction = 'publish'
            this.confirmCallback = () => {
                // 计算新的发布状态：下架时设为0，发布时设为1
                const newPublishStatus = isPublished ? 0 : 1
                
                // 构建请求参数，包含 id 和 isPublish
                const requestData = {
                    id: articleId,
                    isPublish: newPublishStatus
                }
                
                console.log('发布/下架请求参数:', requestData)
                
                pubOrShelfArticleApi(requestData).then(res => {
                    console.log('发布/下架接口返回:', res)
                    this.$toast({ type: "success", message: res.message || `${action}成功` })
                    
                    // 立即更新本地文章列表中的 isPublish 字段
                    const articleIndex = this.myArticles.findIndex(article => article.id === articleId)
                    if (articleIndex !== -1) {
                        // 使用 Vue.set 来触发响应式更新
                        this.$set(this.myArticles[articleIndex], 'isPublish', newPublishStatus)
                        console.log('更新后的 isPublish:', this.myArticles[articleIndex].isPublish)
                    }
                    
                    // 刷新文章列表以确保数据同步
                    this.loadMyArticles()
                }).catch(err => {
                    console.error(`${action}文章失败:`, err)
                    let errorMessage = err.message || `${action}失败`
                    if (errorMessage.includes('演示账号') || errorMessage.includes('不允许操作')) {
                        this.$toast({ 
                            type: "warning", 
                            message: "演示账号不允许此操作，请使用正式账号登录" 
                        })
                    } else {
                        this.$toast({ type: "error", message: errorMessage })
                    }
                })
            }
            this.showConfirmDialog = true
        },
        // 删除文章
        deleteArticle(articleId, articleTitle) {
            // 显示确认对话框
            this.confirmTitle = '删除文章'
            this.confirmMessage = `确定要删除文章《${articleTitle}》吗？此操作不可恢复，请谨慎操作！`
            this.confirmAction = 'delete'
            this.confirmCallback = () => {
                deleteArticle(articleId).then(res => {
                    this.$toast({ type: "success", message: res.message || "删除成功" })
                    this.loadMyArticles()
                }).catch(err => {
                    console.error('删除文章失败:', err)
                    let errorMessage = err.message || "删除失败"
                    if (errorMessage.includes('演示账号') || errorMessage.includes('不允许操作')) {
                        this.$toast({ 
                            type: "warning", 
                            message: "演示账号不允许此操作，请使用正式账号登录" 
                        })
                    } else {
                        this.$toast({ type: "error", message: errorMessage })
                    }
                })
            }
            this.showConfirmDialog = true
        },
        // 处理确认对话框的确定按钮
        handleConfirm() {
            this.showConfirmDialog = false
            if (this.confirmCallback && typeof this.confirmCallback === 'function') {
                this.confirmCallback()
            }
            // 清空回调
            this.confirmCallback = null
        },
        // 打开AI推送对话框
        openAIPush() {
            this.showAIModal = true
            this.aiPrompt = ''
            this.aiSystemPrompt = ''
            this.aiTemperature = 0
        },
        // 关闭AI推送对话框
        closeAIModal() {
            this.showAIModal = false
            this.aiPrompt = ''
            this.aiSystemPrompt = ''
            this.aiTemperature = 0
        },
        // AI推送精品内容
        pushAIContent() {
            if (!this.aiPrompt || this.aiPrompt.trim() === '') {
                this.$toast({ type: "error", message: "请输入您想要生成的内容描述" })
                return
            }

            this.aiLoading = true
            // 根据接口要求构建请求参数
            const requestData = {
                message: this.aiPrompt.trim(),  // 用户输入的内容描述
                systemPrompt: this.aiSystemPrompt.trim() || '',  // 系统提示（可选）
                temperature: this.aiTemperature || 0  // 温度参数，控制生成内容的随机性（0-1）
            }

            console.log('AI推送请求参数:', requestData)

            aiChat(requestData).then(res => {
                console.log('AI推送返回:', res)
                // 优先使用返回的 data 字段，而不是 message
                // 处理不同的返回数据结构
                let aiContent = ''
                
                if (res.data) {
                    // 如果 data 是字符串，直接使用
                    if (typeof res.data === 'string') {
                        aiContent = res.data
                    } 
                    // 如果 data 是对象，尝试获取 content 或其他字段
                    else if (typeof res.data === 'object') {
                        aiContent = res.data.content || res.data.data || res.data.text || ''
                    }
                }
                
                // 如果 data 为空，尝试其他可能的字段（作为备选）
                if (!aiContent) {
                    aiContent = res.content || res.text || ''
                }
                
                if (aiContent) {
                    // 先关闭对话框，减少同时进行的DOM操作
                    this.closeAIModal()
                    
                    // 使用 $nextTick 和 requestAnimationFrame 来优化内容更新时机
                    // 这样可以避免 ResizeObserver 错误
                    this.$nextTick(() => {
                        requestAnimationFrame(() => {
                            // 将AI生成的内容追加到现有内容后面，或替换现有内容
                            if (this.articleForm.contentMd && this.articleForm.contentMd.trim() !== '') {
                                // 如果已有内容，追加到后面
                                this.articleForm.contentMd += '\n\n' + aiContent
                            } else {
                                // 如果没有内容，直接使用AI生成的内容
                                this.articleForm.contentMd = aiContent
                            }
                            
                            // 再次使用 $nextTick 确保内容更新完成后再显示提示
                            this.$nextTick(() => {
                                this.$toast({ type: "success", message: "AI内容已添加到编辑器" })
                            })
                        })
                    })
                } else {
                    console.warn('AI返回数据结构:', res)
                    this.$toast({ type: "warning", message: "AI未返回内容，请检查返回数据格式" })
                    this.closeAIModal()
                }
            }).catch(err => {
                console.error('AI推送失败:', err)
                let errorMessage = err.message || "AI推送失败"
                if (errorMessage.includes('演示账号') || errorMessage.includes('不允许操作')) {
                    this.$toast({ 
                        type: "warning", 
                        message: "演示账号不允许此操作，请使用正式账号登录" 
                    })
                } else {
                    this.$toast({ type: "error", message: errorMessage })
                }
            }).finally(() => {
                this.aiLoading = false
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.user-main {
    min-height: calc(100vh - 167px);
    padding: 24px;
    background-color: var(--background-color);

        .content {
        max-width: 1000px; // 从1200px调整为1000px，使内容更紧凑
        margin: 80px auto 0;
            width: 100%;
    }

    // 用户信息卡片
    .user-card {
        margin-bottom: 24px;
        padding: 32px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 16px;
                color: #fff;

        .user-header {
            display: flex;
            align-items: center;
            gap: 20px;

            .avatar-img {
                width: 90px; // 从100px调整为90px
                height: 90px;
                border-radius: 50%;
                border: 3px solid rgba(255, 255, 255, 0.3);
                object-fit: cover;
            }

            .user-info {
                flex: 1;

                .nickname {
                    font-size: 24px; // 从28px调整为24px
                    font-weight: 700;
                    margin: 0 0 8px 0;
                    color: #fff;
                }

                .intro {
                    font-size: 16px;
                    margin: 10px 0;
                    opacity: 0.9;
                    line-height: 1.6;
                }

                .user-meta {
                    display: flex;
                    gap: 20px;
                    margin-top: 15px;
                    flex-wrap: wrap;

                    .meta-item {
                        display: flex;
                        align-items: center;
                        gap: 5px;
                        font-size: 14px;
                        opacity: 0.9;

                        i {
                            font-size: 16px;
                        }

                        a {
                            color: #fff;
                        text-decoration: none;

                            &:hover {
                                text-decoration: underline;
                            }
                        }
                    }
                }
            }
        }
    }

    // 标签页卡片
    .tabs-card {
        border-radius: 12px;
        overflow: hidden;

        .tabs-header {
            display: flex;
            border-bottom: 2px solid var(--border-line);
            background-color: var(--background-color);

            .tab-btn {
                flex: 1;
                padding: 16px 20px; // 从20px调整为16px 20px
                background: none;
                border: none;
                border-bottom: 3px solid transparent;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                font-size: 15px; // 从16px调整为15px
                color: var(--text-color);
                transition: all 0.3s;
                position: relative;

                i {
                    font-size: 18px;
                }

                .count-badge {
                    background-color: var(--theme-color);
                    color: #fff;
                    padding: 2px 8px;
                    border-radius: 12px;
                    font-size: 12px;
                    min-width: 20px;
                    text-align: center;
                }

                &:hover {
                    background-color: var(--hover-background, rgba(0, 0, 0, 0.02));
                        color: var(--theme-color);
                }

                &.active {
                    color: var(--theme-color);
                    border-bottom-color: var(--theme-color);
                    font-weight: 600;
                }
            }
        }

        .tab-content {
            padding: 24px; // 从30px调整为24px
            min-height: 400px;
        }
    }

    // 文章列表
    .articles-container {
        .article-list {
            list-style: none;
            padding: 0;
            margin: 0;
            display: grid;
            gap: 20px; // 从24px调整为20px

            .article-item {
                display: flex;
                gap: 20px; // 从24px调整为20px
                padding: 20px; // 从24px调整为20px
                background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
                background-color: var(--card-background, #fff);
                border-radius: 12px; // 从16px调整为12px
                border: 1px solid var(--border-line);
                cursor: pointer;
                transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
                position: relative;
                overflow: hidden;

                // 添加左侧装饰条
                &::before {
                    content: '';
                    position: absolute;
                    left: 0;
                    top: 0;
                    bottom: 0;
                    width: 4px;
                    background: linear-gradient(180deg, var(--theme-color) 0%, rgba(102, 126, 234, 0.5) 100%);
                    transform: scaleY(0);
                    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
                }

                &:hover {
                    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
                    transform: translateY(-4px);
                    border-color: var(--theme-color);
                    background-color: var(--hover-background, rgba(102, 126, 234, 0.02));

                    &::before {
                        transform: scaleY(1);
                    }

                    .article-thumbnail {
                        img {
                            transform: scale(1.08);
                        }
                    }

                    .article-title {
                        color: var(--theme-color);
                    }
                }

                .article-thumbnail {
                    flex-shrink: 0;
                    width: 220px; // 从240px调整为220px
                    height: 150px; // 从160px调整为150px
                    position: relative;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);

                    img {
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                        transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
                    }

                    .badge {
                        position: absolute;
                        top: 12px;
                        left: 12px;
                        padding: 6px 12px;
                        border-radius: 20px;
                        font-size: 12px;
                        color: #fff;
                        font-weight: 600;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
                        backdrop-filter: blur(10px);
                        z-index: 2;

                        &.top {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        }

                        &.original {
                            background: linear-gradient(135deg, #26A69A 0%, #00897B 100%);
                        }

                        &.draft {
                            background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
                        }

                        // 多个标签时，第二个标签位置调整
                        &:nth-child(2) {
                            left: auto;
                            right: 12px;
                        }

                        // 三个标签时，第三个标签位置调整
                        &:nth-child(3) {
                            top: 50px;
                            left: 12px;
                        }
                    }

                    // 添加渐变遮罩
                    &::after {
                        content: '';
                        position: absolute;
                        bottom: 0;
                        left: 0;
                        right: 0;
                        height: 40%;
                        background: linear-gradient(to top, rgba(0, 0, 0, 0.3), transparent);
                        opacity: 0;
                        transition: opacity 0.3s;
                    }

                    &:hover::after {
                        opacity: 1;
                    }
                }

                .article-info {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    min-width: 0;

                    .article-title {
                        font-size: 20px; // 从22px调整为20px
                        font-weight: 700;
                        color: var(--article-color);
                        margin: 0 0 10px 0;
                        line-height: 1.5;
                        display: -webkit-box;
                        -webkit-line-clamp: 2;
                        -webkit-box-orient: vertical;
                        overflow: hidden;
                        transition: color 0.3s;
                        letter-spacing: -0.3px;
                    }

                    .article-summary {
                        font-size: 14px; // 从15px调整为14px
                    color: var(--text-color);
                        line-height: 1.6;
                        margin: 0 0 14px 0;
                        display: -webkit-box;
                        -webkit-line-clamp: 2;
                        -webkit-box-orient: vertical;
                        overflow: hidden;
                        opacity: 0.75;
                    }

                    .article-meta {
                        display: flex;
                        gap: 24px;
                        align-items: center;
                        font-size: 13px;
                        color: var(--text-color);
                        margin-bottom: 12px;
                        flex-wrap: wrap;
                        padding-top: 12px;
                        border-top: 1px solid var(--border-line);

                        .meta-item {
                            display: flex;
                            align-items: center;
                            gap: 6px;
                            padding: 4px 8px;
                            border-radius: 6px;
                            transition: all 0.2s;
                            opacity: 0.8;

                            &:hover {
                                background-color: var(--hover-background, rgba(0, 0, 0, 0.03));
                                opacity: 1;
                            }

                            i {
                                font-size: 14px;
                                color: var(--theme-color);
                            }

                            &.date {
                                margin-left: auto;
                                opacity: 0.6;
                                font-weight: 500;
                            }
                        }
                    }

                    .article-tags {
                        display: flex;
                        gap: 8px;
                        flex-wrap: wrap;

                        .tag {
                            display: inline-flex;
                            align-items: center;
                            gap: 4px;
                            padding: 6px 12px;
                            background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
                            background-color: var(--tag-background, rgba(102, 126, 234, 0.08));
                            border-radius: 16px;
                            font-size: 12px;
                            color: var(--theme-color);
                            font-weight: 500;
                            transition: all 0.2s;
                            border: 1px solid rgba(102, 126, 234, 0.2);

                            &:hover {
                                background-color: var(--theme-color);
                                color: #fff;
                                transform: translateY(-2px);
                                box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
                            }

                            i {
                                font-size: 12px;
                            }
                        }
                    }
                }
            }
        }

        .pagination-wrapper {
            margin-top: 40px;
        display: flex;
        justify-content: center;
            padding: 20px 0;
        }
    }

    // 加载状态
    .loading-state {
            text-align: center;
        padding: 80px 20px;
        color: var(--text-color);

        .loading-spinner {
            width: 48px;
            height: 48px;
            margin: 0 auto 20px;
            border: 4px solid var(--border-line);
            border-top-color: var(--theme-color);
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        p {
            font-size: 16px;
            opacity: 0.7;
        }
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    // 空状态
    .empty-state {
        text-align: center;
        padding: 80px 20px;
        color: var(--text-color);

        svg {
            width: 120px;
            height: 120px;
            opacity: 0.4;
            margin-bottom: 24px;
        }

        p {
            font-size: 16px;
            opacity: 0.6;
            font-weight: 500;
        }
    }

    // 编辑表单
    .edit-form {
        max-width: 600px;
        margin: 0 auto;

        .form-item {
            margin-bottom: 25px;

            label {
                display: block;
                margin-bottom: 8px;
                font-weight: 600;
                color: var(--text-color);
                font-size: 14px;
            }

            input,
            textarea {
                width: 100%;
                padding: 12px;
                border: 1px solid var(--border-line);
                border-radius: 6px;
                font-size: 14px;
                color: var(--text-color);
                background-color: var(--background-color);
                transition: all 0.3s;
                box-sizing: border-box;

                &:focus {
                    outline: none;
                    border-color: var(--theme-color);
                    box-shadow: 0 0 0 3px rgba(0, 167, 235, 0.1);
                }

                &:disabled {
                    background-color: var(--disabled-background, #f5f5f5);
                    cursor: not-allowed;
                }
            }

            textarea {
                resize: vertical;
                font-family: inherit;
            }

            .form-tip {
                display: block;
                margin-top: 5px;
                font-size: 12px;
                color: var(--text-color);
                opacity: 0.6;
            }
        }

        .form-actions {
            margin-top: 30px;
            text-align: center;

            .submit-btn {
                padding: 12px 40px;
                background-color: var(--theme-color);
                color: #fff;
                border: none;
                border-radius: 6px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s;

                &:hover:not(:disabled) {
                    opacity: 0.9;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(0, 167, 235, 0.3);
                }

                &:disabled {
                    opacity: 0.6;
                    cursor: not-allowed;
                }
            }
        }
    }

    // 文章编辑器表单
    .article-editor {
        .editor-form {
            max-width: 100%;
            display: flex;
            gap: 24px;
            align-items: flex-start;
            margin-bottom: 24px;

            // 上方基本信息区
            .editor-header {
                flex: 1;
                min-width: 0;

                .form-row {
                    display: flex;
                    gap: 20px;
                    margin-bottom: 20px;
                    flex-wrap: wrap;
                    align-items: flex-start;
                }
            }

            // 右侧选项区
            .editor-sidebar {
                width: 280px;
                flex-shrink: 0;
                background-color: var(--card-background, #fff);
                border: 1px solid var(--border-line);
                border-radius: 8px;
                padding: 20px;
                position: sticky;
                top: 100px;
                max-height: calc(100vh - 200px);
                overflow-y: auto;
                align-self: flex-start;

                .sidebar-section {
                    margin-bottom: 24px;
                    padding-bottom: 20px;
                    border-bottom: 1px solid var(--border-line);

                    &:last-child {
                        margin-bottom: 0;
                        padding-bottom: 0;
                        border-bottom: none;
                    }

                    .section-title {
                        font-size: 16px;
                        font-weight: 600;
                color: var(--text-color);
                        margin: 0 0 16px 0;
                        padding-bottom: 8px;
                        border-bottom: 2px solid var(--theme-color);
                    }

                    .form-item {
                        margin-bottom: 16px;

                        &:last-child {
                            margin-bottom: 0;
                        }

                        label {
                            display: block;
                            margin-bottom: 8px;
                            font-weight: 500;
                            color: var(--text-color);
                            font-size: 14px;
                        }
                    }
                }
            }

                .form-item {
                    flex: 1;
                    min-width: 400px;

                    &.full-width {
                        flex: 1 1 100%;
                    }

                    &.checkbox-item {
                        flex: 0 0 auto;
                        min-width: auto;
                        width: 100%;
                    }

                    label {
                        display: block;
                        margin-bottom: 8px;
                        font-weight: 600;
                        color: var(--text-color);
                        font-size: 14px;

                        .required {
                            color: #f56c6c;
                        }

                        &.checkbox-label {
                            display: flex;
                            align-items: center;
                            margin-bottom: 0;
                            cursor: pointer;
                            font-weight: normal;
                            padding: 8px;
                            border-radius: 4px;
                            transition: background-color 0.2s;

                            &:hover {
                                background-color: var(--hover-background, rgba(0, 0, 0, 0.02));
                            }

                            input[type="checkbox"] {
                                margin-right: 8px;
                                cursor: pointer;
                                width: auto;
                                height: auto;
                                padding: 0;
                                flex-shrink: 0;
                            }

                            span {
                                user-select: none;
                                flex: 1;
                            }
                        }
                    }

                    input[type="checkbox"] {
                        margin-right: 6px;
                        cursor: pointer;
                    }

                    input,
                    textarea,
                    select {
                        width: 100%;
                        padding: 12px;
                    border: 1px solid var(--border-line);
                        border-radius: 6px;
                        font-size: 14px;
                        color: var(--text-color);
                        background-color: var(--background-color);
                        transition: all 0.3s;
                        box-sizing: border-box;

                        &:focus {
                            outline: none;
                            border-color: var(--theme-color);
                            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                        }
                    }

                    .form-select {
                        cursor: pointer;
                    }

                    .tags-selector {
                        .selected-tags {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 8px;
                            margin-bottom: 8px;

                            .tag-chip {
                                display: inline-flex;
                                align-items: center;
                                gap: 6px;
                                padding: 6px 12px;
                                background-color: var(--theme-color);
                                color: #fff;
                                border-radius: 16px;
                                font-size: 12px;
                                cursor: pointer;
                                transition: all 0.2s;

                                &:hover {
                                    opacity: 0.8;
                                    transform: scale(1.05);
                                }

                                i {
                                    font-size: 12px;
                                }
                            }
                        }
                    }

                    .markdown-editor-wrapper {
                        border: 1px solid var(--border-line);
                        border-radius: 6px;
                        overflow: hidden;
                    }
                }
            }

        // 下方中央 Markdown 编辑区
        .editor-content {
                    width: 100%;
            margin-bottom: 24px;

            .form-item {
                width: 100%;

                // Markdown编辑器头部（包含AI按钮）
                .editor-header-row {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 12px;

                    label {
                        margin-bottom: 0;
                    }

                    .ai-push-btn {
                        padding: 8px 16px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: #fff;
                        border: none;
                        border-radius: 6px;
                        font-size: 14px;
                        font-weight: 600;
                        cursor: pointer;
                        display: flex;
                        align-items: center;
                        gap: 6px;
                        transition: all 0.3s;

                        &:hover:not(:disabled) {
                            opacity: 0.9;
                            transform: translateY(-2px);
                            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
                        }

                        &:disabled {
                            opacity: 0.6;
                            cursor: not-allowed;
                        }

                        i {
                            font-size: 16px;
                        }
                    }
                }

                label {
                    display: block;
                    margin-bottom: 12px;
                    font-weight: 600;
                    color: var(--text-color);
                    font-size: 14px;

                    .required {
                        color: #f56c6c;
                    }
                }

                .markdown-editor-wrapper {
                    border: 1px solid var(--border-line);
                    border-radius: 8px;
                    overflow: hidden;
                    background-color: var(--card-background, #fff);
                }
            }
        }

        // AI推送对话框样式 - 使用深度选择器覆盖 Vuetify 组件样式
        ::v-deep .ai-dialog-wrapper {
            .v-dialog {
                border-radius: 8px !important;
                overflow: hidden !important;
            }
        }

        ::v-deep .ai-card-wrapper {
            border-radius: 8px !important;
            overflow: hidden !important;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;

            .v-card__title {
                padding: 0 !important;
                margin: 0 !important;
            }

            .v-card__text {
                padding: 0 !important;
            }

            .v-card__actions {
                padding: 0 !important;
            }
        }

        .ai-modal-header {
            display: flex !important;
            justify-content: space-between !important;
            align-items: center !important;
            padding: 24px 28px !important;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
            color: #fff !important;
            border-radius: 8px 8px 0 0 !important;
            width: 100% !important;
            margin: 0 !important;

            span {
                font-size: 18px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 8px;

                &::before {
                    content: '🤖';
                    font-size: 20px;
                }
            }

            .close-btn {
                background: rgba(255, 255, 255, 0.2);
                border: none;
                cursor: pointer;
                padding: 6px 10px;
                border-radius: 50%;
                color: #fff;
                opacity: 0.9;
                transition: all 0.2s;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 32px;
                height: 32px;

                &:hover {
                    opacity: 1;
                    background: rgba(255, 255, 255, 0.3);
                    transform: rotate(90deg);
                }

                i {
                    font-size: 16px;
                }
            }
        }

        .ai-modal-content {
            padding: 28px !important;
            background-color: var(--card-background, #fff) !important;
            max-height: 70vh !important;
            overflow-y: auto !important;

            .form-item {
                margin-bottom: 24px;
                background-color: var(--background-color, #f8f9fa);
                padding: 20px;
                border-radius: 8px;
                border: 1px solid var(--border-line, #e0e0e0);
                transition: all 0.3s;

                &:hover {
                    border-color: var(--theme-color);
                    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
                }

                    &:last-child {
                    margin-bottom: 0;
                }

                label {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    margin-bottom: 10px;
                    font-weight: 600;
                    color: var(--text-color);
                    font-size: 14px;

                    .required {
                        color: #f56c6c;
                        font-weight: 700;
                    }

                    .optional {
                        color: #999;
                        font-size: 12px;
                        font-weight: normal;
                        background-color: #f0f0f0;
                        padding: 2px 8px;
                        border-radius: 12px;
                    }
                }

                .form-tip {
                    display: flex;
                    align-items: flex-start;
                    gap: 6px;
                    margin-bottom: 12px;
                    padding: 10px 12px;
                    background-color: rgba(102, 126, 234, 0.05);
                    border-left: 3px solid var(--theme-color);
                    border-radius: 4px;
                    font-size: 12px;
                color: var(--text-color);
                    opacity: 0.8;
                    line-height: 1.6;

                    &::before {
                        content: '💡';
                        font-size: 14px;
                        flex-shrink: 0;
                    }
                }

                textarea,
                input[type="number"] {
                    width: 100%;
                    padding: 14px 16px;
                    border: 2px solid var(--border-line, #e0e0e0);
                    border-radius: 8px;
                    font-size: 14px;
                    color: var(--text-color);
                    background-color: #fff;
                    transition: all 0.3s;
                    box-sizing: border-box;
                    font-family: inherit;

                    &:focus {
                        outline: none;
                        border-color: var(--theme-color);
                        box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
                        background-color: #fff;
                    }

                    &:disabled {
                        background-color: var(--disabled-background, #f5f5f5);
                        cursor: not-allowed;
                        opacity: 0.7;
                    }

                    &::placeholder {
                        color: #bbb;
                    }
                }

                textarea {
                    resize: vertical;
                    min-height: 100px;
                    line-height: 1.6;
                }

                input[type="number"] {
                    -moz-appearance: textfield;
                    max-width: 150px;
                    
                    &::-webkit-outer-spin-button,
                    &::-webkit-inner-spin-button {
                        -webkit-appearance: none;
                        margin: 0;
                    }
                }
            }
        }

        .ai-modal-actions {
            padding: 20px 28px !important;
            border-top: 1px solid var(--border-line, #e0e0e0) !important;
            background-color: var(--background-color, #f8f9fa) !important;
            display: flex !important;
            justify-content: flex-end !important;
            gap: 12px !important;
            border-radius: 0 0 8px 8px !important;
            margin: 0 !important;

            .cancel-btn,
            .submit-btn {
                padding: 12px 28px;
                border: none;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s;
                min-width: 100px;

                &:disabled {
                    opacity: 0.6;
                    cursor: not-allowed;
                }
            }

            .cancel-btn {
                background-color: #ffffff;
                    color: var(--text-color);
                border: 2px solid var(--border-line, #e0e0e0);

                &:hover:not(:disabled) {
                    background-color: var(--hover-background, rgba(0, 0, 0, 0.02));
                    border-color: #ccc;
                    transform: translateY(-1px);
                }
            }

            .submit-btn {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color:#ff4d4f;
                box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);

                &:hover:not(:disabled) {
                    opacity: 0.95;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 16px rgba(239, 5, 5, 0.979);
                }

                &:active:not(:disabled) {
                    transform: translateY(0);
                }
            }
        }

        // 底部操作按钮
        .form-actions {
            margin-top: 24px;
            padding-top: 24px;
            border-top: 1px solid var(--border-line);
            display: flex;
            gap: 12px;
            justify-content: center;

                .submit-btn,
                .cancel-btn {
                    padding: 12px 40px;
                    border: none;
                    border-radius: 6px;
                    font-size: 16px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s;

                    &:disabled {
                        opacity: 0.6;
                        cursor: not-allowed;
                    }
                }

                .submit-btn {
                    background-color: var(--theme-color);
                    color: #fff;

                    &:hover:not(:disabled) {
                        opacity: 0.9;
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
                    }
                }

                .cancel-btn {
                    background-color: var(--background-color);
                        color: var(--text-color);
                    border: 1px solid var(--border-line);

                    &:hover:not(:disabled) {
                        background-color: var(--hover-background, rgba(0, 0, 0, 0.02));
                    }
                }
            }
        }
    }

    // 文章列表中的操作按钮
    .article-header {
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        gap: 12px;
        margin-bottom: 10px;

        .article-title {
            flex: 1;
            margin: 0;
        }

        .article-actions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
            align-items: center;

            .action-btn {
                padding: 6px 12px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-size: 13px;
                transition: all 0.2s;
                display: flex;
                align-items: center;
                gap: 4px;
                flex-shrink: 0;
                white-space: nowrap;

                &:hover {
                    opacity: 0.9;
                    transform: translateY(-1px);
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }

                i {
                    font-size: 13px;
                }

                &.edit-btn {
                    background-color: var(--theme-color);
                    color: #fff;
                }

                &.publish-btn {
                    background-color: #52c41a;
                    color: #fff;
                }

                &.unpublish-btn {
                    background-color: #faad14;
                    color: #fff;
                }

                &.delete-btn {
                    background-color: #ff4d4f;
                    color: #fff;
                }
            }
        }
    }
// }

// 移动端适配
@media screen and (max-width: 768px) {
    .user-main {
        padding: 10px;

        .content {
            margin-top: 80px;
        }

        // 文章编辑器移动端适配
        .article-editor {
            .editor-form {
                flex-direction: column;

                .editor-header {
                    width: 100%;
                }

                .editor-sidebar {
                    width: 100%;
                    position: static;
                    max-height: none;
                    margin-top: 20px;
                }
            }

            .editor-content {
                width: 100%;
            }
        }

        .user-card {
            padding: 20px;

            .user-header {
                flex-direction: column;
                text-align: center;

                .avatar-img {
                    width: 80px;
                    height: 80px;
                }
            }
        }

        .tabs-card {
            .tabs-header {
                flex-direction: column;

                .tab-btn {
                    border-bottom: 1px solid var(--border-line);
                    border-left: 3px solid transparent;

                    &.active {
                        border-left-color: var(--theme-color);
                        border-bottom-color: var(--border-line);
                    }
                }
            }
        }

        .articles-container {
            .article-list {
                gap: 16px;

                .article-item {
                    flex-direction: column;
                    padding: 16px;
                    gap: 16px;

                    .article-thumbnail {
                        width: 100%;
                        height: 180px;
                    }

                    .article-info {
                        .article-title {
                            font-size: 18px;
                        }

                        .article-summary {
                            font-size: 14px;
                        }

                        .article-meta {
                            gap: 12px;
                            font-size: 12px;

                            .meta-item {
                                &.date {
                                    margin-left: 0;
                                    width: 100%;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>

<!-- AI对话框全局样式，确保能够覆盖Vuetify默认样式 -->
<style lang="scss">
.ai-dialog-wrapper {
    .v-dialog {
        border-radius: 8px !important;
        overflow: hidden !important;
    }
}

.ai-card-wrapper {
    border-radius: 8px !important;
    overflow: hidden !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;

    .v-card__title {
        padding: 0 !important;
        margin: 0 !important;
    }

    .v-card__text {
        padding: 0 !important;
    }

    .v-card__actions {
        padding: 0 !important;
    }
}

.ai-modal-header {
    display: flex !important;
    justify-content: space-between !important;
    align-items: center !important;
    padding: 24px 28px !important;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
    color: #fff !important;
    border-radius: 8px 8px 0 0 !important;
    width: 100% !important;
    margin: 0 !important;

    span {
        font-size: 18px !important;
        font-weight: 600 !important;
        display: flex !important;
        align-items: center !important;
        gap: 8px !important;

        &::before {
            content: '🤖' !important;
            font-size: 20px !important;
        }
    }

    .close-btn {
        background: rgba(255, 255, 255, 0.2) !important;
        border: none !important;
        cursor: pointer !important;
        padding: 6px 10px !important;
        border-radius: 50% !important;
        color: #fff !important;
        opacity: 0.9 !important;
        transition: all 0.2s !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        width: 32px !important;
        height: 32px !important;

        &:hover {
            opacity: 1 !important;
            background: rgba(255, 255, 255, 0.3) !important;
            transform: rotate(90deg) !important;
        }

        i {
            font-size: 16px !important;
        }
    }
}

.ai-modal-content {
    padding: 28px !important;
    background-color: #fff !important;
    max-height: 70vh !important;
    overflow-y: auto !important;

    .form-item {
        margin-bottom: 24px !important;
        background-color: #f8f9fa !important;
        padding: 20px !important;
        border-radius: 8px !important;
        border: 1px solid #e0e0e0 !important;
        transition: all 0.3s !important;

        &:hover {
            border-color: #667eea !important;
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1) !important;
        }

        &:last-child {
            margin-bottom: 0 !important;
        }

        label {
            display: flex !important;
            align-items: center !important;
            gap: 8px !important;
            margin-bottom: 10px !important;
            font-weight: 600 !important;
            color: #333 !important;
            font-size: 14px !important;

            .required {
                color: #f56c6c !important;
                font-weight: 700 !important;
            }

            .optional {
                color: #999 !important;
                font-size: 12px !important;
                font-weight: normal !important;
                background-color: #f0f0f0 !important;
                padding: 2px 8px !important;
                border-radius: 12px !important;
            }
        }

        .form-tip {
            display: flex !important;
            align-items: flex-start !important;
            gap: 6px !important;
            margin-bottom: 12px !important;
            padding: 10px 12px !important;
            background-color: rgba(102, 126, 234, 0.05) !important;
            border-left: 3px solid #667eea !important;
            border-radius: 4px !important;
            font-size: 12px !important;
            color: #666 !important;
            opacity: 0.8 !important;
            line-height: 1.6 !important;

            &::before {
                content: '💡' !important;
                font-size: 14px !important;
                flex-shrink: 0 !important;
            }
        }

        textarea,
        input[type="number"] {
            width: 100% !important;
            padding: 14px 16px !important;
            border: 2px solid #e0e0e0 !important;
            border-radius: 8px !important;
            font-size: 14px !important;
            color: #333 !important;
            background-color: #fff !important;
            transition: all 0.3s !important;
            box-sizing: border-box !important;
            font-family: inherit !important;

            &:focus {
                outline: none !important;
                border-color: #667eea !important;
                box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1) !important;
                background-color: #fff !important;
            }

            &:disabled {
                background-color: #f5f5f5 !important;
                cursor: not-allowed !important;
                opacity: 0.7 !important;
            }

            &::placeholder {
                color: #bbb !important;
            }
        }

        textarea {
            resize: vertical !important;
            min-height: 100px !important;
            line-height: 1.6 !important;
        }

        input[type="number"] {
            -moz-appearance: textfield !important;
            max-width: 150px !important;
            
            &::-webkit-outer-spin-button,
            &::-webkit-inner-spin-button {
                -webkit-appearance: none !important;
                margin: 0 !important;
            }
        }
    }
}

.ai-modal-actions {
    padding: 20px 28px !important;
    border-top: 1px solid #e0e0e0 !important;
    background-color: #f8f9fa !important;
    display: flex !important;
    justify-content: flex-end !important;
    gap: 12px !important;
    border-radius: 0 0 8px 8px !important;
    margin: 0 !important;

    .cancel-btn,
    .submit-btn {
        padding: 12px 28px !important;
        border: none !important;
        border-radius: 8px !important;
        font-size: 14px !important;
        font-weight: 600 !important;
        cursor: pointer !important;
        transition: all 0.3s !important;
        min-width: 100px !important;

        &:disabled {
            opacity: 0.6 !important;
            cursor: not-allowed !important;
        }
    }

    .cancel-btn {
        background-color: #fff !important;
        color: #333 !important;
        border: 2px solid #e0e0e0 !important;

        &:hover:not(:disabled) {
            background-color: rgba(0, 0, 0, 0.02) !important;
            border-color: #ccc !important;
            transform: translateY(-1px) !important;
        }
    }

    .submit-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
        // background-color: #fff !important;
        color:#ffffff !important;
        box-shadow: 0 2px 8px rgb(234, 214, 102) !important;

        &:hover:not(:disabled) {
            opacity: 0.95 !important;
            transform: translateY(-2px) !important;
            box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4) !important;
        }

        &:active:not(:disabled) {
            transform: translateY(0) !important;
        }
    }
}

// 确认对话框样式
::v-deep .confirm-dialog-card {
    border-radius: 12px !important;
    overflow: hidden !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;

    .confirm-dialog-title {
        padding: 24px 28px 16px !important;
        font-size: 18px !important;
        font-weight: 600 !important;
        color: #333 !important;
        display: flex !important;
        align-items: center !important;
        border-bottom: 1px solid #f0f0f0 !important;
        background-color: #fff !important;
    }

    .confirm-dialog-content {
        padding: 24px 28px !important;
        font-size: 15px !important;
        line-height: 1.6 !important;
        color: #666 !important;
        background-color: #fff !important;
    }

    .confirm-dialog-actions {
        padding: 16px 28px 24px !important;
        background-color: #f8f9fa !important;
        display: flex !important;
        justify-content: flex-end !important;
        gap: 12px !important;
        border-top: 1px solid #e0e0e0 !important;

        .cancel-btn {
            padding: 10px 24px !important;
            border: 1px solid #d0d0d0 !important;
            border-radius: 6px !important;
            font-size: 14px !important;
            color: #666 !important;
            background-color:#E4080A !important;
            cursor: pointer !important;
            transition: all 0.3s !important;

            &:hover {
                background-color: #f5f5f5 !important;
                border-color: #b0b0b0 !important;
            }
        }

        // 使用深度选择器覆盖 Vuetify 按钮样式
        ::v-deep .confirm-btn {
            padding: 10px 24px !important;
            border: none !important;
            border-radius: 6px !important;
            font-size: 14px !important;
            font-weight: 600 !important;
            cursor: pointer !important;
            transition: all 0.3s !important;
            box-shadow: 0 2px 8px rgba(228, 8, 10, 0.3) !important;
            background-color: #E4080A !important;
            background: #E4080A !important;
            
            // 覆盖 Vuetify 按钮内部样式
            &::before {
                background-color: #E4080A !important;
            }
            
            // 覆盖文字颜色
            .v-btn__content {
                color: #ffffff !important;
            }
            
            // 覆盖所有可能的文字元素
            span, i {
                color: #ffffff !important;
            }
            
            // 覆盖 Vuetify 的主题颜色类
            &.v-btn--primary,
            &.v-btn--error,
            &.v-btn--contained {
                background-color: #E4080A !important;
                background: #E4080A !important;
                
                .v-btn__content {
                    color: #ffffff !important;
                }
                
                span {
                    color: #ffffff !important;
                }
            }

            &:hover {
                transform: translateY(-1px) !important;
                background-color: #c40709 !important;
                background: #c40709 !important;
                box-shadow: 0 4px 12px rgba(228, 8, 10, 0.4) !important;
                
                &::before {
                    background-color: #c40709 !important;
                }
                
                .v-btn__content {
                    color: #ffffff !important;
                }
                
                span, i {
                    color: #ffffff !important;
                }
            }
        }
    }
}

// 全局覆盖确认按钮样式（确保优先级最高）
::v-deep .confirm-dialog-card .confirm-dialog-actions .confirm-btn,
::v-deep .confirm-dialog-card .confirm-dialog-actions .v-btn.confirm-btn {
    background-color: #E4080A !important;
    background: #E4080A !important;
    
    .v-btn__content {
        color:#c40709 !important;
    }
    
    span {
        color: #ffffff !important;
    }
    
    &::before {
        background-color: #E4080A !important;
    }
    
    &.v-btn--primary,
    &.v-btn--error,
    &.v-btn--contained {
        background-color: #E4080A !important;
        background: #E4080A !important;
        
        .v-btn__content {
            color: #ffffff !important;
        }
        
        span {
            color: #ffffff !important;
        }
    }
    
    &:hover {
        background-color: #c40709 !important;
        background: #c40709 !important;
        
        &::before {
            background-color: #c40709 !important;
        }
        
        .v-btn__content {
            color: #ffffff !important;
        }
        
        span {
            color: #ffffff !important;
        }
    }
}
</style>