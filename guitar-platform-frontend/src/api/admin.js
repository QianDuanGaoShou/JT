import request from './axios'

export const adminApi = {
  // ─── Dashboard ────────────────────────────────────────────
  dashboardStats:    ()             => request.get('/admin/stats/dashboard'),
  platformOverview:  (params)       => request.get('/admin/stats/overview', { params }),

  // ─── Teacher Audit ────────────────────────────────────────
  pendingTeachers:   (params)       => request.get('/admin/teachers/pending', { params }),
  approvedTeachers:  (params)       => request.get('/admin/teachers/approved', { params }),
  approveTeacher:    (id)           => request.post(`/admin/teachers/${id}/approve`),
  rejectTeacher:     (id, data)     => request.post(`/admin/teachers/${id}/reject`, data),
  teacherDetail:     (id)           => request.get(`/admin/teachers/${id}`),

  // ─── User Management ──────────────────────────────────────
  users:             (params)       => request.get('/admin/users', { params }),
  userDetail:        (id)           => request.get(`/admin/users/${id}`),
  toggleUserStatus:  (id)           => request.put(`/admin/users/${id}/toggle-status`),
  rechargeBalance:   (id, data)     => request.post(`/admin/users/${id}/recharge`, data),
  deleteUser:        (id)           => request.delete(`/admin/users/${id}`),

  // ─── Financial Reports ────────────────────────────────────
  financialReport:   (params)       => request.get('/admin/finance/report', { params }),
  monthlySettlement: (params)       => request.get('/admin/finance/settlement', { params }),
  platformRevenue:   (params)       => request.get('/admin/finance/revenue', { params }),

  // ─── Content Management ───────────────────────────────────
  allCourses:        (params)       => request.get('/admin/courses', { params }),
  toggleCourseStatus:(id)           => request.put(`/admin/courses/${id}/toggle-status`),

  // ─── Activity Logs ────────────────────────────────────────
  recentActivities:  (params)       => request.get('/admin/activities', { params })
}
