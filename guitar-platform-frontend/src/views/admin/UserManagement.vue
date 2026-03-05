<template>
  <div class="user-management-page">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>用户管理</h1>
        <p>管理平台所有用户账号和权限</p>
      </div>
    </div>

    <!-- Filters -->
    <div class="filter-bar">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索用户名、邮箱..."
        :prefix-icon="Search"
        clearable
        style="width:240px;"
        @input="handleSearch"
      />
      <el-select v-model="filters.role" placeholder="角色筛选" clearable style="width:140px;" @change="loadUsers(1)">
        <el-option label="全部角色"   value="" />
        <el-option label="学员"      value="STUDENT" />
        <el-option label="教师"      value="TEACHER" />
        <el-option label="管理员"    value="ADMIN" />
      </el-select>
      <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width:130px;" @change="loadUsers(1)">
        <el-option label="全部状态" value="" />
        <el-option label="正常"    value="ACTIVE" />
        <el-option label="已禁用"  value="DISABLED" />
      </el-select>
      <div style="margin-left:auto;font-size:13px;color:#666;">
        共 {{ total }} 名用户
      </div>
    </div>

    <!-- Table -->
    <div class="table-card">
      <el-table
        v-loading="loading"
        :data="users"
        style="width:100%;"
      >
        <el-table-column label="用户" min-width="220">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" :src="row.avatar">{{ (row.nickname || row.username || '?')[0] }}</el-avatar>
              <div class="user-info">
                <span class="u-name">{{ row.nickname || row.username }}</span>
                <span class="u-email">{{ row.email }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="用户名" width="130">
          <template #default="{ row }">
            <span style="font-size:13px;color:#A0A0A0;font-family:monospace;">@{{ row.username }}</span>
          </template>
        </el-table-column>

        <el-table-column label="角色" width="90" align="center">
          <template #default="{ row }">
            <el-tag
              :type="roleTagType(row.role)"
              effect="plain"
              size="small"
            >
              {{ roleLabels[row.role] || row.role }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="余额" width="100" align="center">
          <template #default="{ row }">
            <span style="color:#FFB000;font-weight:600;">¥{{ (row.balance || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'ACTIVE' ? 'success' : 'danger'"
              effect="plain"
              size="small"
            >
              {{ row.status === 'ACTIVE' ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="注册时间" width="130">
          <template #default="{ row }">
            <span style="font-size:12px;color:#666;">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <!-- Recharge (Students only) -->
              <el-button
                v-if="row.role === 'STUDENT'"
                size="small"
                class="btn-outline-gold"
                @click="openRechargeDialog(row)"
              >
                充值
              </el-button>

              <!-- Toggle Status -->
              <el-popconfirm
                :title="`确定要${row.status === 'ACTIVE' ? '禁用' : '启用'}该用户？`"
                :confirm-button-text="row.status === 'ACTIVE' ? '禁用' : '启用'"
                :confirm-button-type="row.status === 'ACTIVE' ? 'danger' : 'success'"
                @confirm="toggleStatus(row)"
              >
                <template #reference>
                  <el-button
                    size="small"
                    :type="row.status === 'ACTIVE' ? 'danger' : 'success'"
                    plain
                    :loading="togglingId === row.id"
                  >
                    {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, sizes"
          :page-sizes="[10, 20, 50]"
          background
          @current-change="loadUsers"
          @size-change="(size) => { pageSize = size; loadUsers(1) }"
        />
      </div>
    </div>

    <!-- Recharge Dialog -->
    <el-dialog
      v-model="rechargeDialog.visible"
      title="充值余额"
      width="380px"
      :close-on-click-modal="false"
    >
      <div class="recharge-content">
        <div class="recharge-user-info">
          <el-avatar :size="40" :src="rechargeDialog.user?.avatar">
            {{ (rechargeDialog.user?.nickname || rechargeDialog.user?.username || '?')[0] }}
          </el-avatar>
          <div>
            <div style="font-size:14px;font-weight:600;color:#E8E8E8;">
              {{ rechargeDialog.user?.nickname || rechargeDialog.user?.username }}
            </div>
            <div style="font-size:12px;color:#666;">
              当前余额：¥{{ (rechargeDialog.user?.balance || 0).toFixed(2) }}
            </div>
          </div>
        </div>

        <el-form label-position="top">
          <el-form-item label="充值金额 (¥)">
            <el-input-number
              v-model="rechargeDialog.amount"
              :min="1"
              :max="99999"
              :precision="2"
              style="width:100%;"
              size="large"
            />
          </el-form-item>
          <div class="quick-amounts">
            <el-button
              v-for="amt in [10, 50, 100, 200, 500]"
              :key="amt"
              size="small"
              class="btn-outline-gold"
              @click="rechargeDialog.amount = amt"
            >
              ¥{{ amt }}
            </el-button>
          </div>
          <el-form-item label="充值备注">
            <el-input v-model="rechargeDialog.note" placeholder="选填" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="rechargeDialog.visible = false">取消</el-button>
        <el-button
          type="primary"
          class="btn-gradient"
          :loading="rechargeDialog.loading"
          @click="confirmRecharge"
        >
          确认充值
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { adminApi } from '@/api/admin'

const loading    = ref(false)
const togglingId = ref(null)
const users      = ref([])
const total      = ref(0)
const currentPage= ref(1)
const pageSize   = ref(10)
let   searchTimer= null

const filters = reactive({ keyword: '', role: '', status: '' })

const rechargeDialog = reactive({
  visible: false,
  user:    null,
  amount:  100,
  note:    '',
  loading: false
})

const roleLabels   = { STUDENT: '学员', TEACHER: '教师', ADMIN: '管理员' }
const roleTagType  = (r) => ({ ADMIN: 'danger', TEACHER: 'warning', STUDENT: '' })[r] || ''
const formatDate   = (d) => d ? dayjs(d).format('YYYY-MM-DD') : '--'

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadUsers(1), 400)
}

const toggleStatus = async (row) => {
  togglingId.value = row.id
  try {
    await adminApi.toggleUserStatus(row.id)
    row.status = row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
    ElMessage.success(`用户已${row.status === 'ACTIVE' ? '启用' : '禁用'}`)
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    togglingId.value = null
  }
}

const openRechargeDialog = (user) => {
  rechargeDialog.user    = user
  rechargeDialog.amount  = 100
  rechargeDialog.note    = ''
  rechargeDialog.visible = true
}

const confirmRecharge = async () => {
  if (!rechargeDialog.amount || rechargeDialog.amount <= 0) {
    ElMessage.warning('请输入有效的充值金额')
    return
  }
  rechargeDialog.loading = true
  try {
    await adminApi.rechargeBalance(rechargeDialog.user.id, {
      amount: rechargeDialog.amount,
      note:   rechargeDialog.note
    })
    rechargeDialog.user.balance = (rechargeDialog.user.balance || 0) + rechargeDialog.amount
    ElMessage.success(`已成功为 ${rechargeDialog.user.nickname || rechargeDialog.user.username} 充值 ¥${rechargeDialog.amount}`)
    rechargeDialog.visible = false
  } catch (err) {
    ElMessage.error(err.message || '充值失败')
  } finally {
    rechargeDialog.loading = false
  }
}

const loadUsers = async (page = currentPage.value) => {
  loading.value = true
  currentPage.value = page
  try {
    const res = await adminApi.users({
      page:    page - 1,
      size:    pageSize.value,
      keyword: filters.keyword  || undefined,
      role:    filters.role     || undefined,
      status:  filters.status   || undefined
    })
    const data = res.data || res
    users.value = data.content || data.list || data || []
    total.value = data.totalElements || data.total || users.value.length
  } catch {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadUsers(1))
</script>

<style scoped>
.user-management-page { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  h1 { font-size: 22px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.table-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
}

.user-cell { display: flex; align-items: center; gap: 10px; }
.user-info { display: flex; flex-direction: column; gap: 2px; }
.u-name  { font-size: 14px; font-weight: 600; color: #E8E8E8; }
.u-email { font-size: 12px; color: #666; }

.action-btns { display: flex; gap: 6px; }

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #2A2A2A;
}

/* Recharge Dialog */
.recharge-content { display: flex; flex-direction: column; gap: 16px; }
.recharge-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #252525;
  border-radius: 8px;
}

.quick-amounts { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 12px; }

:deep(.el-form-item__label) { color: #C0C0C0 !important; font-size: 13px; }
:deep(.el-input__inner) { color: #E8E8E8 !important; }
</style>
