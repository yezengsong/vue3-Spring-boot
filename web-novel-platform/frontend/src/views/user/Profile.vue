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
          <el-menu-item index="/user/profile" v-if="userStore.token">个人中心</el-menu-item>
          <el-menu-item index="/admin" v-if="userStore.token && (userStore.user.role === 'ADMIN' || userStore.user.role === 'SUPER_ADMIN')">后台管理</el-menu-item>
        </el-menu>
      </div>
    </el-header>

    <el-main class="main-content">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <div class="user-info">
              <el-avatar :size="80" :src="userInfo.avatar || '/image/default_person.png'" />
              <h3>{{ userInfo.username }}</h3>
              <p>{{ userInfo.email || '未设置邮箱' }}</p>
            </div>
            <el-menu :default-active="activeTab">
              <el-menu-item index="bookmarks" @click="switchTab('bookmarks')">
                <el-icon><Collection /></el-icon>
                我的书架
              </el-menu-item>
              <el-menu-item index="history" @click="switchTab('history')">
                <el-icon><Clock /></el-icon>
                阅读历史
              </el-menu-item>
              <el-menu-item index="comments" @click="switchTab('comments')">
                <el-icon><ChatDotRound /></el-icon>
                我的评论
              </el-menu-item>
              <el-menu-item index="settings" @click="switchTab('settings')">
                <el-icon><Setting /></el-icon>
                个人信息
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>

        <el-col :span="18">
          <!-- 我的书架 -->
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

          <!-- 阅读历史 -->
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

          <!-- 我的评论 -->
          <el-card v-if="activeTab === 'comments'">
            <template #header>我的评论</template>
            <div class="comment-list" v-loading="loading">
              <div v-for="comment in commentList" :key="comment.commentId" class="comment-item">
                <div class="comment-header">
                  <el-avatar :size="40" :src="comment.avatar || '/image/default_person.png'" />
                  <div class="comment-meta">
                    <span class="comment-username">{{ comment.username }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                </div>
                <div class="comment-content">
                  <p v-if="comment.replyToUsername" class="reply-info">
                    回复 <span class="reply-target">@{{ comment.replyToUsername }}</span>
                  </p>
                  <p>{{ comment.content }}</p>
                </div>
                <div class="comment-footer">
                  <span class="comment-source" @click="goToChapter(comment.novelId, comment.chapterId)">
                    来自：{{ comment.novelTitle }} - {{ comment.chapterTitle }}
                  </span>
                  <div class="comment-actions">
                    <el-button size="small" @click="goToChapter(comment.novelId, comment.chapterId)">
                      查看原文
                    </el-button>
                    <el-popconfirm
                      title="确定删除这条评论吗？"
                      @confirm="handleDeleteComment(comment.commentId)"
                    >
                      <template #reference>
                        <el-button size="small" type="danger">删除</el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
              <el-empty v-if="!loading && commentList.length === 0" description="暂无评论" />
            </div>
          </el-card>

          <!-- 个人信息 -->
          <el-card v-if="activeTab === 'settings'">
            <template #header>个人信息</template>
            <el-form :model="userForm" label-width="100px" style="max-width: 500px;">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  action=""
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleAvatarUpload"
                  accept="image/*"
                >
                  <el-button :icon="Upload" size="small">选择图片上传</el-button>
                </el-upload>
                <el-avatar v-if="userForm.avatar" :size="80" :src="userForm.avatar" style="margin-top: 10px;" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateUserInfo" :loading="updating">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
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
import { getMyComments, deleteMyComment } from '@/api/comment'
import { getUserInfo, updateUserInfo } from '@/api/user'
import { uploadImage } from '@/api/upload'
import { ElMessage } from 'element-plus'
import { Reading, Collection, Clock, ChatDotRound, Setting, Upload } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('bookmarks')
const loading = ref(false)
const updating = ref(false)
const bookmarks = ref([])
const historyList = ref([])
const commentList = ref([])
const userInfo = ref({
  username: userStore.user.username || '',
  email: userStore.user.email || '',
  avatar: userStore.user.avatar || ''
})
const userForm = ref({
  username: userStore.user.username || '',
  email: userStore.user.email || '',
  avatar: userStore.user.avatar || ''
})

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

async function loadComments() {
  loading.value = true
  try {
    const res = await getMyComments()
    commentList.value = res.data
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadUserInfo() {
  try {
    const res = await getUserInfo()
    if (res.data) {
      userInfo.value = res.data
      userForm.value = {
        username: res.data.username || '',
        email: res.data.email || '',
        avatar: res.data.avatar || ''
      }
      userStore.setUser(res.data)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

async function handleUpdateUserInfo() {
  updating.value = true
  try {
    await updateUserInfo({
      email: userForm.value.email,
      avatar: userForm.value.avatar
    })
    ElMessage.success('更新成功')
    userInfo.value = { ...userForm.value }
    userStore.setUser({ ...userStore.user, ...userForm.value })
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    updating.value = false
  }
}

async function handleDeleteComment(commentId) {
  try {
    await deleteMyComment(commentId)
    ElMessage.success('删除成功')
    commentList.value = commentList.value.filter(c => c.commentId !== commentId)
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

async function handleAvatarUpload(file) {
  try {
    const res = await uploadImage(file.raw)
    userForm.value.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

function switchTab(tab) {
  activeTab.value = tab
  if (tab === 'history' && historyList.value.length === 0) {
    loadHistory()
  } else if (tab === 'comments' && commentList.value.length === 0) {
    loadComments()
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

function goToChapter(novelId, chapterId) {
  router.push(`/novel/${novelId}/chapter/${chapterId}`)
}

onMounted(() => {
  loadBookmarks()
  loadUserInfo()
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
.history-list,
.comment-list {
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

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-meta {
  display: flex;
  flex-direction: column;
}

.comment-username {
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin-bottom: 12px;
  line-height: 1.6;
}

.reply-info {
  font-size: 13px;
  color: #666;
  margin: 0 0 8px;
}

.reply-target {
  color: #409eff;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-source {
  font-size: 12px;
  color: #409eff;
  cursor: pointer;
}

.comment-source:hover {
  text-decoration: underline;
}

.comment-actions {
  display: flex;
  gap: 8px;
}
</style>
