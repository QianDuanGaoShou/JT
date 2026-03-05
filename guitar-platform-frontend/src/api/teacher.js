import request from './axios'

export const teacherApi = {
  // ─── Courses ──────────────────────────────────────────────
  createCourse:  (data)        => request.post('/teacher/courses', data),
  updateCourse:  (id, data)    => request.put(`/teacher/courses/${id}`, data),
  deleteCourse:  (id)          => request.delete(`/teacher/courses/${id}`),
  myCourses:     (params)      => request.get('/teacher/courses', { params }),
  publishCourse: (id)          => request.post(`/teacher/courses/${id}/publish`),
  unpublishCourse:(id)         => request.post(`/teacher/courses/${id}/unpublish`),
  courseStudents:(id, params)  => request.get(`/teacher/courses/${id}/students`, { params }),

  // ─── Chapters ─────────────────────────────────────────────
  addChapter:    (data)        => request.post('/teacher/chapters', data),
  updateChapter: (id, data)    => request.put(`/teacher/chapters/${id}`, data),
  deleteChapter: (id)          => request.delete(`/teacher/chapters/${id}`),
  reorderChapters:(data)       => request.put('/teacher/chapters/reorder', data),

  // ─── Videos ───────────────────────────────────────────────
  addVideo:      (data)        => request.post('/teacher/videos', data),
  updateVideo:   (id, data)    => request.put(`/teacher/videos/${id}`, data),
  deleteVideo:   (id)          => request.delete(`/teacher/videos/${id}`),
  reorderVideos: (data)        => request.put('/teacher/videos/reorder', data),

  // ─── Practice Grading ─────────────────────────────────────
  pendingPractices: (params)   => request.get('/teacher/practices/pending', { params }),
  gradedPractices:  (params)   => request.get('/teacher/practices/graded', { params }),
  gradePractice:    (id, data) => request.post(`/teacher/practices/${id}/grade`, data),

  // ─── Income / Stats ───────────────────────────────────────
  incomeStats:   (params)      => request.get('/teacher/stats/income', { params }),
  dashboardStats:()            => request.get('/teacher/stats/dashboard')
}
