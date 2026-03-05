<template>
  <div class="profile-page">
    <div class="page-container">
      <div class="profile-layout">
        <!-- Left: Avatar + Stats -->
        <div class="left-col">
          <div class="avatar-card">
            <div class="avatar-wrap">
              <el-avatar
                :size="96"
                :src="authStore.avatar"
                class="profile-avatar"
              >
                {{ authStore.fullName.charAt(0) }}
              </el-avatar>
              <div class="avatar-upload-btn" @click="triggerAvatarUpload">
                <el-icon size="16"><Camera /></el-icon>
              </div>
              <input
                ref="avatarInputRef"
                type="file"
                accept="image/*"
                style="display:none;"
                @change="handleAvatarChange"
              />
            </div>
            <h2 class="profile-name">{{ authStore.fullName }}</h2>
            <div class="profile-role">
              <el-icon color="#FFB000"><Medal /></el-icon>
              {{ roleLabels[authStore.userInfo?.role] || '学员' }}
            </div>
            <p class="profile-email">{{ authStore.userInfo?.email }}</p>
          </div>

          <!-- Learning Stats -->
          <div class="stats-card">
            <h3 class="stats-title">学习统计</h3>
            <div class="stats-grid">
              <div class="stats-item">
                <div class="stats-value">{{ stats.totalCourses }}</div>
                <div class="stats-label">已购课程</div>
              </div>
              <div class="stats-item">
                <div class="stats-value">{{ stats.totalHours }}h</div>
                <div class="stats-label">学习时长</div>
              </div>
              <div class="stats-item">
                <div class="stats-value">{{ stats.practiceCount }}</div>
                <div class="stats-label">练习提交</div>
              </div>
              <div class="stats-item">
                <div class="stats-value" :style="{ color: scoreColor(stats.avgScore) }">
                  {{ stats.avgScore || '--' }}
                </div>
                <div class="stats-label">平均分</div>
              </div>
            </div>
          </div>

          <!-- Balance Card -->
          <div class="balance-card">
            <div class="balance-header">
              <el-icon color="#FFB000"><Wallet /></el-icon>
              <span>账户余额</span>
            </div>
            <div class="balance-amount">¥{{ userInfo.balance?.toFixed(2) || '0.00' }}</div>
            <p style="font-size:12px;color:#555;margin-top:4px;">余额用于购买课程</p>
          </div>
        </div>

        <!-- Right: Edit Form -->
        <div class="right-col">
          <div class="form-card">
            <div class="form-card-header">
              <h2>个人信息</h2>
              <p>管理你的账号信息</p>
            </div>

            <el-form
              ref="formRef"
              :model="userInfo"
              :rules="rules"
              label-position="top"
            >
              <div class="form-row">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="userInfo.nickname" placeholder="你的昵称" :prefix-icon="User" />
                </el-form-item>
                <el-form-item label="用户名">
                  <el-input :value="userInfo.username" disabled>
                    <template #prefix><el-icon><AtSign /></el-icon></template>
                  </el-input>
                </el-form-item>
              </div>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userInfo.email" type="email" placeholder="邮箱地址" :prefix-icon="Message" />
              </el-form-item>

              <el-form-item label="个人简介">
                <el-input
                  v-model="userInfo.bio"
                  type="textarea"
                  :rows="4"
                  placeholder="介绍一下你自己..."
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-button
                type="primary"
                class="btn-gradient save-btn"
                :loading="saving"
                @click="saveProfile"
              >
                保存修改
              </el-button>
            </el-form>
          </div>

          <!-- Change Password -->
          <div class="form-card">
            <div class="form-card-header">
              <h2>修改密码</h2>
              <p>定期更换密码保护账号安全</p>
            </div>

            <el-form
              ref="pwdFormRef"
              :model="pwdForm"
              :rules="pwdRules"
              label-position="top"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                  v-model="pwdForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                  :prefix-icon="Lock"
                />
              </el-form-item>
              <div class="form-row">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="pwdForm.newPassword"
                    type="password"
                    placeholder="至少6个字符"
                    show-password
                    :prefix-icon="Lock"
                  />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="pwdForm.confirmPassword"
                    type="password"
                    placeholder="再次输入新密码"
                    show-password
                    :prefix-icon="Lock"
                  />
                </el-form-item>
              </div>

              <el-button
                class="btn-outline-gold"
                :loading="changingPwd"
                @click="changePassword"
              >
                修改密码
              </el-button>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Message, Lock, Camera, Medal, Wallet, AtSign } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { uploadApi } from '@/api/upload'
import { studyApi } from '@/api/study'
import { useAuthStore } from '@/store/auth'

const authStore     = useAuthStore()
const formRef       = ref(null)
const pwdFormRef    = ref(null)
const avatarInputRef= ref(null)
const saving        = ref(false)
const changingPwd   = ref(false)

