<template>
  <div class="practice-grade-page">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>练习批改</h1>
        <p>审阅学员提交的练习并给予专业评分和点评</p>
      </div>
      <div class="view-toggle">
        <el-radio-group v-model="viewMode" @change="loadPractices(1)">
          <el-radio-button value="pending">
            待批改
            <el-badge :value="pendingTotal" type="warning" :max="99" />
          </el-radio-button>
          <el-radio-button value="graded">已批改</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div class="grade-layout">
      <!-- Left: Practice List -->
      <div class="list-panel">
        <div
          v-for="p in practices"
          :key="p.id"
          class="practice-card"
          :class="{ selected: selectedPractice?.id === p.id }"
          @click="selectPractice(p)"
        >
          <div class="practice-thumb-wrap">
            <video :src="p.videoUrl" class="practice-thumb" preload="metadata" />
            <el-icon class="play-icon"><VideoPlay /></el-icon>
          </div>
          <div class="practice-info">
            <div class="p-student-name">{{ p.studentName }}</div>
            <div class="p-course-name">{{ p.courseName }}</div>
            <div class="p-note" v-if="p.note">{{ p.note }}</div>
            <div class="p-time">{{ formatDate(p.createdAt) }}</div>
          </div>
          <div class="p-score" v-if="p.score !== null && p.score !== undefined">
            <span :style="{ color: scoreColor(p.score) }">{{ p.score }}</span>
          </div>
        </div>

        <div v-if="!loading && !practices.length" class="empty-list">
          <el-icon size="40" color="#333">
            <component :is="viewMode === 'pending' ? Clock : CircleCheck" />
          </el-icon>
          <p>{{ viewMode === 'pending' ? '暂无待批改的练习' : '暂无已批改记录' }}</p>
        </div>

        <div v-loading="loading" style="min-height:40px;" />

        <div v-if="total > pageSize" class="list-pagination">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            small
            background
            @current-change="loadPractices"
          />
        </div>
      </div>

      <!-- Right: Grade Panel -->
      <div class="grade-panel">
        <!-- No selection -->
        <div v-if="!selectedPractice" class="no-selection">
          <el-icon size="64" color="#2A2A2A"><Edit /></el-icon>
          <h3>选择一个练习开始批改</h3>
          <p>从左侧列表点击任意练习</p>
        </div>

        <template v-else>
          <!-- Video Player -->
          <div class="grade-video-wrap">
            <VideoPlayer
              :key="selectedPractice.id"
              :src="selectedPractice.videoUrl"
              :initial-position="0"
              class="grade-player"
            />
          </div>

          <!-- Student Info -->
          <div class="student-info-bar">
            <el-avatar :size="36" :src="selectedPractice.studentAvatar">
              {{ selectedPractice.studentName?.[0] }}
            </el-avatar>
            <div>
              <div class="si-name">{{ selectedPractice.studentName }}</div>
              <div class="si-course">{{ selectedPractice.courseName }} · {{ selectedPractice.chapterName }}</div>
            </div>
            <div class="si-time">{{ formatDate(selectedPractice.createdAt) }}</div>
          </div>

          <div v-if="selectedPractice.note" class="student-note">
            <el-icon color="#FFB000"><ChatLineRound /></el-icon>
            <span>{{ selectedPractice.note }}</span>
          </div>

          <!-- Grade Form -->
          <div class="grade-form" v-if="viewMode === 'pending' || (viewMode === 'graded' && isEditing)">
            <div class="score-input-section">
              <label class="grade-label">综合评分 (0-100)</label>
              <div class="score-input-row">
                <el-slider
                  v-model="gradeForm.score"
                  :min="0"
                  :max="100"
                  :step="1"
                  :marks="{ 0:'0', 60:'60', 75:'75', 90:'90', 100:'100' }"
                  style="flex:1;"
                  :color="scoreColor(gradeForm.score)"
                />
                <el-input-number
                  v-model="gradeForm.score"
                  :min="0"
                  :max="100"
                  size="small"
                  style="width:80px;"
                />
              </div>
              <div class="score-grade-tag">
                <span :style="{ color: scoreColor(gradeForm.score), background: `${scoreColor(gradeForm.score)}15`, borderColor: scoreColor(gradeForm.score) }">
                  {{ gradeText(gradeForm.score) }}
                </span>
              </div>
            </div>

            <div class="feedback-input-section">
              <label class="grade-label">教师评语</label>
              <el-input
                v-model="gradeForm.feedback"
                type="textarea"
                :rows="5"
                placeholder="给学员专业的点评和改进建议..."
                maxlength="500"
                show-word-limit
              />
            </div>

            <!-- Quick Feedback Templates -->
            <div class="quick-feedback">
              <span class="qf-label">常用评语：</span>
              <div class="qf-tags">
                <el-tag
                  v-for="t in feedbackTemplates"
                  :key="t"
                  size="small"
                  class="qf-tag"
                  @click="appendFeedback(t)"
                >
                  {{ t }}
                </el-tag>
              </div>
            </div>

            <el-button
              type="primary"
              class="btn-gradient submit-grade-btn"
              :loading="submitting"
              @click="submitGrade"
            >
              <el-icon><Check /></el-icon>
              提交评分
            </el-button>
          </div>

          <!-- Already Graded View -->
          <div v-else-if="viewMode === 'graded' && !isEditing" class="graded-view">
            <div class="graded-score">
              <div class="graded-score-label">已评分</div>
              <div class="graded-score-num" :style="{ color: scoreColor(selectedPractice.score) }">
                {{ selectedPractice.score }}<span style="font-size:14px;color:#555;">/100</span>
              </div>
            </div>
            <div class="graded-feedback">
              <div class="gf-label">评语：</div>
              <p class="gf-text">{{ selectedPractice.teacherFeedback || '无评语' }}</p>
            </div>
            <el-button class="btn-outline-gold" size="small" @click="startEdit">
              修改评分
            </el-button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  VideoPlay, Clock, CircleCheck, Edit, ChatLineRound, Check
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { teacherApi } from '@/api/teacher'
import VideoPlayer from '@/components/VideoPlayer.vue'

