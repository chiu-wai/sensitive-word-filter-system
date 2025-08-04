<template>
  <div class="words-page">
    <!-- 操作栏 -->
    <el-card class="operation-card" shadow="hover">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加敏感词
          </el-button>
          <el-button type="success" @click="showBatchAddDialog">
            <el-icon><Upload /></el-icon>
            批量添加
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchCategory" placeholder="选择分类" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="侮辱" value="侮辱" />
            <el-option label="政治" value="政治" />
            <el-option label="违法" value="违法" />
            <el-option label="default" value="default" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchLevel" placeholder="选择级别" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" @click="reloadDictionary">
            <el-icon><Refresh /></el-icon>
            重新加载字典
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 敏感词列表 -->
    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>敏感词列表</span>
          <span class="total-count">共 {{ total }} 条记录</span>
        </div>
      </template>
      
      <el-table :data="sensitiveWords" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="word" label="敏感词" width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="scope">
            <el-tag :type="getCategoryTagType(scope.row.category)">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100">
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.level)">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showEditDialog(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="敏感词" prop="word">
          <el-input v-model="form.word" placeholder="请输入敏感词" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="侮辱" value="侮辱" />
            <el-option label="政治" value="政治" />
            <el-option label="违法" value="违法" />
            <el-option label="default" value="default" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio :label="1">低</el-radio>
            <el-radio :label="2">中</el-radio>
            <el-radio :label="3">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量添加对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量添加敏感词"
      width="600px"
    >
      <el-form>
        <el-form-item label="敏感词列表">
          <el-input
            v-model="batchWords"
            type="textarea"
            :rows="10"
            placeholder="请输入敏感词，每行一个"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="batchCategory" placeholder="请选择分类">
            <el-option label="侮辱" value="侮辱" />
            <el-option label="政治" value="政治" />
            <el-option label="违法" value="违法" />
            <el-option label="default" value="default" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-radio-group v-model="batchLevel">
            <el-radio :label="1">低</el-radio>
            <el-radio :label="2">中</el-radio>
            <el-radio :label="3">高</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBatchSubmit" :loading="batchSubmitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sensitiveWordApi } from '../utils/api'

