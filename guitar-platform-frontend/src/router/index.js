import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

// ─── Lazy-loaded views ────────────────────────────────────────
const LoginView     = () => import('@/views/LoginView.vue')
const RegisterView  = () => import('@/views/RegisterView.vue')

// Student
const CourseList    = () => import('@/views/student/CourseList.vue')
const CourseDetail  = () => import('@/views/student/CourseDetail.vue')
const MyCourses     = () => import('@/views/student/MyCourses.vue')
const PracticeList  = () => import('@/views/student/PracticeList.vue')
const PracticeReport= () => import('@/views/student/PracticeReport.vue')
const Profile       = () => import('@/views/student/Profile.vue')

// Teacher
const TeacherLayout   = () => import('@/layouts/TeacherLayout.vue')
const TeacherDashboard= () => import('@/views/teacher/TeacherDashboard.vue')
const TeacherCourses  = () => import('@/views/teacher/TeacherCourses.vue')
const CourseEdit      = () => import('@/views/teacher/CourseEdit.vue')
const PracticeGrade   = () => import('@/views/teacher/PracticeGrade.vue')

// Admin
const AdminLayout       = () => import('@/layouts/AdminLayout.vue')
const AdminDashboard    = () => import('@/views/admin/AdminDashboard.vue')
const TeacherAudit      = () => import('@/views/admin/TeacherAudit.vue')
const UserManagement    = () => import('@/views/admin/UserManagement.vue')
const FinancialReports  = () => import('@/views/admin/FinancialReports.vue')

// ─── Main App Layout ──────────────────────────────────────────
const AppLayout = () => import('@/layouts/AppLayout.vue')

const routes = [
  // ── Public ────────────────────────────────────────────────
  { path: '/login',    name: 'Login',    component: LoginView,    meta: { public: true } },
  { path: '/register', name: 'Register', component: RegisterView, meta: { public: true } },

  // ── Redirect root ─────────────────────────────────────────
  { path: '/', redirect: '/courses' },

  // ── Student / Shared Layout ───────────────────────────────
  {
    path: '/',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'courses',                    name: 'CourseList',    component: CourseList },
      { path: 'courses/:id',                name: 'CourseDetail',  component: CourseDetail },
      { path: 'my-courses',                 name: 'MyCourses',     component: MyCourses,  meta: { role: 'STUDENT' } },
      { path: 'practice',                   name: 'PracticeList',  component: PracticeList, meta: { role: 'STUDENT' } },
      { path: 'practice/:id/report',        name: 'PracticeReport',component: PracticeReport },
      { path: 'profile',                    name: 'Profile',       component: Profile }
    ]
  },

  // ── Teacher Layout ────────────────────────────────────────
  {
    path: '/teacher',
    component: TeacherLayout,
    meta: { requiresAuth: true, role: 'TEACHER' },
    children: [
      { path: '',          redirect: '/teacher/dashboard' },
      { path: 'dashboard', name: 'TeacherDashboard', component: TeacherDashboard },
      { path: 'courses',   name: 'TeacherCourses',   component: TeacherCourses },
      { path: 'course/create', name: 'CourseCreate', component: CourseEdit },
      { path: 'course/:id/edit', name: 'CourseEdit', component: CourseEdit },
      { path: 'practice',  name: 'PracticeGrade',    component: PracticeGrade }
    ]
  },

  // ── Admin Layout ──────────────────────────────────────────
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '',           redirect: '/admin/dashboard' },
      { path: 'dashboard',  name: 'AdminDashboard',   component: AdminDashboard },
      { path: 'teachers',   name: 'TeacherAudit',     component: TeacherAudit },
      { path: 'users',      name: 'UserManagement',   component: UserManagement },
      { path: 'reports',    name: 'FinancialReports',  component: FinancialReports }
    ]
  },

  // ── 404 ───────────────────────────────────────────────────
  { path: '/:pathMatch(.*)*', redirect: '/courses' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// ─── Navigation Guard ─────────────────────────────────────────
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Public routes — always allow
  if (to.meta.public) {
    // Redirect logged-in users away from login/register
    if (authStore.isLoggedIn) {
      return next('/courses')
    }
    return next()
  }

  // Protected routes — must be logged in
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // Role-based access
  if (to.meta.role) {
    const userRole = authStore.userInfo?.role
    if (userRole !== to.meta.role) {
      // Redirect to appropriate home based on role
      if (userRole === 'ADMIN')   return next('/admin/dashboard')
      if (userRole === 'TEACHER') return next('/teacher/dashboard')
      return next('/courses')
    }
  }

  next()
})

export default router
