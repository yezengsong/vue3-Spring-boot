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
              <el-menu-item index="1" @click="activeTab = 'bookmarks'">
                <el-icon><Collection /></el-icon>
                我的书架
              </el-menu-item>
              <el-menu-item index="2" @click="activeTab = 'history'">
                <el-icon><History /></el-icon>
                阅读历史
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card v-if="activeTab === 'bookmarks'">
            <template #header>我的书架</template>
            <div class="bookmark-list" v-loading="loading">
              <div class="bookmark-item" v-for="bookmark in bookmarks" :key="bookmark.id">
                <el-card shadow="hover" @click="goToNovel(bookmark.novelId)">
                  <div class="bookmark-info">
                    <span>小说 ID: {{ bookmark.novelId }}</span>
                    <span class="bookmark-time">收藏时间：{{ formatTime(bookmark.createTime) }}</span>
                  </div>
                </el-card>
              </div>
              <el-empty v-if="!loading && bookmarks.length === 0" description="还没有收藏任何小说" />
            </div>
          </el-card>

          <el-card v-if="activeTab === 'history'">
            <template #header>阅读历史</template>
            <div class="history-list">
              <el-empty description="暂无阅读历史" />
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
import { getBookmarks } from '@/api/bookmark'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('bookmarks')
const loading = ref(false)
const bookmarks = ref([])

onMounted(async () => {
  await loadBookmarks()
})

async function loadBookmarks() {
  loading.value = true
  try {
    const res = await getBookmarks()
    bookmarks.value = res.data
  } catch (error) {
    console.error('加载收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

function goToNovel(novelId) {
  router.push(`/novel/${novelId}`)
}
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

.bookmark-item {
  margin-bottom: 16px;
}

.bookmark-info {
  display: flex;
  justify-content: space-between;
  padding: 12px;
}

.bookmark-time {
  color: #999;
  font-size: 13px;
}
</style>
