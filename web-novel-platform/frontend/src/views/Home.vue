<template>
  <div class="home">
    <!-- 顶部导航 -->
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
        <div class="user-actions">
          <template v-if="userStore.token">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.user.avatar" />
                <span>{{ userStore.user.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-input
          v-model="keyword"
          placeholder="搜索小说、作者"
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 推荐小说 -->
      <div class="novel-section">
        <div class="section-header">
          <h2>热门推荐</h2>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="novel in novelList" :key="novel.id">
            <el-card class="novel-card" shadow="hover" @click="goToNovel(novel.id)">
              <div class="novel-cover">
                <el-image :src="novel.cover || '/default-cover.png'" fit="cover" />
              </div>
              <div class="novel-info">
                <h3 class="novel-title">{{ novel.title }}</h3>
                <p class="novel-author">{{ novel.author }}</p>
                <p class="novel-desc">{{ novel.description }}</p>
                <div class="novel-meta">
                  <el-tag size="small">{{ novel.categoryName }}</el-tag>
                  <span class="novel-status" v-if="novel.status === 2">完结</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 排行榜 -->
      <div class="novel-section">
        <div class="section-header">
          <h2>点击排行榜</h2>
        </div>
        <el-table :data="rankList" style="width: 100%" :show-header="false">
          <el-table-column type="index" width="50" :index="indexMethod" />
          <el-table-column prop="title" label="小说名称" @click="goToNovel(row.id)">
            <template #default="{ row }">
              <span class="rank-title" @click="goToNovel(row.id)">{{ row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="clickCount" label="点击量" width="100" />
        </el-table>
      </div>
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <p>网络小说平台 © 2024 - 课程设计项目</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getNovelList } from '@/api/novel'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const keyword = ref('')
const novelList = ref([])
const rankList = ref([])

onMounted(async () => {
  await loadNovelList()
  await loadRankList()
})

async function loadNovelList() {
  try {
    const res = await getNovelList({ page: 1, size: 8, sortBy: 'create_time' })
    novelList.value = res.data.records
  } catch (error) {
    console.error('加载小说列表失败:', error)
  }
}

async function loadRankList() {
  try {
    const res = await getNovelList({ page: 1, size: 10, sortBy: 'click_count' })
    rankList.value = res.data.records
  } catch (error) {
    console.error('加载排行榜失败:', error)
  }
}

function handleSearch() {
  if (!keyword.value.trim()) {
    ElMessage.warning('请输入搜索内容')
    return
  }
  router.push({ path: '/category', query: { keyword: keyword.value } })
}

function goToNovel(novelId) {
  router.push(`/novel/${novelId}`)
}

function indexMethod(index) {
  return index + 1
}

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/')
      ElMessage.success('已退出登录')
    })
  } else if (command === 'profile') {
    router.push('/user/profile')
  }
}
</script>

<style scoped>
.home {
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

.el-menu {
  flex: 1;
  border: none;
  background: transparent;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
}

.search-section {
  margin-bottom: 30px;
}

.novel-section {
  margin-bottom: 40px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 24px;
  color: #333;
}

.novel-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.novel-card:hover {
  transform: translateY(-5px);
}

.novel-cover {
  height: 200px;
  overflow: hidden;
}

.novel-info {
  padding: 12px;
}

.novel-title {
  font-size: 16px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.novel-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.novel-desc {
  font-size: 13px;
  color: #999;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
}

.novel-meta {
  display: flex;
  gap: 8px;
}

.rank-title {
  cursor: pointer;
  color: #409eff;
}

.rank-title:hover {
  text-decoration: underline;
}

.footer {
  background: #333;
  color: #fff;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}
</style>
