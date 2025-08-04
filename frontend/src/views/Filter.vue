<template>
  <div class="filter-page">
    <el-row :gutter="20">
      <!-- 文本输入区域 -->
      <el-col :span="12">
        <el-card class="input-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Edit /></el-icon>
              <span>文本输入</span>
            </div>
          </template>
          
          <el-form>
            <el-form-item>
              <el-input
                v-model="inputText"
                type="textarea"
                :rows="15"
                placeholder="请输入要过滤的文本内容..."
                :disabled="loading"
                show-word-limit
                maxlength="5000"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleFilter" :loading="loading">
                <el-icon><Filter /></el-icon>
                开始过滤
              </el-button>
              <el-button @click="handleCheck" :loading="loading">
                <el-icon><Search /></el-icon>
                仅检测
              </el-button>
              <el-button @click="handleClear">
                <el-icon><Delete /></el-icon>
                清空
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 过滤结果区域 -->
      <el-col :span="12">
        <el-card class="result-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>过滤结果</span>
              <el-button 
                v-if="filterResult" 
                type="text" 
                @click="copyResult"
                style="margin-left: auto;"
              >
                <el-icon><CopyDocument /></el-icon>
                复制结果
              </el-button>
            </div>
          </template>
          
          <div v-if="filterResult" class="result-content">
            <div class="result-section">
              <h4>原始文本</h4>
              <div class="text-display">{{ filterResult.originalText }}</div>
            </div>
            
            <div class="result-section">
              <h4>过滤后文本</h4>
              <div class="text-display filtered">{{ filterResult.filteredText }}</div>
            </div>
            
            <div class="result-section">
              <h4>检测结果</h4>
              <div class="detection-result">
                <div class="result-item">
                  <span class="label">是否包含敏感词：</span>
                  <el-tag :type="filterResult.hasSensitiveWord ? 'danger' : 'success'">
                    {{ filterResult.hasSensitiveWord ? '是' : '否' }}
                  </el-tag>
                </div>
                
                <div class="result-item">
                  <span class="label">敏感词数量：</span>
                  <span class="value">{{ filterResult.sensitiveWordCount }}</span>
                </div>
                
                <div class="result-item">
                  <span class="label">过滤时间：</span>
                  <span class="value">{{ filterResult.filterTime }}ms</span>
                </div>
              </div>
            </div>
            
            <div v-if="filterResult.sensitiveWords && filterResult.sensitiveWords.length > 0" class="result-section">
              <h4>检测到的敏感词</h4>
              <div class="sensitive-words">
                <el-tag 
                  v-for="word in filterResult.sensitiveWords" 
                  :key="word" 
                  type="danger" 
                  effect="dark"
                  style="margin: 5px;"
                >
                  {{ word }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-result">
            <el-empty description="暂无过滤结果" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 批量处理区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="batch-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Files /></el-icon>
              <span>批量处理</span>
            </div>
          </template>
          
          <el-form>
            <el-form-item>
              <el-upload
                ref="uploadRef"
                :auto-upload="false"
                :on-change="handleFileChange"
                :show-file-list="false"
                accept=".txt,.csv"
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  选择文件
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持 .txt 和 .csv 格式文件，每行一个文本
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            
            <el-form-item v-if="batchTexts.length > 0">
              <div class="batch-preview">
                <h4>文件预览（共 {{ batchTexts.length }} 行）</h4>
                <el-input
                  :value="batchTexts.join('\n')"
                  type="textarea"
                  :rows="6"
                  readonly
                />
              </div>
              
              <el-button type="success" @click="handleBatchFilter" :loading="batchLoading">
                <el-icon><Filter /></el-icon>
                批量过滤
              </el-button>
              <el-button @click="clearBatch">
                <el-icon><Delete /></el-icon>
                清空
              </el-button>
            </el-form-item>
          </el-form>
          
          <!-- 批量处理结果 -->
          <div v-if="batchResults.length > 0" class="batch-results">
            <el-divider content-position="left">批量处理结果</el-divider>
            <el-table :data="batchResults" style="width: 100%">
              <el-table-column prop="index" label="序号" width="80" />
              <el-table-column prop="originalText" label="原始文本" show-overflow-tooltip />
              <el-table-column prop="filteredText" label="过滤后文本" show-overflow-tooltip />
              <el-table-column prop="hasSensitiveWord" label="包含敏感词" width="120">
                <template #default="scope">
                  <el-tag :type="scope.row.hasSensitiveWord ? 'danger' : 'success'">
                    {{ scope.row.hasSensitiveWord ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="sensitiveWordCount" label="敏感词数量" width="120" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { filterApi } from '../utils/api'
import { useStore } from 'vuex'
import { computed } from 'vue'

export default {
  name: 'SensitiveFilter',
  setup() {
    const store = useStore()
    const loading = ref(false)
    const batchLoading = ref(false)
    const inputText = computed({
      get: () => store.state.filterInputText,
      set: (value) => store.commit('SET_FILTER_INPUT_TEXT', value)
    })
    const filterResult = ref(null)
    const batchTexts = ref([])
    const batchResults = ref([])
    const uploadRef = ref()

    // 过滤文本
    const handleFilter = async () => {
      if (!inputText.value.trim()) {
        ElMessage.warning('请输入要过滤的文本')
        return
      }
      loading.value = true
      try {
        const response = await filterApi.filterText(inputText.value)
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

    // 检测文本
    const handleCheck = async () => {
      if (!inputText.value.trim()) {
        ElMessage.warning('请输入要检测的文本')
        return
      }
      loading.value = true
      try {
        const response = await filterApi.checkText(inputText.value)
        if (response.data.code === 200) {
          const data = response.data.data
          filterResult.value = {
            originalText: inputText.value,
            filteredText: inputText.value,
            sensitiveWords: data.sensitiveWords,
            hasSensitiveWord: data.containsSensitiveWord,
            sensitiveWordCount: data.sensitiveWordCount,
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

    // 清空输入
    const handleClear = () => {
      inputText.value = ''
      filterResult.value = null
    }

    // 复制结果
    const copyResult = () => {
      if (filterResult.value) {
        navigator.clipboard.writeText(filterResult.value.filteredText).then(() => {
          ElMessage.success('已复制到剪贴板')
        }).catch(() => {
          ElMessage.error('复制失败')
        })
      }
    }

    // 文件上传处理
    const handleFileChange = (file) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        const content = e.target.result
        batchTexts.value = content.split('\n').filter(line => line.trim())
        ElMessage.success(`成功加载 ${batchTexts.value.length} 行文本`)
      }
      reader.readAsText(file.raw)
    }

    // 批量过滤
    const handleBatchFilter = async () => {
      if (batchTexts.value.length === 0) {
        ElMessage.warning('请先选择文件')
        return
      }

      batchLoading.value = true
      batchResults.value = []

      try {
        for (let i = 0; i < batchTexts.value.length; i++) {
          const text = batchTexts.value[i]
          if (text.trim()) {
            const response = await filterApi.filterText(text)
            if (response.code === 200) {
              batchResults.value.push({
                index: i + 1,
                originalText: text,
                filteredText: response.data.filteredText,
                hasSensitiveWord: response.data.hasSensitiveWord,
                sensitiveWordCount: response.data.sensitiveWordCount
              })
            }
          }
        }
        ElMessage.success(`批量处理完成，共处理 ${batchResults.value.length} 条文本`)
      } catch (error) {
        ElMessage.error('批量处理失败')
      } finally {
        batchLoading.value = false
      }
    }

    // 清空批量数据
    const clearBatch = () => {
      batchTexts.value = []
      batchResults.value = []
      if (uploadRef.value) {
        uploadRef.value.clearFiles()
      }
    }

    return {
      loading,
      batchLoading,
      inputText,
      filterResult,
      batchTexts,
      batchResults,
      uploadRef,
      handleFilter,
      handleCheck,
      handleClear,
      copyResult,
      handleFileChange,
      handleBatchFilter,
      clearBatch
    }
  }
}
</script>

<style scoped>
.filter-page {
  padding: 20px;
}

.input-card,
.result-card,
.batch-card {
  height: 100%;
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

.result-content {
  height: 100%;
}

.result-section {
  margin-bottom: 20px;
}

.result-section h4 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.text-display {
  padding: 10px;
  background-color: #f8f9fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 80px;
  word-break: break-all;
  line-height: 1.6;
}

.text-display.filtered {
  color: #e6a23c;
  font-weight: bold;
}

.detection-result {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
}

.result-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.result-item:last-child {
  margin-bottom: 0;
}

.result-item .label {
  width: 120px;
  color: #606266;
  font-weight: 500;
}

.result-item .value {
  color: #303133;
  font-weight: bold;
}

.sensitive-words {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  min-height: 60px;
}

.empty-result {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.batch-preview {
  margin-bottom: 15px;
}

.batch-preview h4 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.batch-results {
  margin-top: 20px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

@media (max-width: 768px) {
  .result-item .label {
    width: 100px;
  }
}
</style> 