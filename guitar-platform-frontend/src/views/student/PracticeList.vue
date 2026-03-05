<template>
  <div class="practice-list-page">
    <div class="page-container">
      <!-- Header -->
      <div class="page-top">
        <div>
          <h1 class="page-title">练习记录</h1>
          <p class="page-desc">查看你提交的练习和教师点评</p>
        </div>
      </div>

      <!-- Filter tabs -->
      <div class="filter-row">
        <div class="status-tabs">
          <div
            v-for="tab in statusTabs"
            :key="tab.value"
            class="status-tab"
            :class="{ active: statusFilter === tab.value }"
            @click="switchStatus(tab.value)"
          >
            {{ tab.label }}
            <span class="tab-count" v-if="tab.count !== undefined">{{ tab.count }}</span>
          </div>
        </div>
        <el-select
          v-model="courseFilter"
          placeholder="按课程筛选"
          clearable
          style="width:200px;"
          @change="loadPractices(1)"
        >
          <el-option
            v-for="c in myCourses"
            :key="c.courseId || c.id"
            :label="c.course?.title || c.title"
            :value="c.courseId || c.id"
          />
        </el-select>
      </div>

      <!-- Table -->
      <div class="table-card">
        <el-table
          v-loading="loading"
          :data="practices"
          style="width: 100%;"
          row-class-name="practice-row"
          @row-click="viewReport"
        >
          <el-table-column label="练习视频" width="120">
            <template #default="{ row }">
              <div class="video-thumb-wrap">
                <video
                  :src="row.videoUrl"
                  class="video-thumb"
                  preload="metadata"
                />
                <el-icon class="play-overlay"><VideoPlay /></el-icon>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="课程" min-width="160">
            <template #default="{ row }">
              <div class="course-cell">
                <span class="course-name">{{ row.courseName || row.course?.title || '--' }}</span>
                <span class="chapter-name" v-if="row.chapterName">{{ row.chapterName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="说明" min-width="160">
            <template #default="{ row }">
              <span class="note-text">{{ row.note || '无说明' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="提交时间" width="140">
            <template #default="{ row }">
              <span style="font-size:13px;color:#A0A0A0;">{{ formatDate(row.createdAt) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag
                :type="row.status === 'GRADED' ? 'success' : 'warning'"
                effect="plain"
                size="small"
              >
                {{ row.status === 'GRADED' ? '已批改' : '待批改' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="评分" width="100" align="center">
            <template #default="{ row }">
              <div v-if="row.score !== null && row.score !== undefined" class="score-cell">
                <span
                  class="score-value"
                  :style="{ color: scoreColor(row.score) }"
                >{{ row.score }}</span>
                <span class="score-max"> /100</span>
              </div>
              <span v-else style="color:#555;font-size:13px;">待评分</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button
                size="small"
                class="btn-outline-gold"
                @click.stop="viewReport(row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- Pagination -->
        <div class="pagination-wrap" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            background
            @current-change="loadPractices"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { practiceApi } from '@/api/practice'
import { courseApi } from '@/api/course'

const router = useRouter()
const loading      = ref(false)
const practices    = ref([])
const myCourses    = ref([])
const total        = ref(0)
const currentPage  = ref(1)
const pageSize     = ref(10)
const statusFilter = ref('')
const courseFilter = ref(null)

const pendingCount = computed(() => practices.value.filter(p => p.status === 'PENDING').length)
const gradedCount  = computed(() => practices.value.filter(p => p.status === 'GRADED').length)

const statusTabs = computed(() => [
  { label: '全部',   value: '',        count: total.value },
  { label: '待批改', value: 'PENDING', count: pendingCount.value },
  { label: '已批改', value: 'GRADED',  count: gradedCount.value }
])

const scoreColor = (score) => {
  if (score >= 90) return '#52C41A'
  if (score >= 70) return '#FFB000'
  if (score >= 60) return '#FAAD14'
  return '#FF4D4F'
}

const formatDate = (d) => d ? dayjs(d).format('MM-DD HH:mm') : '--'

const switchStatus = (v) => {
  statusFilter.value = v
  loadPractices(1)
}

const viewReport = (row) => {
  router.push(`/practice/${row.id}/report`)
}

const loadPractices = async (page = currentPage.value) => {
  loading.value = true
  currentPage.value = page
  try {
    const res = await practiceApi.list({
      page: page - 1,
      size: pageSize.value,
      status:   statusFilter.value || undefined,
      courseId: courseFilter.value  || undefined
    })
    const data = res.data || res
    practices.value = data.content || data.list || data || []
    total.value     = data.totalElements || data.total || practices.value.length
  } catch (err) {
    ElMessage.error('加载练习记录失败')
  } finally {
    loading.value = false
  }
}

const loadMyCourses = async () => {
  try {
    const res = await courseApi.myOrders({ page: 0, size: 100 })
    myCourses.value = (res.data || res).content || res.data || []
  } catch {}
}

onMounted(() => {
  loadPractices(1)
  loadMyCourses()
})
</script>

<style scoped>
.practice-list-page { min-height: calc(100vh - 64px); background: #121212; padding-bottom: 60px; }
.page-container { max-width: 1100px; margin: 0 auto; padding: 32px 24px; }

.page-top { margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 800; color: #E8E8E8; margin-bottom: 4px; }
.page-desc  { font-size: 14px; color: #666; }

.filter-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.status-tabs { display: flex; gap: 6px; }
.status-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  border-radius: 100px;
  font-size: 13px;
  color: #A0A0A0;
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { border-color: rgba(255,176,0,0.4); color: #E8E8E8; }
  &.active { background: rgba(255,176,0,0.12); border-color: #FFB000; color: #FFB000; font-weight: 600; }
}
.tab-count {
  background: rgba(255,255,255,0.1);
  border-radius: 100px;
  padding: 0 6px;
  font-size: 11px;
}
.status-tab.active .tab-count { background: rgba(255,176,0,0.2); }

.table-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
}

:deep(.practice-row) { cursor: pointer; }

.video-thumb-wrap {
  position: relative;
  width: 80px;
  height: 52px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
}
.video-thumb { width: 100%; height: 100%; object-fit: cover; background: #333; }
.play-overlay {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%,-50%);
  color: #FFB000;
  font-size: 20px;
  opacity: 0.9;
  pointer-events: none;
}

.course-cell { display: flex; flex-direction: column; gap: 2px; }
.course-name { font-size: 13px; color: #E8E8E8; font-weight: 500; }
.chapter-name { font-size: 12px; color: #666; }

.note-text { font-size: 13px; color: #A0A0A0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 140px; display: block; }

.score-cell { display: inline-flex; align-items: baseline; }
.score-value { font-size: 20px; font-weight: 700; }
.score-max   { font-size: 12px; color: #555; }

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #2A2A2A;
}
</style>
