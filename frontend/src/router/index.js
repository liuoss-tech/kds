import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录 - KDS家校系统' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/Message.vue'),
        meta: { title: '私信' }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '账号管理' }
      },
      {
        path: 'system/role',
        name: 'RoleManagement',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'busi/class',
        name: 'ClassManagement',
        component: () => import('@/views/busi/Class.vue'),
        meta: { title: '班级管理' }
      },
      {
        path: 'busi/student',
        name: 'StudentManagement',
        component: () => import('@/views/busi/Student.vue'),
        meta: { title: '幼儿管理' }
      },
      {
        path: 'busi/leave/parent',
        name: 'LeaveParent',
        component: () => import('@/views/busi/leave/LeaveParent.vue'),
        meta: { title: '家长请假' }
      },
      {
        path: 'busi/leave/teacher',
        name: 'LeaveTeacher',
        component: () => import('@/views/busi/leave/LeaveTeacher.vue'),
        meta: { title: '请假审批' }
      },
      {
        path: 'busi/leave/admin',
        name: 'LeaveAdmin',
        component: () => import('@/views/busi/leave/LeaveAdmin.vue'),
        meta: { title: '请假管理' }
      },
      {
        path: 'busi/attendance',
        name: 'Attendance',
        component: () => import('@/views/busi/Attendance.vue'),
        meta: { title: '考勤管理' }
      },
      {
        path: 'busi/attendance-stat',
        name: 'AttendanceStat',
        component: () => import('@/views/busi/AttendanceStat.vue'),
        meta: { title: '考勤情况统计' }
      },
      {
        path: 'busi/activity/teacher/list',
        name: 'TeacherActivityList',
        component: () => import('@/views/busi/activity/TeacherActivityList.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'busi/activity/teacher/form',
        name: 'TeacherActivityForm',
        component: () => import('@/views/busi/activity/TeacherActivityForm.vue'),
        meta: { title: '发布活动' }
      },
      {
        path: 'busi/activity/teacher/detail/:id',
        name: 'TeacherActivityDetail',
        component: () => import('@/views/busi/activity/TeacherActivityDetail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'busi/activity/parent/list',
        name: 'ParentActivityList',
        component: () => import('@/views/busi/activity/ParentActivityList.vue'),
        meta: { title: '活动列表' }
      },
      {
        path: 'busi/activity/parent/detail/:id',
        name: 'ParentActivityDetail',
        component: () => import('@/views/busi/activity/ParentActivityDetail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'busi/life-record',
        name: 'LifeRecord',
        component: () => import('@/views/busi/LifeRecord.vue'),
        meta: { title: '生活记录' }
      },
      {
        path: 'busi/life-record/parent',
        name: 'ParentLifeRecord',
        component: () => import('@/views/busi/life-record/ParentLifeRecord.vue'),
        meta: { title: '生活记录查看' }
      },
      {
        path: 'busi/recipe',
        name: 'Recipe',
        component: () => import('@/views/busi/Recipe.vue'),
        meta: { title: '食谱管理' }
      },
      {
        path: 'busi/recipe/parent',
        name: 'ParentRecipe',
        component: () => import('@/views/busi/ParentRecipe.vue'),
        meta: { title: '食谱查看' }
      },
      {
        path: 'busi/notice/manage/list',
        name: 'NoticeManageList',
        component: () => import('@/views/busi/notice/NoticeManageList.vue'),
        meta: { title: '通知管理' }
      },
      {
        path: 'busi/notice/manage/form',
        name: 'NoticeForm',
        component: () => import('@/views/busi/notice/NoticeForm.vue'),
        meta: { title: '发布通知' }
      },
      {
        path: 'busi/notice/manage/detail/:id',
        name: 'NoticeReceiptDetail',
        component: () => import('@/views/busi/notice/NoticeReceiptDetail.vue'),
        meta: { title: '通知详情' }
      },
      {
        path: 'busi/notice/view/list',
        name: 'NoticeViewList',
        component: () => import('@/views/busi/notice/NoticeViewList.vue'),
        meta: { title: '通知列表' }
      },
      {
        path: 'busi/notice/view/detail/:id',
        name: 'NoticeViewDetail',
        component: () => import('@/views/busi/notice/NoticeViewDetail.vue'),
        meta: { title: '通知详情' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }

  const token = localStorage.getItem('kds_token')

  if (to.path === '/login') {
    if (token) {
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
