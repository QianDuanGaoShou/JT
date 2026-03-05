<template>
  <div class="teacher-audit-page">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>教师审核</h1>
        <p>审核教师资质申请，管理教学团队</p>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="loadTeachers(1)">
      <!-- Pending Tab -->
      <el-tab-pane name="pending">
        <template #label>
          <span>待审核</span>
          <el-badge :value="pendingTotal" type="warning" style="margin-left:4px;" :hidden="!pendingTotal" />
        </template>

        <div class="teachers-grid" v-loading="loading">
          <div
            v-for="t in teachers"
            :key="t.id"
            class="teacher-card"
          >
            <div class="teacher-card-header">
              <el-avatar :size="56" :src="t.avatar" class="teacher-avatar">
                {{ (t.nickname || t.username || '?')[0] }}
              </el-avatar>
              <div class="teacher-basic">
                <h4>{{ t.nickname || t.username }}</h4>
                <p class="teacher-email">{{ t.email }}</p>
                <p class="apply-date">申请于 {{ formatDate(t.createdAt) }}</p>
              </div>
            </div>

            <div class="teacher-card-body">
              <!-- Qualification File -->
              <div class="qualification-section">
                <div class="q-label">
                  <el-icon color="#FFB000"><Document /></el-icon>
                  资质证明
                </div>
                <div v-if="t.qualificationUrl" class="q-file">
                  <a :href="t.qualificationUrl" target="_blank" rel="noopener" class="q-link">
                    <el-icon><View /></el-icon>
                    查看资质文件
                  </a>
                </div>
                <span v-else class="q-empty">未上传资质文件</span>
              </div>

              <div v-if="t.bio" class="teacher-bio">
                <p>{{ t.bio }}</p>
              </div>
            </div>

            <div class="teacher-card-actions">
              <el-button
                type="success"
                :icon="CircleCheck"
                :loading="processingId === t.id + '-approve'"
                @click="approveTeacher(t)"
                style="flex:1;"
              >
                通过申请
              </el-button>
              <el-button
                type="danger"
                plain
                :icon="CircleClose"
                :loading="processingId === t.id + '-reject'"
                @click="openRejectDialog(t)"
                style="flex:1;"
              >
                拒绝
              </el-button>
            </div>
          </div>

          <div v-if="!loading && !teachers.length" class="empty-state">
            <el-icon size="64" color="#333"><CircleCheck /></el-icon>
            <h3>暂无待审核申请</h3>
            <p>所有教师申请均已处理完毕</p>
          </div>
        </div>
      </el-tab-pane>

      <!-- Approved Tab -->
      <el-tab-pane label="已通过" name="approved">
        <div class="filter-bar">
          <el-input
            v-model="searchQuery"
            placeholder="搜索教师..."
            :prefix-icon="Search"
            clearable
            style="width:240px;"
            @input="handleSearch"
          />
        </div>

        <el-table
          v-loading="loading"
          :data="teachers"
          style="width:100%;"
        >
          <el-table-column label="教师" min-width="200">
            <template #default="{ row }">
              <div style="display:flex;align-items:center;gap:10px;">
                <el-avatar :size="36" :src="row.avatar">{{ (row.nickname || row.username || '?')[0] }}</el-avatar>
                <div>
                  <div style="font-size:14px;font-weight:600;color:#E8E8E8;">{{ row.nickname || row.username }}</div>
                  <div style="font-size:12px;color:#666;">{{ row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="课程数" width="90" align="center">
            <template #default="{ row }">{{ row.courseCount || 0 }}</template>
          </el-table-column>
          <el-table-column label="学员数" width="90" align="center">
            <template #default="{ row }">{{ row.studentCount || 0 }}</template>
          </el-table-column>
          <el-table-column label="加入时间" width="130">
            <template #default="{ row }">
              <span style="font-size:12px;color:#666;">{{ formatDate(row.approvedAt || row.createdAt) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag type="success" effect="plain" size="small">已认证</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Pagination -->
    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="loadTeachers"
      />
    </div>

    <!-- Reject Dialog -->
    <el-dialog
      v-model="rejectDialog.visible"
      title="拒绝教师申请"
      width="420px"
      :close-on-click-modal="false"
    >
      <div style="margin-bottom:12px;">
        <p style="font-size:14px;color:#A0A0A0;">
          请输入拒绝
          <strong style="color:#E8E8E8;">{{ rejectDialog.teacher?.nickname || rejectDialog.teacher?.username }}</strong>
          的原因，系统将通知该用户：
        </p>
      </div>
      <el-input
        v-model="rejectDialog.reason"
        type="textarea"
        :rows="4"
        placeholder="请输入拒绝原因（选填）..."
        maxlength="200"
        show-word-limit
      />
      <template #footer>
        <el-button @click="rejectDialog.visible = false">取消</el-button>
        <el-button
          type="danger"
          :loading="processingId === rejectDialog.teacher?.id + '-reject'"
          @click="confirmReject"
        >
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose, Document, View, Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { adminApi } from '@/api/admin'

const activeTab    = ref('pending')
const loading      = ref(false)
const teachers     = ref([])
const total        = ref(0)
const pendingTotal = ref(0)
const currentPage  = ref(1)
const pageSize     = ref(12)
const searchQuery  = ref('')
const processingId = ref(null)
let   searchTimer  = null

const rejectDialog = reactive({
  visible: false,
  teacher: null,
  reason:  ''
})

const formatDate = (d) => d ? dayjs(d).format('YYYY-MM-DD') : '--'

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadTeachers(1), 400)
}

const approveTeacher = async (t) => {
  processingId.value = `${t.id}-approve`
  try {
    await adminApi.approveTeacher(t.id)
    ElMessage.success(`已通过 ${t.nickname || t.username} 的教师申请`)
    loadTeachers(currentPage.value)
    loadPendingCount()
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    processingId.value = null
  }
}

const openRejectDialog = (t) => {
  rejectDialog.teacher = t
  rejectDialog.reason  = ''
  rejectDialog.visible = true
}

const confirmReject = async () => {
  const t = rejectDialog.teacher
  processingId.value = `${t.id}-reject`
  try {
    await adminApi.rejectTeacher(t.id, { reason: rejectDialog.reason })
    ElMessage.success('已拒绝申请')
    rejectDialog.visible = false
    loadTeachers(currentPage.value)
    loadPendingCount()
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    processingId.value = null
  }
}

const loadTeachers = async (page = currentPage.value) => {
  loading.value = true
  currentPage.value = page
  try {
    const api = activeTab.value === 'pending' ? adminApi.pendingTeachers : adminApi.approvedTeachers
    const res = await api({
      page:    page - 1,
      size:    pageSize.value,
      keyword: searchQuery.value || undefined
    })
    const data    = res.data || res
    teachers.value = data.content || data.list || data || []
    total.value    = data.totalElements || data.total || teachers.value.length
  } catch {
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

const loadPendingCount = async () => {
  try {
    const res = await adminApi.pendingTeachers({ page: 0, size: 1 })
    const d   = res.data || res
    pendingTotal.value = d.totalElements || d.total || 0
  } catch {}
}

onMounted(() => {
  loadTeachers(1)
  loadPendingCount()
})
</script>

<style scoped>
.teacher-audit-page { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  h1 { font-size: 22px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}

.filter-bar { margin-bottom: 16px; }

.teachers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  padding-top: 16px;
}

.teacher-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.25s;
  &:hover { border-color: rgba(255,176,0,0.3); transform: translateY(-2px); }
}

.teacher-card-header {
  display: flex;
  gap: 14px;
  padding: 16px;
  background: #252525;
  border-bottom: 1px solid #2A2A2A;
}
.teacher-avatar { background: #FFB000; color: #121212; font-weight: 700; flex-shrink: 0; }
.teacher-basic h4 { font-size: 15px; font-weight: 700; color: #E8E8E8; margin-bottom: 3px; }
.teacher-email { font-size: 12px; color: #A0A0A0; }
.apply-date    { font-size: 11px; color: #555; margin-top: 3px; }

.teacher-card-body { padding: 14px 16px; }

.qualification-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.q-label { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #A0A0A0; white-space: nowrap; }
.q-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #FFB000;
  &:hover { color: #FFC833; }
}
.q-empty { font-size: 13px; color: #555; }

.teacher-bio {
  background: #252525;
  border-radius: 6px;
  padding: 8px 10px;
  p { font-size: 12px; color: #A0A0A0; line-height: 1.5; }
}

.teacher-card-actions {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid #2A2A2A;
}

.empty-state {
  grid-column: 1/-1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px;
  color: #555;
  h3 { font-size: 18px; color: #444; }
  p  { font-size: 14px; }
}

.pagination-wrap { display: flex; justify-content: center; }
</style>
