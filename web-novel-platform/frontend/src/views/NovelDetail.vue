<template>
  <div class="novel-detail">
    <!-- 顶部导航（复用 Home 的导航） -->
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

    <el-main class="main-content" v-loading="loading">
      <el-row :gutter="20" v-if="novel">
        <!-- 左侧：小说信息 -->
        <el-col :span="8">
          <el-card class="novel-info-card">
            <div class="novel-cover">
              <el-image :src="novel.cover || '/default-cover.png'" fit="cover" />
            </div>
            <h1 class="novel-title">{{ novel.title }}</h1>
            <p class="novel-author">作者：{{ novel.author }}</p>
            <div class="novel-meta">
              <el-tag>{{ novel.categoryName }}</el-tag>
              <el-tag :type="novel.status === 2 ? 'success' : 'warning'">
                {{ novel.status === 2 ? '完结' : '连载' }}
              </el-tag>
              <span>字数：{{ formatWordCount(novel.wordCount) }}</span>
            </div>
            <p class="novel-description">{{ novel.description }}</p>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="startReading" :disabled="!novel.chapters?.length">
                开始阅读
              </el-button>
              <el-button size="large" @click="toggleBookmark">
                {{ isBookmarked ? '取消收藏' : '加入书架' }}
              </el-button>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：章节列表 -->
        <el-col :span="16">
          <el-card class="chapter-list-card">
            <template #header>
              <div class="card-header">
                <span>章节列表</span>
                <span>共 {{ novel.chapters?.length || 0 }} 章</span>
              </div>
            </template>
            <div class="chapter-list">
              <el-button
                v-for="chapter in novel.chapters"
                :key="chapter.id"
                text
                @click="goToChapter(chapter.id)"
              >
                {{ chapter.title }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getNovelDetail } from '@/api/novel'
import { bookmark, unbookmark, getBookmarks } from '@/api/bookmark'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const novel = ref(null)
const bookmarkedNovels = ref([])

const isBookmarked = computed(() => {
  return bookmarkedNovels.value.some(b => b.novelId === novel.value?.id)
})

onMounted(async () => {
  await loadNovelDetail()
  if (userStore.token) {
    await loadBookmarks()
  }
})

async function loadNovelDetail() {
  loading.value = true
  try {
    const res = await getNovelDetail(route.params.novelId)
    novel.value = res.data
  } catch (error) {
    console.error('加载小说详情失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadBookmarks() {
  try {
    const res = await getBookmarks()
    bookmarkedNovels.value = res.data
  } catch (error) {
    console.error('加载收藏列表失败:', error)
  }
}

function formatWordCount(count) {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万字'
  }
  return count + '字'
}

function startReading() {
  if (novel.value?.chapters?.length > 0) {
    const firstChapter = novel.value.chapters[0]
    goToChapter(firstChapter.id)
  }
}

function goToChapter(chapterId) {
  router.push(`/novel/${novel.value.id}/chapter/${chapterId}`)
}

async function toggleBookmark() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (isBookmarked.value) {
      await unbookmark(novel.value.id)
      ElMessage.success('已取消收藏')
    } else {
      await bookmark(novel.value.id)
      ElMessage.success('已加入书架')
    }
    await loadBookmarks()
  } catch (error) {
    console.error('操作失败:', error)
  }
}
</script>

<style scoped>
.novel-detail {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
}

.novel-info-card {
  margin-bottom: 20px;
}

.novel-cover {
  width: 200px;
  height: 280px;
  margin: 0 auto 20px;
  overflow: hidden;
  border-radius: 8px;
}

.novel-title {
  font-size: 24px;
  text-align: center;
  margin-bottom: 12px;
}

.novel-author {
  text-align: center;
  color: #666;
  margin-bottom: 16px;
}

.novel-meta {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.novel-description {
  line-height: 1.8;
  color: #666;
  margin-bottom: 24px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapter-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  max-height: 600px;
  overflow-y: auto;
}

.chapter-list .el-button {
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
