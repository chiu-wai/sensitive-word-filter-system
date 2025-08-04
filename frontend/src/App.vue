<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="header-content">
          <h1 class="app-title">
            <el-icon><Shield /></el-icon>
            敏感词过滤系统
          </h1>
          <div class="header-nav">
            <el-menu
              :default-active="$route.path"
              mode="horizontal"
              router
              background-color="#409EFF"
              text-color="#fff"
              active-text-color="#ffd04b"
            >
              <el-menu-item index="/">
                <el-icon><HomeFilled /></el-icon>
                首页
              </el-menu-item>
              <el-menu-item index="/filter">
                <el-icon><Filter /></el-icon>
                文本过滤
              </el-menu-item>
              <el-menu-item index="/words">
                <el-icon><Document /></el-icon>
                敏感词管理
              </el-menu-item>
              <el-menu-item index="/records">
                <el-icon><List /></el-icon>
                过滤记录
              </el-menu-item>
              <el-menu-item index="/statistics">
                <el-icon><TrendCharts /></el-icon>
                统计分析
              </el-menu-item>
            </el-menu>
          </div>
        </div>
      </el-header>
      
      <el-main class="app-main">
        <router-view />
      </el-main>
      
      <el-footer class="app-footer">
        <p>&copy; 2024 敏感词过滤系统. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
}

.app-container {
  height: 100vh;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.app-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.app-title .el-icon {
  margin-right: 10px;
  font-size: 28px;
}

.header-nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-nav .el-menu {
  border: none;
}

.header-nav .el-menu-item {
  font-size: 16px;
  font-weight: 500;
}

.app-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.app-footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 15px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    padding: 10px;
  }
  
  .app-title {
    font-size: 20px;
    margin-bottom: 10px;
  }
  
  .header-nav {
    width: 100%;
  }
  
  .header-nav .el-menu {
    justify-content: center;
  }
  
  .header-nav .el-menu-item {
    font-size: 14px;
    padding: 0 10px;
  }
}
</style> 