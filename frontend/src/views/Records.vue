<template>
  <div class="records-page">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="hover">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchIp"
            placeholder="搜索IP地址"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Location /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="8">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleSearch"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="danger" @click="showCleanDialog">
            <el-icon><Delete /></el-icon>
            清理记录
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 记录列表 -->
    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>过滤记录</span>
          <span class="total-count">共 {{ total }} 条记录</span>
        </div>
      </template>
      
      <el-table :data="records" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="originalText" label="原始文本" show-overflow-tooltip />
        <el-table-column prop="filteredText" label="过滤后文本" show-overflow-tooltip />
        <el-table-column prop="filterCount" label="敏感词数量" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.filterCount > 0 ? 'danger' : 'success'">
              {{ scope.row.filterCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="userAgent" label="用户代理" width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showDetailDialog(scope.row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="过滤记录详情"
      width="800px"
    >
      <div v-if="currentRecord" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ currentRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(currentRecord.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentRecord.ipAddress }}</el-descriptions-item>
          <el-descriptions-item label="敏感词数量">{{ currentRecord.filterCount }}</el-descriptions-item>
          <el-descriptions-item label="用户代理" :span="2">{{ currentRecord.userAgent }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">文本内容</el-divider>
        
        <div class="text-section">
          <h4>原始文本</h4>
          <div class="text-content">{{ currentRecord.originalText }}</div>
        </div>
        
        <div class="text-section">
          <h4>过滤后文本</h4>
          <div class="text-content filtered">{{ currentRecord.filteredText }}</div>
        </div>
        
        <div v-if="currentRecord.sensitiveWords" class="text-section">
          <h4>检测到的敏感词</h4>
          <div class="sensitive-words">
            <el-tag 
              v-for="word in parseSensitiveWords(currentRecord.sensitiveWords)" 
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
    </el-dialog>

    <!-- 清理记录对话框 -->
    <el-dialog
      v-model="cleanDialogVisible"
      title="清理历史记录"
      width="500px"
    >
      <el-form>
        <el-form-item label="清理时间">
          <el-date-picker
            v-model="cleanBeforeTime"
            type="datetime"
            placeholder="选择时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-alert
            title="警告"
            type="warning"
            description="将删除指定时间之前的所有过滤记录，此操作不可恢复！"
            show-icon
            :closable="false"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cleanDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleCleanRecords" :loading="cleanLoading">
            确定清理
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { filterApi } from '../utils/api'

export default {
  name: 'Records',
  setup() {
    const loading = ref(false)
    const cleanLoading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchIp = ref('')
    const dateRange = ref([])
    const records = ref([])
    const detailDialogVisible = ref(false)
    const cleanDialogVisible = ref(false)
    const currentRecord = ref(null)
    const cleanBeforeTime = ref('')

    // 加载记录列表
    const loadRecords = async () => {
      loading.value = true
      try {
        const response = await filterApi.getRecords(currentPage.value, pageSize.value)
        if (response.data.code === 200) {
          const data = response.data.data
          records.value = data.list
          total.value = data.total
        }
      } catch (error) {
        ElMessage.error('加载记录列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      currentPage.value = 1
      loadRecords()
    }

    // 显示详情对话框
    const showDetailDialog = (record) => {
      currentRecord.value = record
      detailDialogVisible.value = true
    }

    // 显示清理对话框
    const showCleanDialog = () => {
      cleanDialogVisible.value = true
    }

    // 清理记录
    const handleCleanRecords = async () => {
      if (!cleanBeforeTime.value) {
        ElMessage.warning('请选择清理时间')
        return
      }

      try {
        await ElMessageBox.confirm(
          `确定要删除 ${cleanBeforeTime.value} 之前的所有记录吗？此操作不可恢复！`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        cleanLoading.value = true
        const response = await filterApi.cleanHistoryRecords(cleanBeforeTime.value)
        if (response.data.code === 200) {
          ElMessage.success('清理成功')
          cleanDialogVisible.value = false
          cleanBeforeTime.value = ''
          loadRecords()
        } else {
          ElMessage.error(response.data.message || '清理失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('清理失败')
        }
      } finally {
        cleanLoading.value = false
      }
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadRecords()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadRecords()
    }

    // 工具函数
    const formatTime = (time) => {
      if (!time) return ''
      return new Date(time).toLocaleString()
    }

    const parseSensitiveWords = (wordsStr) => {
      try {
        return JSON.parse(wordsStr)
      } catch (error) {
        return []
      }
    }

    onMounted(() => {
      loadRecords()
    })

    return {
      loading,
      cleanLoading,
      currentPage,
      pageSize,
      total,
      searchIp,
      dateRange,
      records,
      detailDialogVisible,
      cleanDialogVisible,
      currentRecord,
      cleanBeforeTime,
      loadRecords,
      handleSearch,
      showDetailDialog,
      showCleanDialog,
      handleCleanRecords,
      handleSizeChange,
      handleCurrentChange,
      formatTime,
      parseSensitiveWords
    }
  }
}
</script>

<style scoped>
.records-page {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.total-count {
  color: #909399;
  font-size: 14px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.detail-content {
  max-height: 600px;
  overflow-y: auto;
}

.text-section {
  margin-bottom: 20px;
}

.text-section h4 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.text-content {
  padding: 15px;
  background-color: #f8f9fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 80px;
  word-break: break-all;
  line-height: 1.6;
}

.text-content.filtered {
  color: #e6a23c;
  font-weight: bold;
}

.sensitive-words {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  min-height: 60px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .search-card .el-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .search-card .el-col {
    width: 100%;
  }
}
</style> 