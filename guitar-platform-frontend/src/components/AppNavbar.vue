<template>
  <nav class="app-navbar">
    <div class="navbar-inner">
      <!-- Logo -->
      <div class="navbar-logo" @click="goHome">
        <span class="logo-emoji">🎸</span>
        <span class="logo-text">GuitarHub</span>
      </div>

      <!-- Navigation Links -->
      <div class="navbar-links">
        <router-link to="/courses" class="nav-link" active-class="active">课程广场</router-link>

        <template v-if="authStore.isLoggedIn">
          <router-link
            v-if="authStore.isStudent"
            to="/my-courses"
            class="nav-link"
            active-class="active"
          >
            我的课程
          </router-link>
          <router-link
            v-if="authStore.isStudent"
            to="/practice"
            class="nav-link"
            active-class="active"
          >
            练习记录
          </router-link>

          <!-- Teacher Nav -->
          <router-link
            v-if="authStore.isTeacher"
            to="/teacher/dashboard"
            class="nav-link"
            active-class="active"
          >
            教师中心
          </router-link>

          <!-- Admin Nav -->
          <router-link
            v-if="authStore.isAdmin"
            to="/admin/dashboard"
            class="nav-link admin-link"
            active-class="active"
          >
            <el-icon size="13"><Shield /></el-icon>
            管理后台
          </router-link>
        </template>
      </div>

      <!-- Right Actions -->
      <div class="navbar-right">
        <template v-if="authStore.isLoggedIn">
          <!-- Notification Bell (placeholder) -->
          <el-badge :value="notifCount" :max="9" :hidden="!notifCount" class="notif-badge">
            <el-button
              circle
              style="background:transparent;border-color:#2A2A2A;color:#A0A0A0;width:36px;height:36px;"
            >
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>

          <!-- User Dropdown -->
          <el-dropdown trigger="click" @command="handleCommand" :teleported="false">
            <div class="user-trigger">
              <el-avatar
                :size="32"
                :src="authStore.avatar"
                class="user-avatar"
              >
                {{ authStore.fullName.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ authStore.fullName }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-menu">
                <div class="menu-user-info">
                  <el-avatar :size="40" :src="authStore.avatar" class="menu-avatar">
                    {{ authStore.fullName.charAt(0) }}
                  </el-avatar>
                  <div>
                    <div class="menu-name">{{ authStore.fullName }}</div>
                    <div class="menu-role">{{ roleLabels[authStore.userInfo?.role] }}</div>
                  </div>
                </div>
                <el-divider style="margin:6px 0;border-color:#2A2A2A;" />
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人资料
                </el-dropdown-item>
                <el-dropdown-item v-if="authStore.isStudent" command="my-courses">
                  <el-icon><VideoPlay /></el-icon> 我的课程
                </el-dropdown-item>
                <el-dropdown-item v-if="authStore.isTeacher" command="teacher">
                  <el-icon><DataLine /></el-icon> 教师控制台
                </el-dropdown-item>
                <el-dropdown-item v-if="authStore.isAdmin" command="admin">
                  <el-icon><Setting /></el-icon> 管理后台
                </el-dropdown-item>
                <el-divider style="margin:6px 0;border-color:#2A2A2A;" />
                <el-dropdown-item command="logout" style="color:#FF4D4F;">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <!-- Not logged in -->
        <template v-else>
          <router-link to="/login">
            <el-button style="background:transparent;border-color:#333;color:#A0A0A0;" size="small">
              登录
            </el-button>
          </router-link>
          <router-link to="/register">
            <el-button type="primary" class="btn-gradient" size="small" style="margin-left:8px;">
              注册
            </el-button>
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowDown, User, VideoPlay, DataLine, Setting,
  SwitchButton, Bell, Shield
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'

const router    = useRouter()
const authStore = useAuthStore()

const notifCount = ref(0)
const roleLabels = { STUDENT: '学员', TEACHER: '认证教师', ADMIN: '管理员' }

const goHome = () => router.push('/courses')

const handleCommand = async (cmd) => {
  switch (cmd) {
    case 'profile':    return router.push('/profile')
    case 'my-courses': return router.push('/my-courses')
    case 'teacher':    return router.push('/teacher/dashboard')
    case 'admin':      return router.push('/admin/dashboard')
    case 'logout': {
      try { await authApi.logout() } catch {}
      authStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
      break
    }
  }
}
</script>

<style scoped>
.app-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;
  background: rgba(26, 26, 26, 0.95);
  border-bottom: 1px solid #2A2A2A;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.navbar-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 32px;
}

/* Logo */
.navbar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
  text-decoration: none;
}
.logo-emoji { font-size: 22px; }
.logo-text {
  font-size: 18px;
  font-weight: 800;
  background: linear-gradient(135deg, #FFB000, #FFC833);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

/* Nav Links */
.navbar-links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #A0A0A0;
  text-decoration: none;
  transition: all 0.2s;
  font-weight: 500;

  &:hover { color: #E8E8E8; background: rgba(255,255,255,0.06); }
  &.active { color: #FFB000; background: rgba(255,176,0,0.08); }
}

.admin-link {
  color: #FF6B35;
  &:hover { color: #FF8555; background: rgba(255,107,53,0.08); }
  &.active { color: #FF6B35; background: rgba(255,107,53,0.1); }
}

/* Right */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
  flex-shrink: 0;
}

.notif-badge { cursor: pointer; }

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  border-radius: 100px;
  cursor: pointer;
  border: 1px solid #2A2A2A;
  background: transparent;
  transition: all 0.2s;

  &:hover { border-color: #404040; background: rgba(255,255,255,0.04); }
}
.user-avatar { background: #FFB000; color: #121212; font-weight: 700; flex-shrink: 0; }
.user-name { font-size: 13px; color: #C0C0C0; max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.arrow-icon { color: #666; font-size: 12px; }

/* Dropdown Menu */
:deep(.user-menu) {
  background: #1E1E1E !important;
  border: 1px solid #2A2A2A !important;
  border-radius: 10px !important;
  padding: 4px !important;
  min-width: 200px;
}

.menu-user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
}
.menu-avatar { background: #FFB000; color: #121212; font-weight: 700; }
.menu-name   { font-size: 14px; font-weight: 600; color: #E8E8E8; }
.menu-role   { font-size: 12px; color: #FFB000; margin-top: 2px; }

:deep(.el-dropdown-menu__item) {
  border-radius: 6px !important;
  color: #C0C0C0 !important;
  font-size: 14px;
  &:hover { background: rgba(255,255,255,0.05) !important; color: #E8E8E8 !important; }
}
</style>