const roleLabels = { STUDENT: '学员', TEACHER: '认证教师', ADMIN: '管理员' }

const userInfo = reactive({
  username: '',
  nickname: '',
  email:    '',
  bio:      '',
  balance:  0
})

const stats = reactive({
  totalCourses:  0,
  totalHours:    0,
  practiceCount: 0,
  avgScore:      null
})

const pwdForm = reactive({
  oldPassword:     '',
  newPassword:     '',
  confirmPassword: ''
})

const validateConfirm = (_r, v, cb) => {
  if (v !== pwdForm.newPassword) cb(new Error('两次输入的密码不一致'))
  else cb()
}

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email:    [{ type: 'email', message: '请输入有效邮箱', trigger: 'blur' }]
}

const pwdRules = {
  oldPassword:     [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword:     [{ required: true, min: 6, message: '新密码至少6个字符', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
}

const scoreColor = (s) => {
  if (!s) return '#E8E8E8'
  if (s >= 90) return '#52C41A'
  if (s >= 75) return '#FFB000'
  if (s >= 60) return '#FAAD14'
  return '#FF4D4F'
}

const triggerAvatarUpload = () => avatarInputRef.value?.click()

const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await uploadApi.image(fd)
    const url = (res.data || res).url
    await authApi.updateProfile({ avatar: url })
    authStore.updateUserInfo({ avatar: url })
    ElMessage.success('头像更新成功')
  } catch (err) {
    ElMessage.error('头像上传失败')
  }
  e.target.value = ''
}

const saveProfile = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await authApi.updateProfile({
      nickname: userInfo.nickname,
      email:    userInfo.email,
      bio:      userInfo.bio
    })
    authStore.updateUserInfo({ nickname: userInfo.nickname, email: userInfo.email, bio: userInfo.bio })
    ElMessage.success('保存成功')
  } catch (err) {
    ElMessage.error(err.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await authApi.changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch (err) {
    ElMessage.error(err.message || '密码修改失败')
  } finally {
    changingPwd.value = false
  }
}

onMounted(async () => {
  try {
    const [profileRes, statsRes] = await Promise.allSettled([
      authApi.getProfile(),
      studyApi.stats()
    ])
    if (profileRes.status === 'fulfilled') {
      const u = profileRes.value?.data || profileRes.value
      Object.assign(userInfo, {
        username: u.username || '',
        nickname: u.nickname || '',
        email:    u.email    || '',
        bio:      u.bio      || '',
        balance:  u.balance  || 0
      })
      authStore.setUserInfo(u)
    }
    if (statsRes.status === 'fulfilled') {
      const s = statsRes.value?.data || statsRes.value
      Object.assign(stats, s || {})
    }
  } catch {}
})
</script>

<style scoped>
.profile-page { min-height: calc(100vh - 64px); background: #121212; padding: 32px 24px 60px; }
.page-container { max-width: 1000px; margin: 0 auto; }

.profile-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  align-items: flex-start;
}

/* Left */
.left-col { display: flex; flex-direction: column; gap: 16px; position: sticky; top: 80px; }

.avatar-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 28px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.avatar-wrap { position: relative; display: inline-block; margin-bottom: 4px; }
.profile-avatar { background: #FFB000; color: #121212; font-size: 32px; font-weight: 800; }
.avatar-upload-btn {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 28px;
  height: 28px;
  background: #FFB000;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #121212;
  border: 2px solid #121212;
  transition: all 0.2s;
  &:hover { background: #FFC833; }
}
.profile-name { font-size: 18px; font-weight: 700; color: #E8E8E8; }
.profile-role { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #FFB000; font-weight: 500; }
.profile-email { font-size: 12px; color: #555; }

.stats-card, .balance-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.stats-title { font-size: 14px; font-weight: 600; color: #A0A0A0; margin-bottom: 16px; }
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.stats-item { text-align: center; }
.stats-value { font-size: 22px; font-weight: 700; color: #E8E8E8; }
.stats-label { font-size: 11px; color: #555; margin-top: 2px; }

.balance-header { display: flex; align-items: center; gap: 6px; font-size: 14px; color: #A0A0A0; margin-bottom: 10px; }
.balance-amount { font-size: 32px; font-weight: 800; color: #FFB000; }

/* Right */
.right-col { display: flex; flex-direction: column; gap: 20px; }

.form-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 24px;
}
.form-card-header {
  margin-bottom: 20px;
  h2 { font-size: 17px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

:deep(.el-form-item__label) { color: #C0C0C0 !important; font-size: 13px; }
:deep(.el-input__inner) { color: #E8E8E8 !important; }
:deep(.el-textarea__inner) { background: #2A2A2A !important; color: #E8E8E8 !important; border-color: #333 !important; }

.save-btn { padding: 10px 28px; font-size: 14px; font-weight: 600; }
</style>