const viewMode        = ref('pending')
const loading         = ref(false)
const submitting      = ref(false)
const practices       = ref([])
const total           = ref(0)
const pendingTotal    = ref(0)
const currentPage     = ref(1)
const pageSize        = ref(15)
const selectedPractice= ref(null)
const isEditing       = ref(false)

const gradeForm = reactive({ score: 80, feedback: '' })

const feedbackTemplates = [
  '节奏稳定，继续保持！',
  '音准需要加强练习',
  '换弦速度可以再提高',
  '右手拨弦力度很均匀',
  '建议用节拍器辅助练习',
  '表现力很好，感情到位',
  '基本功扎实，可以挑战难度更高的曲目'
]

const scoreColor = (s) => {
  if (!s && s !== 0) return '#555'
  if (s >= 90) return '#52C41A'
  if (s >= 75) return '#FFB000'
  if (s >= 60) return '#FAAD14'
  return '#FF4D4F'
}

const gradeText = (s) => {
  if (s >= 90) return '优秀'
  if (s >= 75) return '良好'
  if (s >= 60) return '及格'
  return '需要加油'
}

const formatDate = (d) => d ? dayjs(d).format('MM-DD HH:mm') : '--'

const selectPractice = (p) => {
  selectedPractice.value = p
  isEditing.value = false
  if (viewMode.value === 'pending') {
    gradeForm.score    = 80
    gradeForm.feedback = ''
  }
}

const startEdit = () => {
  gradeForm.score    = selectedPractice.value.score || 80
  gradeForm.feedback = selectedPractice.value.teacherFeedback || ''
  isEditing.value    = true
}

const appendFeedback = (t) => {
  if (gradeForm.feedback) gradeForm.feedback += '，' + t
  else gradeForm.feedback = t
}