export default {
  name: 'Words',
  setup() {
    const loading = ref(false)
    const submitLoading = ref(false)
    const batchSubmitLoading = ref(false)
    const dialogVisible = ref(false)
    const batchDialogVisible = ref(false)
    const dialogTitle = ref('添加敏感词')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchCategory = ref('')
    const searchLevel = ref('')
    const sensitiveWords = ref([])
    const formRef = ref()

    // 表单数据
    const form = reactive({
      id: null,
      word: '',
      category: 'default',
      level: 1,
      status: 1
    })

    // 批量添加数据
    const batchWords = ref('')
    const batchCategory = ref('default')
    const batchLevel = ref(1)

    // 表单验证规则
    const rules = {
      word: [
        { required: true, message: '请输入敏感词', trigger: 'blur' }
      ],
      category: [
        { required: true, message: '请选择分类', trigger: 'change' }
      ],
      level: [
        { required: true, message: '请选择级别', trigger: 'change' }
      ]
    }

    // 加载敏感词列表
    const loadSensitiveWords = async () => {
      loading.value = true
      try {
        const response = await sensitiveWordApi.getByPage(currentPage.value, pageSize.value)
        if (response.data.code === 200) {
          const data = response.data.data
          sensitiveWords.value = data.list
          total.value = data.total
        }
      } catch (error) {
        ElMessage.error('加载敏感词列表失败')
      } finally {
        loading.value = false
      }
    }

    // 显示添加对话框
    const showAddDialog = () => {
      dialogTitle.value = '添加敏感词'
      dialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (row) => {
      dialogTitle.value = '编辑敏感词'
      Object.assign(form, row)
      dialogVisible.value = true
    }

    // 显示批量添加对话框
    const showBatchAddDialog = () => {
      batchDialogVisible.value = true
    }

    // 提交表单
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          submitLoading.value = true
          try {
            let response
            if (form.id) {
              response = await sensitiveWordApi.update(form.id, form)
            } else {
              response = await sensitiveWordApi.add(form)
            }
            
            if (response.data.code === 200) {
              ElMessage.success(form.id ? '更新成功' : '添加成功')
              dialogVisible.value = false
              loadSensitiveWords()
              window.dispatchEvent(new Event('refreshSensitiveWordCount'))
            } else {
              ElMessage.error(response.data.message || '操作失败')
            }
          } catch (error) {
            ElMessage.error('操作失败')
          } finally {
            submitLoading.value = false
          }
        }
      })
    }

    // 批量提交
    const handleBatchSubmit = async () => {
      if (!batchWords.value.trim()) {
        ElMessage.warning('请输入敏感词')
        return
      }

      batchSubmitLoading.value = true
      try {
        const words = batchWords.value.split('\n').filter(word => word.trim())
        const wordList = words.map(word => ({
          word: word.trim(),
          category: batchCategory.value,
          level: batchLevel.value,
          status: 1
        }))

        const response = await sensitiveWordApi.batchAdd(wordList)
        if (response.data.code === 200) {
          ElMessage.success(`批量添加成功，共 ${wordList.length} 个敏感词`)
          batchDialogVisible.value = false
          batchWords.value = ''
          loadSensitiveWords()
          window.dispatchEvent(new Event('refreshSensitiveWordCount'))
        } else {
          ElMessage.error(response.data.message || '批量添加失败')
        }
      } catch (error) {
        ElMessage.error('批量添加失败')
      } finally {
        batchSubmitLoading.value = false
      }
    }

    // 删除敏感词
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(`确定要删除敏感词"${row.word}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await sensitiveWordApi.delete(row.id)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          loadSensitiveWords()
          window.dispatchEvent(new Event('refreshSensitiveWordCount'))
        } else {
          ElMessage.error(response.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    // 状态变更
    const handleStatusChange = async (row) => {
      try {
        const response = await sensitiveWordApi.update(row.id, row)
        if (response.data.code === 200) {
          ElMessage.success('状态更新成功')
        } else {
          ElMessage.error('状态更新失败')
          row.status = row.status === 1 ? 0 : 1 // 恢复原状态
        }
      } catch (error) {
        ElMessage.error('状态更新失败')
        row.status = row.status === 1 ? 0 : 1 // 恢复原状态
      }
    }

    // 重新加载字典
    const reloadDictionary = async () => {
      try {
        const response = await sensitiveWordApi.reloadDictionary()
        if (response.data.code === 200) {
          ElMessage.success('字典重新加载成功')
        } else {
          ElMessage.error('字典重新加载失败')
        }
      } catch (error) {
        ElMessage.error('字典重新加载失败')
      }
    }

    // 搜索
    const handleSearch = () => {
      currentPage.value = 1
      loadSensitiveWords()
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadSensitiveWords()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadSensitiveWords()
    }

    // 重置表单
    const resetForm = () => {
      if (formRef.value) {
        formRef.value.resetFields()
      }
      Object.assign(form, {
        id: null,
        word: '',
        category: 'default',
        level: 1,
        status: 1
      })
    }

    // 工具函数
    const formatTime = (time) => {
      if (!time) return ''
      return new Date(time).toLocaleString()
    }

    const getCategoryTagType = (category) => {
      const types = {
        '侮辱': 'danger',
        '政治': 'warning',
        '违法': 'error',
        'default': 'info'
      }
      return types[category] || 'info'
    }

    const getLevelTagType = (level) => {
      const types = {
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return types[level] || 'info'
    }

    const getLevelText = (level) => {
      const texts = {
        1: '低',
        2: '中',
        3: '高'
      }
      return texts[level] || '未知'
    }

    onMounted(() => {
      loadSensitiveWords()
    })

    return {
      loading,
      submitLoading,
      batchSubmitLoading,
      dialogVisible,
      batchDialogVisible,
      dialogTitle,
      currentPage,
      pageSize,
      total,
      searchCategory,
      searchLevel,
      sensitiveWords,
      form,
      formRef,
      rules,
      batchWords,
      batchCategory,
      batchLevel,
      showAddDialog,
      showEditDialog,
      showBatchAddDialog,
      handleSubmit,
      handleBatchSubmit,
      handleDelete,
      handleStatusChange,
      reloadDictionary,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      resetForm,
      formatTime,
      getCategoryTagType,
      getLevelTagType,
      getLevelText
    }
  }
}
</script>

<style scoped>
.words-page {
  padding: 20px;
}

.operation-card {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .operation-card .el-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .operation-card .el-col {
    width: 100%;
  }
}
</style> 