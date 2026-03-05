<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-logo">
        <span class="logo-icon">⚙️</span>
        <span class="logo-text" v-show="!sidebarCollapsed">管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1A1A1A"
        text-color="#A0A0A0"
        active-text-color="#FFB000"
        :collapse="sidebarCollapsed"
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>
        <el-menu-item index="/admin/teachers">
          <el-icon><UserFilled /></el-icon>
          <template #title>教师审核</template>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><Avatar /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/reports">
          <el-icon><TrendCharts /></el-icon>
          <template #title>财务报表</template>
        </el-menu-item>
        <el-divider style="border-color: #333; margin: 8px 0;" />
        <el-menu-item index="/courses">
          <el-icon><ArrowLeft /></el-icon>
          <template #title>返回首页</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-badge" v-show="!sidebarCollapsed">
        <el-icon><Shield /></el-icon>
        <span>系统管理员</span>
      </div>
    </aside>

    <!-- Main -->
    <div class="layout-main" :class="{ expanded: sidebarCollapsed }">
      <header class="top-bar">
        <el-button
          :icon="sidebarCollapsed ? Expand : Fold"
          @click="sidebarCollapsed = !sidebarCollapsed"
          circle
          style="background: #2A2A2A; border-color: #333; color: #A0A0A0;"
        />
        <div style="display:flex;align-items:center;gap:8px;color:#A0A0A0;font-size:13px;">
          <el-icon color="#FFB000"><Shield /></el-icon>
          吉他教学平台 · 管理控制台
        </div>
        <div class="top-bar-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-trigger">
              <el-avatar :size="32" style="background:#FFB000;color:#121212;font-weight:700;">
                {{ authStore.fullName.charAt(0) }}
              </el-avatar>
              <span>{{ authStore.fullName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import {
  Odometer, UserFilled, Avatar, TrendCharts, ArrowLeft,
  Expand, Fold, ArrowDown, Shield
} from '@element-plus/icons-vue'

const route     = useRoute()
const router    = useRouter()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)
const activeMenu = computed(() => route.path)

const handleCommand = async (cmd) => {
  if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'logout') {
    try { await authApi.logout() } catch {}
    authStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #121212;
}

.sidebar {
  width: 220px;
  min-height: 100vh;
  background: #1A1A1A;
  border-right: 1px solid #333;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  transition: width 0.25s ease;
  overflow: hidden;

  &.collapsed {
    width: 64px;
    .sidebar-logo { justify-content: center; }
  }
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  border-bottom: 1px solid #333;

  .logo-icon { font-size: 22px; flex-shrink: 0; }
  .logo-text {
    font-size: 16px;
    font-weight: 700;
    color: #FFB000;
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none !important;
  padding: 8px 0;

  :deep(.el-menu-item) {
    border-radius: 6px;
    margin: 2px 8px;
    &.is-active { background: rgba(255,176,0,0.15) !important; }
    &:hover { background: rgba(255,255,255,0.05) !important; }
  }
}

.sidebar-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px;
  border-top: 1px solid #333;
  font-size: 12px;
  color: #FFB000;
}

.layout-main {
  margin-left: 220px;
  flex: 1;
  transition: margin-left 0.25s ease;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  &.expanded { margin-left: 64px; }
}

.top-bar {
  height: 56px;
  background: #1A1A1A;
  border-bottom: 1px solid #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 99;
}

.top-bar-right { display: flex; align-items: center; gap: 12px; }

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #C0C0C0;
  font-size: 14px;
  padding: 6px 10px;
  border-radius: 6px;
  transition: background 0.2s;
  &:hover { background: rgba(255,255,255,0.05); }
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
