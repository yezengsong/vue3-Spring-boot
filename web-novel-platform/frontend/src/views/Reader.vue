<template>
  <div class="reader">
    <!-- 顶部工具栏 -->
    <div class="reader-toolbar" v-show="showToolbar">
      <div class="toolbar-content">
        <el-button @click="goBackToNovelDetail">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="chapter-nav">
          <el-button :disabled="!chapter?.prevChapterId" @click="goToChapter(chapter?.prevChapterId)">
            上一章
          </el-button>
          <el-select v-model="currentChapterId" @change="goToChapter($event)" style="width: 300px">
            <el-option
              v-for="c in chapters"
              :key="c.chapterId"
              :label="c.title"
              :value="c.chapterId"
            />
          </el-select>
          <el-button :disabled="!chapter?.nextChapterId" @click="goToChapter(chapter?.nextChapterId)">
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
    <div class="reader-content" :class="{ 'dark-theme': isDark }">
      <div class="chapter-container" v-loading="loading" @click="toggleToolbar">
        <h1 class="chapter-title">{{ chapter.title }}</h1>
        <div class="chapter-text" v-html="formattedContent"></div>
        <div class="chapter-nav-bottom">
          <el-button :disabled="!chapter?.prevChapterId" @click.stop="goToChapter(chapter?.prevChapterId)">
            上一章
          </el-button>
          <el-button :disabled="!chapter?.nextChapterId" @click.stop="goToChapter(chapter?.nextChapterId)">
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
            maxlength="500"
            show-word-limit
          />
          <div class="form-actions">
            <el-button type="primary" @click="submitComment" :loading="submitting">
              发表评论
            </el-button>
          </div>
        </div>
        <div class="comment-list">
          <div class="comment-item" v-for="comment in comments" :key="comment.commentId">
            <div class="comment-header">
              <el-avatar :size="32" :src="comment.avatar || '/image/default_person.png'" />
              <span class="comment-author">{{ comment.username }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button text size="small" @click="likeComment(comment)">
                <el-icon><ThumbUp /></el-icon>
                {{ comment.likeCount }}
              </el-button>
              <el-button text size="small" @click="showReplyInput(comment)">
                回复
              </el-button>
            </div>
            
            <!-- 回复输入框 -->
            <div v-if="replyToCommentId === comment.commentId" class="reply-input-wrapper">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                :placeholder="`回复 @${comment.username} ...`"
                maxlength="500"
                show-word-limit
              />
              <div class="reply-actions">
                <el-button size="small" @click="cancelReply">取消</el-button>
                <el-button size="small" type="primary" @click="submitReply(comment)" :loading="submitting">
                  提交回复
                </el-button>
              </div>
            </div>
            
            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
              <div class="reply-item" v-for="reply in comment.replies" :key="reply.commentId">
                <div class="reply-header">
                  <el-avatar :size="24" :src="reply.avatar || '/image/default_person.png'" />
                  <span class="reply-author">{{ reply.username }}</span>
                  <span v-if="reply.replyToUsername" class="reply-to">回复 @{{ reply.replyToUsername }}</span>
                  <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div class="reply-actions-small">
                  <el-button text size="small" @click="likeComment(reply)">
                    <el-icon><ThumbUp /></el-icon>
                    {{ reply.likeCount }}
                  </el-button>
                </div>
              </div>
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
import { getComments, createComment, replyComment, likeComment as apiLikeComment } from '@/api/comment'
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
const replyContent = ref('')
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

// 监听路由变化，自动刷新章节内容
watch(() => route.params.chapterId, async (newChapterId) => {
  if (newChapterId && newChapterId !== chapter.value?.chapterId?.toString()) {
    currentChapterId.value = Number(newChapterId)
    await loadChapterContent()
    await loadComments()
  }
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

function goBackToNovelDetail() {
  router.push(`/novel/${route.params.novelId}`)
}

function goToChapter(chapterId) {
  if (chapterId) {
    loading.value = true
    currentChapterId.value = chapterId
    
    // 立即刷新章节内容
    try {
      const res = getChapterContent(route.params.novelId, chapterId)
      chapter.value = res.data
      
      // 刷新评论
      loadComments()
      
      // 路由跳转（保持历史记录）
      router.push(`/novel/${route.params.novelId}/chapter/${chapterId}`)
    } catch (error) {
      console.error('切换章节失败:', error)
      ElMessage.error('切换章节失败')
    } finally {
      loading.value = false
    }
  }
}

async function submitComment() {
  // 验证必填字段
  if (!commentContent.value || !commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  // 验证字数
  if (commentContent.value.length > 500) {
    ElMessage.warning('评论内容不能超过 500 字')
    return
  }

  submitting.value = true
  try {
    await createComment(route.params.chapterId, { content: commentContent.value.trim() })
    ElMessage.success('评论成功，待审核后显示')
    commentContent.value = ''
    await loadComments()
  } catch (error) {
    console.error('发表评论失败:', error)
    // 显示详细错误信息
    if (error.response) {
      ElMessage.error(`错误 ${error.response.status}: ${error.response.data?.message || error.response.statusText}`)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('发表评论失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

async function likeComment(comment) {
  try {
    await apiLikeComment(comment.commentId)
    comment.likeCount++
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

function showReplyInput(comment) {
  replyToCommentId.value = comment.commentId
  replyContent.value = ''
}

function cancelReply() {
  replyToCommentId.value = null
  replyContent.value = ''
}

async function submitReply(parentComment) {
  // 验证必填字段
  if (!replyContent.value || !replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  // 验证字数
  if (replyContent.value.length > 500) {
    ElMessage.warning('回复内容不能超过 500 字')
    return
  }

  submitting.value = true
  try {
    await replyComment(route.params.chapterId, parentComment.commentId, { 
      content: replyContent.value.trim() 
    })
    ElMessage.success('回复成功')
    replyContent.value = ''
    replyToCommentId.value = null
    await loadComments()
  } catch (error) {
    console.error('回复评论失败:', error)
    // 显示详细错误信息
    if (error.response) {
      ElMessage.error(`错误 ${error.response.status}: ${error.response.data?.message || error.response.statusText}`)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('回复失败，请重试')
    }
  } finally {
    submitting.value = false
  }
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

.form-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.reply-input-wrapper {
  margin-top: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 4px;
}

.reply-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.replies-list {
  margin-top: 12px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
  border-left: 3px solid #1890ff;
}

.reply-item {
  padding: 8px 0;
  border-bottom: 1px solid #e8e8e8;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.reply-author {
  font-weight: bold;
  font-size: 14px;
}

.reply-to {
  color: #666;
  font-size: 13px;
}

.reply-time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}

.reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 6px;
}

.reply-actions-small {
  display: flex;
  gap: 8px;
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
