import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  baseURL: process.env.NODE_ENV === 'development' ? '/api' : 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '网络错误'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权访问'
          break
        case 403:
          message = '禁止访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败: ${error.response.status}`
      }
    } else if (error.request) {
      message = '网络连接失败'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 敏感词相关API
export const sensitiveWordApi = {
  // 获取所有启用的敏感词
  getAllEnabled: () => api.get('/sensitive-words/enabled'),
  
  // 分页查询敏感词
  getByPage: (page, size) => api.get('/sensitive-words/page', { params: { page, size } }),
  
  // 根据ID获取敏感词
  getById: (id) => api.get(`/sensitive-words/${id}`),
  
  // 添加敏感词
  add: (word) => api.post('/sensitive-words', word),
  
  // 更新敏感词
  update: (id, word) => api.put(`/sensitive-words/${id}`, word),
  
  // 删除敏感词
  delete: (id) => api.delete(`/sensitive-words/${id}`),
  
  // 批量添加敏感词
  batchAdd: (words) => api.post('/sensitive-words/batch', words),
  
  // 根据分类获取敏感词
  getByCategory: (category) => api.get(`/sensitive-words/category/${category}`),
  
  // 根据级别获取敏感词
  getByLevel: (level) => api.get(`/sensitive-words/level/${level}`),
  
  // 重新加载敏感词字典
  reloadDictionary: () => api.post('/sensitive-words/reload')
}

// 过滤相关API
export const filterApi = {
  // 过滤文本
  filterText: (text) => api.post('/filter/text', { text }),
  
  // 检测文本
  checkText: (text) => api.post('/filter/check', { text }),
  
  // 分页查询过滤记录
  getRecords: (page, size) => api.get('/filter/records', { params: { page, size } }),
  
  // 根据IP地址查询过滤记录
  getRecordsByIp: (ipAddress) => api.get(`/filter/records/ip/${ipAddress}`),
  
  // 根据时间范围查询过滤记录
  getRecordsByTimeRange: (startTime, endTime) => api.get('/filter/records/time', { 
    params: { startTime, endTime } 
  }),
  
  // 清理历史记录
  cleanHistoryRecords: (beforeTime) => api.delete('/filter/records/clean', { 
    params: { beforeTime } 
  })
}

export default api 