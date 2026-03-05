<template>
  <div class="admin-dashboard">
    <!-- Stats Grid -->
    <div class="stats-grid">
      <div class="stat-card" v-for="s in statCards" :key="s.label">
        <div class="stat-icon-box" :style="{ background: s.bg }">
          <el-icon :size="22" :color="s.color"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">{{ s.label }}</div>
          <div class="stat-value">{{ s.value }}</div>
        </div>
      </div>
    </div>

    <!-- Charts -->
    <div class="charts-row">
      <div class="chart-card chart-card--wide">
        <div class="chart-header">
          <h3>用户增长趋势</h3>
          <el-radio-group v-model="userChartType" size="small" @change="renderUserChart">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="student">学员</el-radio-button>
            <el-radio-button value="teacher">教师</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="userChartRef" class="echarts-box"></div>
      </div>
      <div class="chart-card">
        <div class="chart-header">
          <h3>收入趋势</h3>
        </div>
        <div ref="revenueChartRef" class="echarts-box"></div>
      </div>
    </div>

    <!-- Bottom Row: Activities + Pending -->
    <div class="bottom-row">
      <!-- Recent Activities -->
      <div class="panel-card">
        <div class="panel-header">
          <h3>最近动态</h3>
          <span style="font-size:12px;color:#555;">最近 20 条</span>
        </div>
        <div class="activities-list">
          <div
            v-for="a in activities"
            :key="a.id"
            class="activity-item"
          >
            <div class="activity-dot" :style="{ background: activityColor(a.type) }"></div>
            <div class="activity-content">
              <span class="activity-text">{{ a.description }}</span>
              <span class="activity-time">{{ formatDate(a.createdAt) }}</span>
            </div>
          </div>
          <div v-if="!activities.length" class="empty-panel">
            <p>暂无动态记录</p>
          </div>
        </div>
      </div>

      <!-- Pending Teachers -->
      <div class="panel-card">
        <div class="panel-header">
          <h3>待审核教师</h3>
          <el-button size="small" class="btn-outline-gold" @click="$router.push('/admin/teachers')">
            查看全部
          </el-button>
        </div>
        <div v-if="pendingTeachers.length" class="teacher-pending-list">
          <div
            v-for="t in pendingTeachers.slice(0, 5)"
            :key="t.id"
            class="teacher-pending-item"
          >
            <el-avatar :size="36" :src="t.avatar">{{ t.nickname?.[0] || t.username?.[0] }}</el-avatar>
            <div class="tp-info">
              <span class="tp-name">{{ t.nickname || t.username }}</span>
              <span class="tp-date">申请于 {{ formatDate(t.createdAt) }}</span>
            </div>
            <div class="tp-actions">
              <el-button size="small" type="success" @click="quickApprove(t)">通过</el-button>
              <el-button size="small" type="danger" plain @click="quickReject(t)">拒绝</el-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-panel">
          <el-icon size="32" color="#333"><CircleCheck /></el-icon>
          <p>暂无待审核申请</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, Briefcase, VideoPlay, Money, CircleCheck
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { adminApi } from '@/api/admin'

const router = useRouter()
const userChartRef    = ref(null)
const revenueChartRef = ref(null)
const userChartType   = ref('all')

let userChart    = null
let revenueChart = null

const dashStats      = ref({})
const activities     = ref([])
const pendingTeachers= ref([])

const statCards = computed(() => [
  { label: '总用户数',   value: dashStats.value.totalUsers    || 0,  icon: 'User',      bg: 'rgba(24,144,255,0.12)',  color: '#1890FF' },
  { label: '认证教师',   value: dashStats.value.totalTeachers || 0,  icon: 'Briefcase', bg: 'rgba(255,176,0,0.12)',   color: '#FFB000' },
  { label: '上架课程',   value: dashStats.value.totalCourses  || 0,  icon: 'VideoPlay', bg: 'rgba(82,196,26,0.12)',   color: '#52C41A' },
  { label: '平台总收入', value: `¥${(dashStats.value.totalRevenue || 0).toLocaleString()}`, icon: 'Money', bg: 'rgba(155,89,182,0.12)', color: '#9B59B6' }
])

const activityColor = (type) => {
  const map = {
    REGISTER: '#1890FF',
    PURCHASE: '#52C41A',
    SUBMIT:   '#FFB000',
    APPROVE:  '#52C41A',
    REJECT:   '#FF4D4F'
  }
  return map[type] || '#555'
}

const formatDate = (d) => d ? dayjs(d).format('MM-DD HH:mm') : '--'

