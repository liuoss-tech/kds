import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '../router/index.js';

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
});

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('kds_token');
    if (token) {
      config.headers['Authorization'] = token;
    }
    return config;
  },
  error => {
    console.error('请求发送异常:', error);
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  response => {
    const res = response.data;

    if (res.code === 200) {
      return res;
    } else if (res.code === 401) {
      ElMessage.warning('登录状态已过期，请重新登录');
      localStorage.removeItem('kds_token');
      localStorage.removeItem('kds_role');
      localStorage.removeItem('kds_realName');
      localStorage.removeItem('kds_userId');
      localStorage.removeItem('kds_userType');
      router.push('/login');
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      ElMessage.error(res.message || '系统繁忙，请稍后再试');
      return Promise.reject(new Error(res.message || 'Error'));
    }
  },
  error => {
    console.error('接口请求失败:', error);
    ElMessage.error('网络异常或服务器无响应，请重试');
    return Promise.reject(error);
  }
);

export default service;
