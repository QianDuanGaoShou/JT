import request from './axios'

export const uploadApi = {
  video: (formData, onProgress) => request.post('/upload/video', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (e) => {
      if (onProgress && e.total) {
        onProgress(Math.round((e.loaded / e.total) * 100))
      }
    }
  }),
  image: (formData, onProgress) => request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (e) => {
      if (onProgress && e.total) {
        onProgress(Math.round((e.loaded / e.total) * 100))
      }
    }
  }),
  document: (formData) => request.post('/upload/document', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
