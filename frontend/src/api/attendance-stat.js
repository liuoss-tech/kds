import request from '@/utils/request'

export function getAttendanceOverview(params) {
  return request({
    url: '/api/busi/attendance/stat/overview',
    method: 'get',
    params
  })
}

export function getAttendanceClassRateList(params) {
  return request({
    url: '/api/busi/attendance/stat/class-rate',
    method: 'get',
    params
  })
}

export function getAttendanceLeaveStat(params) {
  return request({
    url: '/api/busi/attendance/stat/leave',
    method: 'get',
    params
  })
}

export function getAttendanceTrend(params) {
  return request({
    url: '/api/busi/attendance/stat/trend',
    method: 'get',
    params
  })
}