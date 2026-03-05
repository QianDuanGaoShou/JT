import request from './axios'

export const practiceApi = {
  submit: (formData) => request.post('/practice/submit', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  list:   (params)  => request.get('/practice/list', { params }),
  report: (id)      => request.get(`/practice/${id}/report`),
  detail: (id)      => request.get(`/practice/${id}`)
}
