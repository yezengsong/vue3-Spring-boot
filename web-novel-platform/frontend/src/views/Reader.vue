<template>
  <div class="reader">
    <!-- 顶部工具栏 -->
    <div class="reader-toolbar" v-show="showToolbar">
      <div class="toolbar-content">
        <el-button @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="chapter-nav">
          <el-button :disabled="!chapter.prevChapterId" @click="goToChapter(chapter.prevChapterId)">
            上一章
          </el-button>
          <el-select v-model="currentChapterId" @change="goToChapter($event)" style="width: 300px">
            <el-option
              v-for="c in chapters"
              :key="c.id"
              :label="c.title"
              :value="c.id"
            />
          </el-select>
          <el-button :disabled="!chapter.nextChapterId" @click="goToChapter(chapter.nextChapterId)">
            下一章
          </el-button>
        </div>
        <div class="toolbar-settings">
          <el-button @click="toggleTheme">
            <el-icon><Moon v-if="isDark" /><Sunny v-else /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 阅读区域 -->
    <div class="reader-content" :class="{ 'dark-theme': isDark }" @click="toggleToolbar">
      <div class="chapter-container" v-loading="loading">
        <h1 class="chapter-title">{{ chapter.title }}</h1>
        <div class="chapter-text" v-html="formattedContent"></div>
        <div class="chapter-nav-bottom">
          <el-button :disabled="!chapter.prevChapterId" @click.stop="goToChapter(chapter.prevChapterId)">
            上一章
          </el-button>
          <el-button :disabled="!chapter.nextChapterId" @click.stop="goToChapter(chapter.nextChapterId)">
            下一章
          </el-button>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>评论区</h3>
        <div class="comment-form">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="发表评论..."
          />
          <el-button type="primary" @click="submitComment" :loading="submitting">
            发表评论
          </el-button>
        </div>
        <div class="comment-list">
          <div class="comment-item" v-for="comment in comments" :key="comment.id">
            <div class="comment-header">
              <el-avatar :size="32" :src="comment.avatar" />
              <span class="comment-author">{{ comment.username }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button text size="small" @click="likeComment(comment)">
                <el-icon><ThumbUp /></el-icon>
                {{ comment.likeCount }}
              </el-button>
              <el-button text size="small" @click="showReplyInput(comment.id)">
                回复
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getChapterContent, getChapters } from '@/api/chapter'
import { getComments, createComment, likeComment as apiLikeComment } from '@/api/comment'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const showToolbar = ref(true)
const isDark = ref(false)
const loading = ref(true)
const submitting = ref(false)

const chapter = ref({})
const chapters = ref([])
const comments = ref([])
const currentChapterId = ref(Number(route.params.chapterId))
const commentContent = ref('')
const replyToCommentId = ref(null)

const formattedContent = computed(() => {
  if (!chapter.value.content) return ''
  return chapter.value.content.replace(/\n/g, '<br/>')
})

onMounted(async () => {
  await loadChapterContent()
  await loadChapters()
  await loadComments()
})

async function loadChapterContent() {
  loading.value = true
  try {
    const res = await getChapterContent(route.params.novelId, route.params.chapterId)
    chapter.value = res.data
  } catch (error) {
    console.error('加载章节失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadChapters() {
  try {
    const res = await getChapters(route.params.novelId)
    chapters.value = res.data
  } catch (error) {
    console.error('加载章节列表失败:', error)
  }
}

async function loadComments() {
  try {
    const res = await getComments(route.params.chapterId)
    comments.value = res.data
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

function toggleToolbar() {
  showToolbar.value = !showToolbar.value
}

function toggleTheme() {
  isDark.value = !isDark.value
}

function goToChapter(chapterId) {
  if (chapterId) {
    router.push(`/novel/${route.params.novelId}/chapter/${chapterId}`)
    currentChapterId.value = chapterId
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await createComment(route.params.chapterId, { content: commentContent.value })
    ElMessage.success('评论成功，待审核后显示')
    commentContent.value = ''
    await loadComments()
  } catch (error) {
    console.error('发表评论失败:', error)
  } finally {
    submitting.value = false
  }
}

async function likeComment(comment) {
  try {
    await apiLikeComment(comment.id)
    comment.likeCount++
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

function showReplyInput(commentId) {
  replyToCommentId.value = commentId
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.reader {
  min-height: 100vh;
}

.reader-toolbar {
  position: sticky;
  top: 0;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  padding: 12px 0;
}

.toolbar-content {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.chapter-nav {
  display: flex;
  gap: 12px;
  align-items: center;
}

.reader-content {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 40px 20px;
  cursor: pointer;
}

.reader-content.dark-theme {
  background: #1a1a1a;
}

.reader-content.dark-theme .chapter-container {
  background: #2a2a2a;
  color: #e0e0e0;
}

.chapter-container {
  max-width: 900px;
  margin: 0 auto 40px;
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chapter-title {
  font-size: 24px;
  text-align: center;
  margin-bottom: 32px;
  color: #333;
}

.chapter-text {
  font-size: 18px;
  line-height: 2;
  color: #333;
  text-indent: 2em;
}

.chapter-nav-bottom {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.comment-section {
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.comment-section h3 {
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-author {
  font-weight: bold;
}

.comment-time {
  color: #999;
  font-size: 13px;
}

.comment-content {
  line-height: 1.6;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  gap: 12px;
}
</style>
