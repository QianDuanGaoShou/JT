<template>
  <div class="financial-reports-page">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>财务报表</h1>
        <p>查看平台收入和教师结算数据</p>
      </div>
      <div class="header-actions">
        <el-date-picker
          v-model="selectedMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY年MM月"
          value-format="YYYY-MM"
          style="width:160px;"
          @change="loadReport"
        />
        <el-button class="btn-outline-gold" :icon="Download" @click="exportReport">
          导出报表
        </el-button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="summary-grid" v-loading="loading">
      <div class="summary-card" v-for="s in summaryCards" :key="s.label">
        <div class="sc-label">{{ s.label }}</div>
        <div class="sc-value" :style="{ color: s.color }">{{ s.value }}</div>
        <div class="sc-compare" v-if="s.compare">
          <span :style="{ color: s.compareValue >= 0 ? '#52C41A' : '#FF4D4F' }">
            {{ s.compareValue >= 0 ? '▲' : '▼' }}
            {{ Math.abs(s.compareValue) }}%
          </span>
          较上月
        </div>
      </div>
    </div>

    <!-- Revenue Chart -->
    <div class="chart-card">
      <div class="chart-header">
        <h3>月度收入分析</h3>
        <div class="chart-legend">
          <span class="legend-dot" style="background:#FFB000;"></span> 平台收入
          <span class="legend-dot" style="background:#52C41A;margin-left:12px;"></span> 教师分成
        </div>
      </div>
      <div ref="revenueChartRef" class="echarts-box"></div>
    </div>

    <!-- Settlement Table -->
    <div class="table-card">
      <div class="table-card-header">
        <h3>教师结算明细</h3>
        <span style="font-size:13px;color:#666;">{{ selectedMonth || '本月' }} 结算数据</span>
      </div>

      <el-table
        v-loading="tableLoading"
        :data="settlements"
        style="width:100%;"
        show-summary
        :summary-method="getSummary"
      >
        <el-table-column label="教师" min-width="180">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:10px;">
              <el-avatar :size="32" :src="row.teacherAvatar">{{ (row.teacherName || '?')[0] }}</el-avatar>
              <span style="font-size:14px;color:#E8E8E8;">{{ row.teacherName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="课程数" width="90" align="center">
          <template #default="{ row }">{{ row.courseCount }}</template>
        </el-table-column>

        <el-table-column label="订单数" width="90" align="center">
          <template #default="{ row }">{{ row.orderCount }}</template>
        </el-table-column>

        <el-table-column label="课程总收入" width="130" align="right">
          <template #default="{ row }">
            <span style="color:#E8E8E8;">¥{{ row.grossIncome?.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="平台抽成 (20%)" width="140" align="right">
          <template #default="{ row }">
            <span style="color:#FF4D4F;">-¥{{ row.platformFee?.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="教师实得" width="130" align="right">
          <template #default="{ row }">
            <span style="color:#FFB000;font-weight:700;">¥{{ row.netIncome?.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="结算状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.settled ? 'success' : 'warning'"
              effect="plain"
              size="small"
            >
              {{ row.settled ? '已结算' : '待结算' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="settlementTotal > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="settlementTotal"
          layout="prev, pager, next"
          background
          @current-change="loadSettlements"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { adminApi } from '@/api/admin'

const revenueChartRef = ref(null)
let   revenueChart    = null

const loading        = ref(false)
const tableLoading   = ref(false)
const settlements    = ref([])
const reportData     = ref({})
const settlementTotal= ref(0)
const currentPage    = ref(1)
const pageSize       = ref(20)
const selectedMonth  = ref(dayjs().format('YYYY-MM'))

const summaryCards = computed(() => [
  {
    label: '平台总收入',
    value: `¥${(reportData.value.totalRevenue || 0).toLocaleString()}`,
    color: '#FFB000',
    compare: true,
    compareValue: reportData.value.revenueGrowth || 12
  },
  {
    label: '教师结算总额',
    value: `¥${(reportData.value.teacherTotal || 0).toLocaleString()}`,
    color: '#52C41A',
    compare: true,
    compareValue: reportData.value.teacherGrowth || 10
  },
  {
    label: '平台净收益',
    value: `¥${(reportData.value.platformProfit || 0).toLocaleString()}`,
    color: '#1890FF',
    compare: false
  },
  {
    label: '订单总数',
    value: reportData.value.orderCount || 0,
    color: '#9B59B6',
    compare: false
  }
])

const getSummary = ({ columns, data }) => {
  const sums = []
  columns.forEach((col, idx) => {
    if (idx === 0) { sums[idx] = '汇总'; return }
    const vals = data.map(r => Number(r[col.property] || 0)).filter(v => !isNaN(v))
    if (vals.length) {
      const sum = vals.reduce((a, b) => a + b, 0)
      if (['grossIncome','platformFee','netIncome'].includes(col.property)) {
        sums[idx] = `¥${sum.toFixed(2)}`
      } else if (col.property && Number.isInteger(sum)) {
        sums[idx] = sum
      } else {
        sums[idx] = ''
      }
    } else {
      sums[idx] = ''
    }
  })
  return sums
}

const initRevenueChart = (data) => {
  if (!revenueChartRef.value) return
  if (revenueChart) revenueChart.dispose()
  revenueChart = echarts.init(revenueChartRef.value, 'dark')

  const months  = data?.months || ['1月','2月','3月','4月','5月','6月']
  const platform= data?.platform || [3200,4100,5800,4900,6700,7800]
  const teacher = data?.teacher  || [2560,3280,4640,3920,5360,6240]

  revenueChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 30, right: 20, bottom: 40, left: 70 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#1E1E1E',
      borderColor: '#333',
      textStyle: { color: '#E8E8E8' },
      formatter: (params) => {
        return `${params[0].name}<br/>${params.map(p => `${p.marker}${p.seriesName}: ¥${p.value.toLocaleString()}`).join('<br/>')}`
      }
    },
    legend: { textStyle: { color: '#A0A0A0' }, top: 4 },
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
        name: '平台收入',
        type: 'bar',
        data: platform,
        barMaxWidth: 32,
        stack: 'total',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0,0,0,1,[
            { offset:0, color:'rgba(255,176,0,0.9)' },
            { offset:1, color:'rgba(255,176,0,0.4)' }
          ]),
          borderRadius: [0,0,0,0]
        }
      },
      {
        name: '教师分成',
        type: 'bar',
        data: teacher,
        barMaxWidth: 32,
        stack: 'total',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0,0,0,1,[
            { offset:0, color:'rgba(82,196,26,0.9)' },
            { offset:1, color:'rgba(82,196,26,0.4)' }
          ]),
          borderRadius: [4,4,0,0]
        }
      }
    ]
  })
}

const exportReport = () => {
  ElMessage.info('导出功能开发中...')
}

const loadReport = async () => {
  loading.value = true
  try {
    const res = await adminApi.financialReport({ month: selectedMonth.value })
    reportData.value = res.data || res
    await nextTick()
    initRevenueChart(reportData.value.chartData)
    loadSettlements(1)
  } catch {
    ElMessage.error('加载报表失败')
    initRevenueChart(null)
  } finally {
    loading.value = false
  }
}

const loadSettlements = async (page = currentPage.value) => {
  tableLoading.value = true
  currentPage.value  = page
  try {
    const res = await adminApi.monthlySettlement({
      month: selectedMonth.value,
      page:  page - 1,
      size:  pageSize.value
    })
    const data = res.data || res
    settlements.value    = data.content || data.list || data || []
    settlementTotal.value= data.totalElements || data.total || settlements.value.length
  } catch {
    ElMessage.error('加载结算数据失败')
  } finally {
    tableLoading.value = false
  }
}

const handleResize = () => revenueChart?.resize()

onMounted(async () => {
  await loadReport()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  revenueChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.financial-reports-page { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  h1 { font-size: 22px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}
.header-actions { display: flex; gap: 10px; align-items: center; }

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.summary-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.25s;
  &:hover { border-color: rgba(255,176,0,0.3); transform: translateY(-2px); }
}
.sc-label   { font-size: 12px; color: #666; margin-bottom: 8px; }
.sc-value   { font-size: 26px; font-weight: 800; margin-bottom: 6px; }
.sc-compare { font-size: 12px; color: #666; }

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
.chart-legend { display: flex; align-items: center; font-size: 12px; color: #A0A0A0; }
.legend-dot { display: inline-block; width: 10px; height: 10px; border-radius: 50%; margin-right: 4px; }
.echarts-box { height: 260px; }

.table-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
}
.table-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #2A2A2A;
  h3 { font-size: 15px; font-weight: 700; color: #E8E8E8; }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #2A2A2A;
}
</style>
