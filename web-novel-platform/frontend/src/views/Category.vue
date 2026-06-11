<template>
  <div class="category-page">
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
      </div>
    </el-header>

    <el-main class="main-content">
      <h1>小说分类</h1>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-input
          v-model="keyword"
          placeholder="搜索小说"
          clearable
          @keyup.enter="loadNovelList"
          style="width: 300px"
        >
          <template #append>
            <el-button @click="loadNovelList">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 分类标签 -->
      <div class="category-tags">
        <el-tag
          :type="selectedCategory === null ? 'primary' : ''"
          @click="selectCategory(null)"
        >
          全部
        </el-tag>
        <el-tag
          v-for="cat in categories"
          :key="cat.id"
          :type="selectedCategory === cat.id ? 'primary' : ''"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </el-tag>
      </div>

      <!-- 小说列表 -->
      <div class="novel-list" v-loading="loading">
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

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[12, 24, 36]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadNovelList"
            @current-change="loadNovelList"
          />
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getNovelList, getCategoryList } from '@/api/novel'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const categories = ref([])
const novelList = ref([])
const selectedCategory = ref(null)
const keyword = ref(route.query.keyword || '')
const page = ref(1)
const size = ref(12)
const total = ref(0)

onMounted(async () => {
  await loadCategories()
  await loadNovelList()
})

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

async function loadNovelList() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      categoryId: selectedCategory.value,
      keyword: keyword.value,
      sortBy: 'create_time'
    }
    const res = await getNovelList(params)
    novelList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载小说列表失败:', error)
  } finally {
    loading.value = false
  }
}

function selectCategory(categoryId) {
  selectedCategory.value = categoryId
  page.value = 1
  loadNovelList()
}

function goToNovel(novelId) {
  router.push(`/novel/${novelId}`)
}
</script>

<style scoped>
.category-page {
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

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
}

.filter-section {
  margin-bottom: 24px;
}

.category-tags {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.category-tags .el-tag {
  cursor: pointer;
}

.novel-list {
  min-height: 400px;
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

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style>
