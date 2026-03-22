import request from '@/utils/request'

export function getTeacherActivityList(params) {
  return request({
    url: '/api/busi/activity/teacher/list',
    method: 'get',
    params
  })
}

export function getParentActivityList(params) {
  return request({
    url: '/api/busi/activity/parent/list',
    method: 'get',
    params
  })
}

export function getActivityDetail(id) {
  return request({
    url: `/api/busi/activity/${id}`,
    method: 'get'
  })
}

export function publishActivity(data) {
  return request({
    url: '/api/busi/activity',
    method: 'post',
    data
  })
}

export function getActivityRegistrations(id) {
  return request({
    url: `/api/busi/activity/${id}/registrations`,
    method: 'get'
  })
}

export function registerActivity(data) {
  return request({
    url: '/api/busi/activity/registration',
    method: 'post',
    params: {
      activityId: data.activityId,
      studentId: data.studentId,
      remark: data.remark
    }
  })
}

export function cancelRegistration(id) {
  return request({
    url: `/api/busi/activity/registration/${id}/cancel`,
    method: 'put'
  })
}

export function getAvailableStudents(id) {
  return request({
    url: `/api/busi/activity/${id}/available-students`,
    method: 'get'
  })
}