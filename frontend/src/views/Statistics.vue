<template>
  <div class="statistics-page">
    <!-- 统计概览 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.totalWords || 0 }}</div>
              <div class="stat-label">敏感词总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Filter /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.totalFilters || 0 }}</div>
              <div class="stat-label">总过滤次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.todayFilters || 0 }}</div>
              <div class="stat-label">今日过滤</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.avgTime || 0 }}ms</div>
              <div class="stat-label">平均过滤时间</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>过滤趋势</span>
              <el-select v-model="trendPeriod" size="small" @change="loadTrendData">
                <el-option label="最近7天" value="7" />
                <el-option label="最近30天" value="30" />
                <el-option label="最近90天" value="90" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="trendOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>敏感词分类统计</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="categoryOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>敏感词级别分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="levelOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>过滤时间分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="timeOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门敏感词 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="hot-words-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热门敏感词排行</span>
            </div>
          </template>
          <el-table :data="hotWords" style="width: 100%" v-loading="loading">
            <el-table-column prop="rank" label="排名" width="80">
              <template #default="scope">
                <el-tag :type="getRankTagType(scope.row.rank)">
                  {{ scope.row.rank }}
                </el-tag>
              </template>
            </el-table-column>
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
            <el-table-column prop="count" label="出现次数" width="120" />
            <el-table-column prop="percentage" label="占比" width="120">
              <template #default="scope">
                <el-progress :percentage="scope.row.percentage" :stroke-width="8" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

export default {
  name: 'Statistics',
  components: {
    VChart
  },
  setup() {
    const loading = ref(false)
    const trendPeriod = ref('7')
    const statistics = ref({})
    const hotWords = ref([])

    // 过滤趋势图表配置
    const trendOption = ref({
      title: {
        text: '过滤趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '过滤次数',
          type: 'line',
          data: [],
          smooth: true,
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    })

    // 分类统计图表配置
    const categoryOption = ref({
      title: {
        text: '敏感词分类统计',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '分类',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 10, name: '侮辱' },
            { value: 8, name: '政治' },
            { value: 10, name: '违法' }
          ]
        }
      ]
    })

    // 级别分布图表配置
    const levelOption = ref({
      title: {
        text: '敏感词级别分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '级别',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 15, name: '低' },
            { value: 8, name: '中' },
            { value: 5, name: '高' }
          ]
        }
      ]
    })

    // 过滤时间分布图表配置
    const timeOption = ref({
      title: {
        text: '过滤时间分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['0-10ms', '10-50ms', '50-100ms', '100-500ms', '500ms+']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '次数',
          type: 'bar',
          data: [120, 80, 45, 25, 10],
          itemStyle: {
            color: '#67C23A'
          }
        }
      ]
    })

    // 加载统计数据
    const loadStatistics = () => {
      // 模拟统计数据
      statistics.value = {
        totalWords: 28,
        totalFilters: 1250,
        todayFilters: 45,
        avgTime: 12
      }
    }

    // 加载趋势数据
    const loadTrendData = () => {
      // 模拟趋势数据
      const days = []
      const data = []
      const period = parseInt(trendPeriod.value)
      
      for (let i = period - 1; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        days.push(date.toLocaleDateString())
        data.push(Math.floor(Math.random() * 50) + 10)
      }
      
      trendOption.value.xAxis.data = days
      trendOption.value.series[0].data = data
    }

    // 加载热门敏感词
    const loadHotWords = () => {
      hotWords.value = [
        { rank: 1, word: '傻逼', category: '侮辱', level: 3, count: 45, percentage: 15 },
        { rank: 2, word: '混蛋', category: '侮辱', level: 2, count: 38, percentage: 12 },
        { rank: 3, word: '赌博', category: '违法', level: 2, count: 32, percentage: 10 },
        { rank: 4, word: '毒品', category: '违法', level: 3, count: 28, percentage: 9 },
        { rank: 5, word: '色情', category: '违法', level: 3, count: 25, percentage: 8 }
      ]
    }

    // 工具函数
    const getRankTagType = (rank) => {
      if (rank <= 3) return 'danger'
      if (rank <= 10) return 'warning'
      return 'info'
    }

    const getCategoryTagType = (category) => {
      const types = {
        '侮辱': 'danger',
        '政治': 'warning',
        '违法': 'error'
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
      loadStatistics()
      loadTrendData()
      loadHotWords()
    })

    return {
      loading,
      trendPeriod,
      statistics,
      hotWords,
      trendOption,
      categoryOption,
      levelOption,
      timeOption,
      loadTrendData,
      getRankTagType,
      getCategoryTagType,
      getLevelTagType,
      getLevelText
    }
  }
}
</script>

<style scoped>
.statistics-page {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon .el-icon {
  font-size: 30px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-container {
  padding: 10px;
}

.hot-words-card {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style> 