import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Filter from '../views/Filter.vue'
import Words from '../views/Words.vue'
import Records from '../views/Records.vue'
import Statistics from '../views/Statistics.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/filter',
    name: 'Filter',
    component: Filter,
    meta: { title: '文本过滤' }
  },
  {
    path: '/words',
    name: 'Words',
    component: Words,
    meta: { title: '敏感词管理' }
  },
  {
    path: '/records',
    name: 'Records',
    component: Records,
    meta: { title: '过滤记录' }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics,
    meta: { title: '统计分析' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫，设置页面标题
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 敏感词过滤系统` : '敏感词过滤系统'
  next()
})

export default router 