const submitGrade = async () => {
  if (!gradeForm.score && gradeForm.score !== 0) {
    ElMessage.warning('请输入评分')
    return
  }
  submitting.value = true
  try {
    await teacherApi.gradePractice(selectedPractice.value.id, {
      score:    gradeForm.score,
      feedback: gradeForm.feedback
    })
    ElMessage.success('评分提交成功！')
    selectedPractice.value = null
    isEditing.value = false
    loadPractices(currentPage.value)
    loadPendingCount()
  } catch (err) {
    ElMessage.error(err.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const loadPractices = async (page = currentPage.value) => {
  loading.value = true
  currentPage.value = page
  try {
    const api    = viewMode.value === 'pending' ? teacherApi.pendingPractices : teacherApi.gradedPractices
    const res    = await api({ page: page - 1, size: pageSize.value })
    const data   = res.data || res
    practices.value = data.content || data.list || data || []
    total.value     = data.totalElements || data.total || practices.value.length
  } catch {
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

const loadPendingCount = async () => {
  try {
    const res = await teacherApi.pendingPractices({ page: 0, size: 1 })
    const d   = res.data || res
    pendingTotal.value = d.totalElements || d.total || 0
  } catch {}
}

onMounted(() => {
  loadPractices(1)
  loadPendingCount()
})
</script>

<style scoped>
.practice-grade-page { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  h1 { font-size: 22px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}

.grade-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
  align-items: flex-start;
  height: calc(100vh - 180px);
}

/* List Panel */
.list-panel {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow-y: auto;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.practice-card {
  display: flex;
  gap: 10px;
  padding: 12px;
  border-bottom: 1px solid #252525;
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: #252525; }
  &.selected { background: rgba(255,176,0,0.08); border-left: 3px solid #FFB000; }
  &:last-child { border-bottom: none; }
}

.practice-thumb-wrap {
  position: relative;
  width: 72px;
  height: 44px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}
.practice-thumb { width: 100%; height: 100%; object-fit: cover; background: #333; }
.play-icon {
  position: absolute;
  inset: 0;
  margin: auto;
  width: 20px;
  height: 20px;
  color: #FFB000;
  opacity: 0.9;
  pointer-events: none;
}

.practice-info { flex: 1; min-width: 0; }
.p-student-name { font-size: 13px; font-weight: 600; color: #E8E8E8; }
.p-course-name  { font-size: 12px; color: #A0A0A0; margin: 2px 0; }
.p-note         { font-size: 11px; color: #666; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.p-time         { font-size: 11px; color: #444; margin-top: 2px; }

.p-score { font-size: 18px; font-weight: 700; flex-shrink: 0; }

.empty-list { display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 40px 0; color: #555; p { font-size: 13px; } }

.list-pagination { padding: 12px; border-top: 1px solid #252525; display: flex; justify-content: center; }

/* Grade Panel */
.grade-panel {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow-y: auto;
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.no-selection {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #555;
  h3 { font-size: 16px; color: #444; }
  p  { font-size: 13px; }
}

.grade-video-wrap {
  border-radius: 10px;
  overflow: hidden;
  aspect-ratio: 16/9;
  background: #000;
  flex-shrink: 0;
}
.grade-player { width: 100%; height: 100%; }

.student-info-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
.si-name   { font-size: 14px; font-weight: 600; color: #E8E8E8; }
.si-course { font-size: 12px; color: #A0A0A0; }
.si-time   { margin-left: auto; font-size: 12px; color: #555; }

.student-note {
  display: flex;
  gap: 6px;
  font-size: 13px;
  color: #A0A0A0;
  background: rgba(255,176,0,0.06);
  border-left: 3px solid #FFB000;
  padding: 8px 10px;
  border-radius: 0 6px 6px 0;
}

/* Grade Form */
.grade-form { display: flex; flex-direction: column; gap: 16px; }
.grade-label { font-size: 13px; color: #C0C0C0; font-weight: 500; display: block; margin-bottom: 8px; }

.score-input-row { display: flex; align-items: center; gap: 12px; }
.score-grade-tag { margin-top: 4px; }
.score-grade-tag span {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 100px;
  border: 1px solid;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s;
}

:deep(.el-textarea__inner) { background: #252525 !important; color: #E8E8E8 !important; border-color: #333 !important; }
:deep(.el-slider__runway) { background: #333 !important; }
:deep(.el-slider__bar) { background: #FFB000 !important; }
:deep(.el-slider__button) { border-color: #FFB000 !important; }

.quick-feedback { }
.qf-label { font-size: 12px; color: #555; display: block; margin-bottom: 6px; }
.qf-tags  { display: flex; flex-wrap: wrap; gap: 6px; }
.qf-tag {
  cursor: pointer;
  background: #252525 !important;
  border-color: #333 !important;
  color: #A0A0A0 !important;
  transition: all 0.2s;
  &:hover { border-color: #FFB000 !important; color: #FFB000 !important; }
}

.submit-grade-btn { height: 42px; font-size: 15px; font-weight: 600; display: flex; align-items: center; gap: 8px; }

/* Graded View */
.graded-view { display: flex; flex-direction: column; gap: 14px; }
.graded-score { }
.graded-score-label { font-size: 12px; color: #666; margin-bottom: 4px; }
.graded-score-num { font-size: 42px; font-weight: 800; line-height: 1; }
.graded-feedback { }
.gf-label { font-size: 12px; color: #666; margin-bottom: 6px; }
.gf-text { font-size: 14px; color: #C0C0C0; line-height: 1.7; background: #252525; padding: 10px; border-radius: 6px; }
</style>