const initUserChart = () => {
  if (!userChartRef.value) return
  if (userChart) userChart.dispose()
  userChart = echarts.init(userChartRef.value, 'dark')

  const months = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
  const mockData = {
    all:     [120,180,250,310,420,380,460,520,610,690,750,820],
    student: [100,150,210,260,350,310,380,440,520,590,640,700],
    teacher: [20, 30, 40, 50, 70, 70, 80, 80, 90, 100,110,120]
  }

  userChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 20, right: 20, bottom: 40, left: 60 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#1E1E1E',
      borderColor: '#333',
      textStyle: { color: '#E8E8E8' }
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: { color: '#666', fontSize: 11 },
      axisLine:  { lineStyle: { color: '#333' } },
      axisTick:  { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#666', fontSize: 11 },
      splitLine:  { lineStyle: { color: '#2A2A2A' } }
    },
    series: [{
      name: '用户数',
      type: 'bar',
      data: mockData[userChartType.value],
      barMaxWidth: 40,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#FFB000' },
          { offset: 1, color: 'rgba(255,176,0,0.3)' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

const renderUserChart = () => {
  initUserChart()
}

const initRevenueChart = () => {
  if (!revenueChartRef.value) return
  if (revenueChart) revenueChart.dispose()
  revenueChart = echarts.init(revenueChartRef.value, 'dark')

  const months = ['1月','2月','3月','4月','5月','6月']
  const platform = [3200, 4100, 5800, 4900, 6700, 7800]
  const teacher  = [2800, 3500, 5000, 4200, 5800, 6700]

  revenueChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 20, right: 20, bottom: 40, left: 60 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#1E1E1E',
      borderColor: '#333',
      textStyle: { color: '#E8E8E8' }
    },
    legend: { textStyle: { color: '#A0A0A0' }, top: 0 },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: { color: '#666', fontSize: 11 },
      axisLine:  { lineStyle: { color: '#333' } },
      axisTick:  { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#666', fontSize: 11, formatter: v => `¥${(v/1000).toFixed(0)}k` },
      splitLine: { lineStyle: { color: '#2A2A2A' } }
    },
    series: [
      {
        name: '平台总收入',
        type: 'line',
        data: platform,
        smooth: true,
        lineStyle: { color: '#FFB000' },
        itemStyle: { color: '#FFB000' },
        areaStyle: { color: 'rgba(255,176,0,0.1)' }
      },
      {
        name: '教师分成',
        type: 'line',
        data: teacher,
        smooth: true,
        lineStyle: { color: '#52C41A' },
        itemStyle: { color: '#52C41A' },
        areaStyle: { color: 'rgba(82,196,26,0.08)' }
      }
    ]
  })
}

const quickApprove = async (teacher) => {
  try {
    await adminApi.approveTeacher(teacher.id)
    ElMessage.success(`已通过 ${teacher.nickname || teacher.username} 的申请`)
    loadData()
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  }
}

const quickReject = async (teacher) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝申请', {
      confirmButtonText: '确认拒绝',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请说明拒绝原因...'
    })
    await adminApi.rejectTeacher(teacher.id, { reason })
    ElMessage.success('已拒绝申请')
    loadData()
  } catch {}
}

const loadData = async () => {
  try {
    const [statsRes, activitiesRes, teachersRes] = await Promise.allSettled([
      adminApi.dashboardStats(),
      adminApi.recentActivities({ size: 20 }),
      adminApi.pendingTeachers({ page: 0, size: 5 })
    ])

    if (statsRes.status === 'fulfilled') {
      dashStats.value = statsRes.value?.data || statsRes.value || {}
    }
    if (activitiesRes.status === 'fulfilled') {
      const d = activitiesRes.value?.data || activitiesRes.value
      activities.value = d.content || d.list || d || []
    }
    if (teachersRes.status === 'fulfilled') {
      const d = teachersRes.value?.data || teachersRes.value
      pendingTeachers.value = d.content || d.list || d || []
    }
  } catch {}
}

const handleResize = () => {
  userChart?.resize()
  revenueChart?.resize()
}

onMounted(async () => {
  await loadData()
  await nextTick()
  initUserChart()
  initRevenueChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  userChart?.dispose()
  revenueChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.admin-dashboard { display: flex; flex-direction: column; gap: 20px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.stat-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.25s;
  &:hover { border-color: rgba(255,176,0,0.3); transform: translateY(-2px); }
}
.stat-icon-box {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-label { font-size: 12px; color: #666; margin-bottom: 4px; }
.stat-value { font-size: 22px; font-weight: 700; color: #E8E8E8; }

.charts-row {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 16px;
}
.chart-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  h3 { font-size: 15px; font-weight: 700; color: #E8E8E8; }
}
.echarts-box { height: 240px; }

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 16px;
}
.panel-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  h3 { font-size: 15px; font-weight: 700; color: #E8E8E8; }
}

/* Activities */
.activities-list { display: flex; flex-direction: column; gap: 10px; max-height: 300px; overflow-y: auto; }
.activity-item { display: flex; align-items: flex-start; gap: 10px; }
.activity-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; margin-top: 5px; }
.activity-content { display: flex; flex-direction: column; gap: 2px; }
.activity-text { font-size: 13px; color: #C0C0C0; }
.activity-time { font-size: 11px; color: #555; }

/* Teacher Pending */
.teacher-pending-list { display: flex; flex-direction: column; gap: 10px; }
.teacher-pending-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: #252525;
  border-radius: 8px;
}
.tp-info { flex: 1; }
.tp-name { font-size: 13px; font-weight: 600; color: #E8E8E8; display: block; }
.tp-date { font-size: 11px; color: #555; }
.tp-actions { display: flex; gap: 6px; }

.empty-panel { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 24px 0; color: #555; p { font-size: 13px; } }
</style>
