import { createStore } from 'vuex'
import api from '../utils/api'

export default createStore({
  state: {
    sensitiveWords: [],
    filterRecords: [],
    statistics: {},
    loading: false,
    filterInputText: '', // 保存文本过滤页面的输入内容
    quickFilterText: '' // 保存首页快速过滤的输入内容
  },
  
  mutations: {
    SET_SENSITIVE_WORDS(state, words) {
      state.sensitiveWords = words
    },
    SET_FILTER_RECORDS(state, records) {
      state.filterRecords = records
    },
    SET_STATISTICS(state, statistics) {
      state.statistics = statistics
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    SET_FILTER_INPUT_TEXT(state, text) {
      state.filterInputText = text
    },
    SET_QUICK_FILTER_TEXT(state, text) {
      state.quickFilterText = text
    }
  },
  
  actions: {
    // 获取敏感词列表
    async fetchSensitiveWords({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await api.get('/sensitive-words/enabled')
        if (response.data.code === 200) {
          commit('SET_SENSITIVE_WORDS', response.data.data)
        }
      } catch (error) {
        console.error('获取敏感词列表失败:', error)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    // 获取过滤记录
    async fetchFilterRecords({ commit }, { page = 1, size = 10 } = {}) {
      commit('SET_LOADING', true)
      try {
        const response = await api.get('/filter/records', {
          params: { page, size }
        })
        if (response.data.code === 200) {
          commit('SET_FILTER_RECORDS', response.data.data.list)
        }
      } catch (error) {
        console.error('获取过滤记录失败:', error)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    // 过滤文本
    async filterText({ commit }, text) {
      commit('SET_LOADING', true)
      try {
        const response = await api.post('/filter/text', { text })
        return response.data
      } catch (error) {
        console.error('文本过滤失败:', error)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    // 检测文本
    async checkText({ commit }, text) {
      try {
        const response = await api.post('/filter/check', { text })
        return response.data
      } catch (error) {
        console.error('文本检测失败:', error)
        throw error
      }
    }
  },
  
  getters: {
    sensitiveWordsCount: state => state.sensitiveWords.length,
    filterRecordsCount: state => state.filterRecords.length,
    isLoading: state => state.loading,
    getFilterInputText: state => state.filterInputText,
    getQuickFilterText: state => state.quickFilterText
  }
}) 