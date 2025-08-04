<template>
  <div class="home">
    <el-row :gutter="20">
      <!-- 系统概览卡片 -->
      <el-col :span="24">
        <el-card class="overview-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><DataBoard /></el-icon>
              <span>系统概览</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ statistics.sensitiveWordCount || 0 }}</div>
                <div class="stat-label">敏感词总数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ statistics.filterCount || 0 }}</div>
                <div class="stat-label">过滤次数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ statistics.todayFilterCount || 0 }}</div>
                <div class="stat-label">今日过滤</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ statistics.avgFilterTime || 0 }}ms</div>
                <div class="stat-label">平均过滤时间</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 快速过滤 -->
      <el-col :span="12">
        <el-card class="quick-filter-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Filter /></el-icon>
              <span>快速过滤</span>
            </div>
          </template>
          <el-form @submit.prevent="handleQuickFilter">
            <el-form-item>
              <el-input
                v-model="quickFilterText"
                type="textarea"
                :rows="6"
                placeholder="请输入要过滤的文本内容..."
                :disabled="loading"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuickFilter" :loading="loading">
                <el-icon><Filter /></el-icon>
                开始过滤
              </el-button>
              <el-button @click="handleQuickCheck" :loading="loading">
                <el-icon><Search /></el-icon>
                仅检测
              </el-button>
            </el-form-item>
          </el-form>
          
          <!-- 过滤结果 -->
          <div v-if="filterResult" class="filter-result">
            <el-divider content-position="left">过滤结果</el-divider>
            <div class="result-item">
              <strong>原始文本：</strong>
              <div class="text-content">{{ filterResult.originalText }}</div>
            </div>
            <div class="result-item">
              <strong>过滤后文本：</strong>
              <div class="text-content filtered">{{ filterResult.filteredText }}</div>
            </div>
            <div class="result-item">
              <strong>检测到的敏感词：</strong>
              <el-tag 
                v-for="word in filterResult.sensitiveWords" 
                :key="word" 
                type="danger" 
                style="margin-right: 5px;"
              >
                {{ word }}
              </el-tag>
              <span v-if="filterResult.sensitiveWords.length === 0" style="color: #67c23a;">
                未检测到敏感词
              </span>
            </div>
            <div class="result-item">
              <strong>过滤时间：</strong>
              <span>{{ filterResult.filterTime }}ms</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统功能 -->
      <el-col :span="12">
        <el-card class="features-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Grid /></el-icon>
              <span>系统功能</span>
            </div>
          </template>
          <div class="features-grid">
            <div class="feature-item" @click="$router.push('/filter')">
              <el-icon><Filter /></el-icon>
              <h3>文本过滤</h3>
              <p>实时过滤文本中的敏感词，支持批量处理</p>
            </div>
            <div class="feature-item" @click="$router.push('/words')">
              <el-icon><Document /></el-icon>
              <h3>敏感词管理</h3>
              <p>添加、编辑、删除敏感词，支持分类管理</p>
            </div>
            <div class="feature-item" @click="$router.push('/records')">
              <el-icon><List /></el-icon>
              <h3>过滤记录</h3>
              <p>查看历史过滤记录，支持按条件筛选</p>
            </div>
            <div class="feature-item" @click="$router.push('/statistics')">
              <el-icon><TrendCharts /></el-icon>
              <h3>统计分析</h3>
              <p>查看过滤统计数据和趋势分析</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近过滤记录 -->
      <el-col :span="24">
        <el-card class="recent-records-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Clock /></el-icon>
              <span>最近过滤记录</span>
              <el-button 
                type="text" 
                @click="$router.push('/records')"
                style="margin-left: auto;"
              >
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%" v-loading="loading">
            <el-table-column prop="createTime" label="时间" width="180">
              <template #default="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="originalText" label="原始文本" show-overflow-tooltip />
            <el-table-column prop="filteredText" label="过滤后文本" show-overflow-tooltip />
            <el-table-column prop="filterCount" label="敏感词数量" width="100" />
            <el-table-column prop="ipAddress" label="IP地址" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { filterApi, sensitiveWordApi } from '../utils/api'
import { computed } from 'vue'

export default {
  name: 'Home',
  setup() {
    const store = useStore()
    const loading = ref(false)
    const quickFilterText = computed({
      get: () => store.state.quickFilterText,
      set: (value) => store.commit('SET_QUICK_FILTER_TEXT', value)
    })
    const filterResult = ref(null)
    const statistics = ref({})
    const recentRecords = ref([])

    // 快速过滤
    const handleQuickFilter = async () => {
      if (!quickFilterText.value.trim()) {
        ElMessage.warning('请输入要过滤的文本')
        return
      }

      loading.value = true
      try {
        const response = await filterApi.filterText(quickFilterText.value)
        if (response.data.code === 200) {
          filterResult.value = response.data.data
          ElMessage.success('过滤完成')
        } else {
          ElMessage.error(response.data.message || '过滤失败')
        }
      } catch (error) {
        ElMessage.error('过滤失败')
      } finally {
        loading.value = false
      }
    }

    // 快速检测
    const handleQuickCheck = async () => {
      if (!quickFilterText.value.trim()) {
        ElMessage.warning('请输入要检测的文本')
        return
      }

      loading.value = true
      try {
        const response = await filterApi.checkText(quickFilterText.value)
        if (response.data.code === 200) {
          const data = response.data.data
          filterResult.value = {
            originalText: quickFilterText.value,
            filteredText: quickFilterText.value,
            sensitiveWords: data.sensitiveWords,
            filterTime: 0
          }
          ElMessage.success('检测完成')
        } else {
          ElMessage.error(response.data.message || '检测失败')
        }
      } catch (error) {
        ElMessage.error('检测失败')
      } finally {
        loading.value = false
      }
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return ''
      return new Date(time).toLocaleString()
    }

    // 加载数据
    const loadData = async () => {
      loading.value = true
      try {
        // 加载最近记录
        const recordsResponse = await filterApi.getRecords(1, 5)
        if (recordsResponse.data.code === 200) {
          recentRecords.value = recordsResponse.data.data.list
        }
        // 获取敏感词总数
        const wordsResponse = await sensitiveWordApi.getByPage(1, 1)
        let wordCount = 0
        if (wordsResponse.data.code === 200) {
          wordCount = wordsResponse.data.data.total
        }
        statistics.value = {
          sensitiveWordCount: wordCount,
          filterCount: 1250,
          todayFilterCount: 45,
          avgFilterTime: 12
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadData()
      window.addEventListener('refreshSensitiveWordCount', loadData)
    })

    return {
      loading,
      quickFilterText,
      filterResult,
      statistics,
      recentRecords,
      handleQuickFilter,
      handleQuickCheck,
      formatTime
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.overview-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 10px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.quick-filter-card,
.features-card,
.recent-records-card {
  height: 100%;
}

.filter-result {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.result-item {
  margin-bottom: 10px;
}

.result-item strong {
  display: inline-block;
  width: 120px;
  color: #606266;
}

.text-content {
  margin-top: 5px;
  padding: 10px;
  background-color: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 60px;
  word-break: break-all;
}

.text-content.filtered {
  color: #e6a23c;
  font-weight: bold;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.feature-item {
  padding: 20px;
  text-align: center;
  background-color: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-item:hover {
  background-color: #e3f2fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-item .el-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.feature-item h3 {
  margin: 10px 0;
  color: #303133;
}

.feature-item p {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-item {
    margin-bottom: 15px;
  }
}
</style> 