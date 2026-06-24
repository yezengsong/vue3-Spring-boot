<template>
  <div class="profile-page">
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <el-icon><Reading /></el-icon>
          <span>网络小说平台</span>
        </div>
        <el-menu mode="horizontal" :ellipsis="false" router>
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/category">分类</el-menu-item>
          <el-menu-item index="/user/profile" v-if="userStore.token">我的书架</el-menu-item>
          <el-menu-item index="/admin" v-if="userStore.token && userStore.user.role === 'ADMIN'">后台管理</el-menu-item>
        </el-menu>
      </div>
    </el-header>

    <el-main class="main-content">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <div class="user-info">
              <el-avatar :size="80" :src="userStore.user.avatar" />
              <h3>{{ userStore.user.username }}</h3>
              <p>{{ userStore.user.email || '未设置邮箱' }}</p>
            </div>
            <el-menu>
              <el-menu-item index="1" @click="switchTab('bookmarks')">
                <el-icon><Collection /></el-icon>
                我的书架
              </el-menu-item>
              <el-menu-item index="2" @click="switchTab('history')">
                <el-icon><Clock /></el-icon>
                阅读历史
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card v-if="activeTab === 'bookmarks'">
            <template #header>我的书架</template>
            <div class="bookmark-list" v-loading="loading">
              <el-row :gutter="16">
                <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="bookmark in bookmarks" :key="bookmark.bookmarkId">
                  <el-card shadow="hover" class="bookmark-card" @click="goToNovel(bookmark.novelId)">
                    <div class="bookmark-cover">
                      <el-image :src="bookmark.cover || '/image/default-cover.png'" fit="cover" />
                    </div>
                    <div class="bookmark-info">
                      <h3 class="bookmark-title">{{ bookmark.title }}</h3>
                      <p class="bookmark-author">{{ bookmark.author }}</p>
                      <div class="bookmark-tags">
                        <el-tag size="small">{{ bookmark.categoryName }}</el-tag>
                        <el-tag size="small" :type="bookmark.status === 2 ? 'success' : 'warning'">
                          {{ bookmark.status === 2 ? '完结' : '连载' }}
                        </el-tag>
                      </div>
                      <p class="bookmark-desc">{{ bookmark.description }}</p>
                      <div class="bookmark-footer">
                        <span class="bookmark-wordcount">{{ formatWordCount(bookmark.wordCount) }}</span>
                        <span class="bookmark-time">{{ formatTime(bookmark.createTime) }}</span>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              <el-empty v-if="!loading && bookmarks.length === 0" description="还没有收藏任何小说" />
            </div>
          </el-card>

          <el-card v-if="activeTab === 'history'">
            <template #header>阅读历史</template>
            <div class="history-list" v-loading="loading">
              <el-row :gutter="16">
                <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in historyList" :key="item.historyId">
                  <el-card shadow="hover" class="history-card" @click="goToNovel(item.novelId)">
                    <div class="history-cover">
                      <el-image :src="item.novelCover || '/image/default-cover.png'" fit="cover" />
                    </div>
                    <div class="history-info">
                      <h3 class="history-title">{{ item.novelTitle }}</h3>
                      <p class="history-author">{{ item.author }}</p>
                      <div class="history-tags">
                        <el-tag size="small">{{ item.categoryName }}</el-tag>
                      </div>
                      <p class="history-chapter">阅读至：{{ item.chapterTitle }}</p>
                      <div class="history-footer">
                        <span class="history-time">{{ formatTime(item.readTime) }}</span>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              <el-empty v-if="!loading && historyList.length === 0" description="暂无阅读历史" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBookmarkDetails } from '@/api/bookmark'
import { getReadHistory } from '@/api/history'
import { Reading, Collection, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('bookmarks')
const loading = ref(false)
const bookmarks = ref([])
const historyList = ref([])

async function loadBookmarks() {
  loading.value = true
  try {
    const res = await getBookmarkDetails()
    bookmarks.value = res.data
  } catch (error) {
    console.error('加载书架失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadHistory() {
  loading.value = true
  try {
    const res = await getReadHistory()
    historyList.value = res.data
  } catch (error) {
    console.error('加载阅读历史失败:', error)
  } finally {
    loading.value = false
  }
}

function switchTab(tab) {
  activeTab.value = tab
  if (tab === 'history' && historyList.value.length === 0) {
    loadHistory()
  }
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

function formatWordCount(count) {
  if (!count) return '0字'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万字'
  }
  return count + '字'
}

function goToNovel(novelId) {
  router.push(`/novel/${novelId}`)
}

onMounted(() => {
  loadBookmarks()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  margin-right: 40px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
}

.user-info {
  text-align: center;
  padding: 20px 0;
}

.user-info h3 {
  margin: 16px 0 8px;
}

.user-info p {
  color: #666;
}

.bookmark-list,
.history-list {
  min-height: 300px;
}

.bookmark-card,
.history-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}

.bookmark-card:hover,
.history-card:hover {
  transform: translateY(-4px);
}

.bookmark-cover,
.history-cover {
  width: 100%;
  height: 160px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 12px;
}

.bookmark-cover .el-image,
.history-cover .el-image {
  width: 100%;
  height: 100%;
}

.bookmark-info,
.history-info {
  padding: 4px;
}

.bookmark-title,
.history-title {
  font-size: 16px;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bookmark-author,
.history-author {
  font-size: 13px;
  color: #666;
  margin: 0 0 8px;
}

.bookmark-tags,
.history-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}

.bookmark-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.history-chapter {
  font-size: 12px;
  color: #409eff;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bookmark-footer,
.history-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.bookmark-wordcount {
  color: #409eff;
}

.bookmark-time,
.history-time {
  color: #999;
}
</style>
