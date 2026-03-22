# HTTP 请求封装

> **文档版本**：1.0.0
> **更新时间**：2026-03-15

---

## 一、模块概述

封装 Axios 实例，实现统一的请求和响应处理机制。

---

## 二、代码文件路径

### 前端代码

| 文件 | 路径 | 说明 |
|------|------|------|
| request.js | [frontend/src/utils/request.js](frontend/src/utils/request.js) | Axios封装 |
| main.js | [frontend/src/main.js](frontend/src/main.js) | 应用入口 |

---

## 三、技术实现

### 3.1 请求拦截器

- 自动在请求头添加 `Authorization: Token`
- 处理请求异常

### 3.2 响应拦截器

- 统一处理响应结构 `Result<T>`
- 401 状态码：Token 过期，清除本地存储并跳转登录
- 其他错误：弹出错误提示

### 3.3 核心代码

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('kds_token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message || '操作失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('kds_token')
        router.push('/login')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
```

---

## 四、使用方式

### 4.1 导入

```javascript
import request from '@/utils/request'
```

### 4.2 GET 请求

```javascript
request({
  url: '/system/user/page',
  method: 'get',
  params: { page: 1, size: 10 }
})
```

### 4.3 POST 请求

```javascript
request({
  url: '/system/user/add',
  method: 'post',
  data: { realName: '张三', username: '13800138000' }
})
```

### 4.4 简写方式

```javascript
request.get('/system/user/page', { params: { page: 1 } })
request.post('/system/user/add', { realName: '张三' })
```

---

## 五、统一响应结构

```typescript
interface Result<T> {
  code: number      // 状态码
  message: string   // 消息
  data: T          // 数据
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 错误响应

```json
{
  "code": 500,
  "message": "错误描述",
  "data": null
}
```

---

## 六、错误处理

### 6.1 状态码处理

| 状态码 | 处理方式 |
|--------|----------|
| 200 | 正常返回数据 |
| 400 | 弹出错误提示 |
| 401 | 清除Token，跳转登录页 |
| 403 | 弹出无权限提示 |
| 500 | 弹出服务器错误提示 |

### 6.2 全局错误提示

使用 Element Plus 的 `ElMessage` 组件显示错误信息：

```javascript
import { ElMessage } from 'element-plus'

ElMessage.error('操作失败')
ElMessage.success('操作成功')
ElMessage.warning('警告信息')
ElMessage.info('提示信息')
```

---

## 七、配置说明

### 7.1 Axios 配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `baseURL` | 请求基础URL | `/api` |
| `timeout` | 请求超时时间 | 30000ms |
| `headers` | 请求头 | - |
| `params` | URL参数 | - |
| `data` | 请求体数据 | - |

### 7.2 自定义请求头

```javascript
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('kds_token')
    config.headers['Authorization'] = token
    config.headers['Content-Type'] = 'application/json'
    return config
  }
)
```

---

*文档生成时间：2026-03-15*
