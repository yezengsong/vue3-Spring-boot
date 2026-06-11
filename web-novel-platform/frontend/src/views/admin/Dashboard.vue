<template>
  <div class="admin-dashboard">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <div class="logo">后台管理</div>
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="novel" @click="activeMenu = 'novel'">
            <el-icon><Document /></el-icon>
            小说管理
          </el-menu-item>
          <el-menu-item index="chapter" @click="activeMenu = 'chapter'">
            <el-icon><Document /></el-icon>
            章节管理
          </el-menu-item>
          <el-menu-item index="comment" @click="activeMenu = 'comment'">
            <el-icon><ChatDotRound /></el-icon>
            评论管理
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <!-- 顶部栏 -->
        <el-header>
          <div class="header-content">
            <span>欢迎回来，{{ userStore.user.username }}</span>
            <el-button @click="$router.push('/')">返回首页</el-button>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main>
          <!-- 小说管理 -->
          <div v-if="activeMenu === 'novel'">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>小说管理</span>
                  <el-button type="primary" @click="showNovelDialog = true">新增小说</el-button>
                </div>
              </template>
              <el-table :data="novelList" v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="author" label="作者" width="120" />
                <el-table-column prop="categoryId" label="分类" width="100" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 2 ? 'success' : 'warning'">
                      {{ row.status === 2 ? '完结' : '连载' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <el-button size="small" @click="editNovel(row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteNovel(row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>

          <!-- 章节管理 -->
          <div v-if="activeMenu === 'chapter'">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>章节管理</span>
                  <el-button type="primary" @click="showChapterDialog = true">新增章节</el-button>
                </div>
              </template>
              <el-form :inline="true">
                <el-form-item label="小说名称">
                  <el-select v-model="chapterQuery.novelName" placeholder="选择小说" style="width: 200px" clearable>
                    <el-option
                      v-for="novel in novelList"
                      :key="novel.id"
                      :label="novel.title"
                      :value="novel.title"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadChapters">查询</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="chapterList" v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="novelId" label="小说 ID" width="100" />
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="orderNum" label="顺序" width="80" />
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <el-button size="small" @click="editChapter(row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteChapter(row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>

          <!-- 评论管理 -->
          <div v-if="activeMenu === 'comment'">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>评论管理</span>
                </div>
              </template>
              <el-alert
                v-if="novelList.length === 0"
                title="提示"
                type="info"
                description="暂无小说数据，请先添加小说"
                show-icon
                :closable="false"
              />
              <el-form v-else :inline="true">
                <el-form-item label="小说名称">
                  <el-select 
                    v-model="commentQuery.novelName" 
                    placeholder="选择小说" 
                    style="width: 200px" 
                    clearable
                    @change="loadComments"
                  >
                    <el-option
                      v-for="novel in novelList"
                      :key="novel.id"
                      :label="novel.title"
                      :value="novel.title"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadComments">查询</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="commentList" v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="chapterId" label="章节 ID" width="100" />
                <el-table-column prop="content" label="内容" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                      {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <el-button
                      v-if="row.status === 0"
                      size="small"
                      type="success"
                      @click="auditComment(row.id, 1)"
                    >
                      通过
                    </el-button>
                    <el-button
                      v-if="row.status === 0"
                      size="small"
                      type="danger"
                      @click="auditComment(row.id, 2)"
                    >
                      拒绝
                    </el-button>
                    <el-button size="small" type="danger" @click="deleteComment(row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>

    <!-- 小说编辑对话框 -->
    <el-dialog v-model="showNovelDialog" :title="novelForm.id ? '编辑小说' : '新增小说'">
      <el-form :model="novelForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="novelForm.title" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="novelForm.author" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="novelForm.categoryName" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="novelForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="novelForm.status">
            <el-option label="连载" :value="1" />
            <el-option label="完结" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNovelDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNovel">保存</el-button>
      </template>
    </el-dialog>

    <!-- 章节编辑对话框 -->
    <el-dialog v-model="showChapterDialog" :title="chapterForm.id ? '编辑章节' : '新增章节'">
      <el-form :model="chapterForm" label-width="80px">
        <el-form-item label="小说">
          <el-select v-model="chapterForm.novelName" placeholder="选择小说" style="width: 100%">
            <el-option
              v-for="novel in novelList"
              :key="novel.id"
              :label="novel.title"
              :value="novel.title"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="chapterForm.title" />
        </el-form-item>
        <el-form-item label="顺序">
          <el-input-number v-model="chapterForm.orderNum" :min="1" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="chapterForm.content" type="textarea" :rows="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChapterDialog = false">取消</el-button>
        <el-button type="primary" @click="saveChapter">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('novel')
const loading = ref(false)

// 小说管理
const novelList = ref([])
const showNovelDialog = ref(false)
const novelForm = ref({})
const categories = ref([])

// 章节管理
const chapterList = ref([])
const showChapterDialog = ref(false)
const chapterQuery = ref({ novelName: '' })
const chapterForm = ref({})

// 评论管理
const commentList = ref([])
const commentQuery = ref({ novelName: '' })

onMounted(() => {
  if (userStore.user.role !== 'ADMIN') {
    ElMessage.error('无权限访问')
    router.push('/')
    return
  }
  loadCategories()
  loadNovels()
})

// 监听小说对话框打开，初始化表单
watch(showNovelDialog, (newVal) => {
  if (newVal && !novelForm.value.id) {
    // 新增模式，初始化表单
    novelForm.value = { status: 1 }
  }
})

// 监听章节对话框打开，初始化表单
watch(showChapterDialog, (newVal) => {
  if (newVal && !chapterForm.value.id) {
    // 新增模式，初始化表单
    chapterForm.value = { orderNum: 1 }
  }
})

async function loadCategories() {
  try {
    const res = await request.get('/category/list')
    categories.value = res.data || res
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

async function loadNovels() {
  loading.value = true
  try {
    const res = await request.get('/novel/list', {
      params: { page: 1, size: 100 }
    })
    novelList.value = (res.data || res).records
  } catch (error) {
    console.error('加载小说列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadChapters() {
  if (!chapterQuery.value.novelName) {
    ElMessage.warning('请选择小说名称')
    return
  }
  // 通过小说名称查找小说 ID
  const novel = novelList.value.find(n => n.title === chapterQuery.value.novelName)
  if (!novel) {
    ElMessage.error('未找到该小说')
    return
  }
  loading.value = true
  try {
    console.log('=== 请求章节列表 ===')
    console.log('小说 ID:', novel.id)
    console.log('请求 URL:', `/novel/${novel.id}/chapters`)
    const res = await request.get(`/novel/${novel.id}/chapters`)
    console.log('=== 章节列表响应 ===')
    console.log('响应数据:', res)
    console.log('res.data:', res.data)
    console.log('res:', res)
    chapterList.value = res.data || res
    console.log('章节列表:', chapterList.value)
    if (chapterList.value.length === 0) {
      ElMessage.info('该小说暂无章节')
    } else {
      ElMessage.success(`加载到 ${chapterList.value.length} 个章节`)
    }
  } catch (error) {
    console.error('=== 加载章节列表失败 ===')
    console.error('错误对象:', error)
    console.error('错误响应:', error.response)
    console.error('错误消息:', error.message)
    ElMessage.error('加载章节失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

async function saveNovel() {
  // 验证必填字段
  if (!novelForm.value.title) {
    ElMessage.warning('请输入小说标题')
    return
  }
  if (!novelForm.value.author) {
    ElMessage.warning('请输入作者')
    return
  }
  if (!novelForm.value.categoryName) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!novelForm.value.status) {
    novelForm.value.status = 1  // 默认连载状态
  }
  
  try {
    // 确保只发送必要的字段
    const novelData = {
      title: novelForm.value.title,
      author: novelForm.value.author,
      categoryName: novelForm.value.categoryName,
      description: novelForm.value.description || '',
      status: novelForm.value.status,
      cover: novelForm.value.cover || ''
    }
    
    if (novelForm.value.id) {
      await request.put(`/admin/novel/${novelForm.value.id}`, novelData)
    } else {
      await request.post('/admin/novel', novelData)
    }
    ElMessage.success('保存成功')
    showNovelDialog.value = false
    // 重置表单
    novelForm.value = {}
    loadNovels()
  } catch (error) {
    console.error('保存失败:', error)
    // 显示详细错误信息
    if (error.response) {
      ElMessage.error(`错误 ${error.response.status}: ${error.response.data?.message || error.response.statusText}`)
    } else {
      ElMessage.error('保存失败，请重试')
    }
  }
}

async function saveChapter() {
  try {
    if (chapterForm.value.id) {
      await request.put(`/admin/chapter/${chapterForm.value.id}`, chapterForm.value)
    } else {
      // 新增章节时，通过小说名称查找小说 ID
      const novel = novelList.value.find(n => n.title === chapterForm.value.novelName)
      if (!novel) {
        ElMessage.error('未找到该小说')
        return
      }
      await request.post('/admin/novel/chapter', chapterForm.value, {
        params: { novelName: chapterForm.value.novelName }
      })
    }
    ElMessage.success('保存成功')
    showChapterDialog.value = false
    loadChapters()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

function editNovel(row) {
  // 查找分类名称
  const category = categories.value.find(c => c.id === row.categoryId)
  novelForm.value = { 
    ...row,
    categoryName: category ? category.name : ''
  }
  showNovelDialog.value = true
}

function editChapter(row) {
  // 查找小说名称
  const novel = novelList.value.find(n => n.id === row.novelId)
  chapterForm.value = { 
    ...row,
    novelName: novel ? novel.title : ''
  }
  showChapterDialog.value = true
}

async function deleteNovel(id) {
  await ElMessageBox.confirm('确定要删除这本小说吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/admin/novel/${id}`)
    ElMessage.success('删除成功')
    loadNovels()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

async function deleteChapter(id) {
  await ElMessageBox.confirm('确定要删除这个章节吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/admin/chapter/${id}`)
    ElMessage.success('删除成功')
    loadChapters()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

async function loadComments() {
  if (!commentQuery.value.novelName) {
    ElMessage.warning('请选择小说名称')
    return
  }
  // 通过小说名称查找小说 ID
  const novel = novelList.value.find(n => n.title === commentQuery.value.novelName)
  if (!novel) {
    ElMessage.error('未找到该小说')
    return
  }
  loading.value = true
  try {
    console.log('=== 请求评论列表 ===')
    console.log('小说 ID:', novel.id)
    console.log('请求 URL:', `/admin/novel/${novel.id}/comments`)
    const res = await request.get(`/admin/novel/${novel.id}/comments`)
    console.log('=== 评论列表响应 ===')
    console.log('响应数据:', res)
    console.log('res.data:', res.data)
    commentList.value = res.data || res
    console.log('评论列表:', commentList.value)
    if (commentList.value.length === 0) {
      ElMessage.info('该小说暂无评论')
    } else {
      ElMessage.success(`加载到 ${commentList.value.length} 条评论`)
    }
  } catch (error) {
    console.error('=== 加载评论列表失败 ===')
    console.error('错误对象:', error)
    console.error('错误响应:', error.response)
    console.error('错误消息:', error.message)
    const errorMsg = error.response ? 
      `错误 ${error.response.status}: ${error.response.data?.message || error.response.statusText}` : 
      error.message
    ElMessage.error('加载评论失败：' + errorMsg)
  } finally {
    loading.value = false
  }
}

async function auditComment(id, status) {
  try {
    await request.put(`/admin/comment/${id}/audit`, null, { params: { status } })
    ElMessage.success('操作成功')
    loadComments()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

async function deleteComment(id) {
  await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/admin/comment/${id}`)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    console.error('删除失败:', error)
  }
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  background-color: #2b3a4b;
}

.el-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.el-main {
  background: #f5f5f5;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
