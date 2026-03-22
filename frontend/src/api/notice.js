import request from '@/utils/request'

export function getManagePage(params) {
  return request({
    url: '/api/busi/notice/manage/page',
    method: 'get',
    params
  })
}

export function publishNotice(data) {
  return request({
    url: '/api/busi/notice',
    method: 'post',
    data
  })
}

export function getNoticeDetail(id) {
  return request({
    url: `/api/busi/notice/${id}`,
    method: 'get'
  })
}

export function getNoticeReceipts(id) {
  return request({
    url: `/api/busi/notice/${id}/receipts`,
    method: 'get'
  })
}

export function getViewPage(params) {
  return request({
    url: '/api/busi/notice/view/page',
    method: 'get',
    params
  })
}

export function confirmNotice(id) {
  return request({
    url: `/api/busi/notice/${id}/confirm`,
    method: 'post'
  })
}